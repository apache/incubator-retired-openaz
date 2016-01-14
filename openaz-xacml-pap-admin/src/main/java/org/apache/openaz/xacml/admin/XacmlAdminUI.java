/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.openaz.xacml.admin;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import org.apache.openaz.xacml.admin.XacmlAdminAuthorization.AdminAction;
import org.apache.openaz.xacml.admin.XacmlAdminAuthorization.AdminResource;
import org.apache.openaz.xacml.admin.converters.XacmlConverterFactory;
import org.apache.openaz.xacml.admin.jpa.Attribute;
import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.ConstraintType;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.jpa.FunctionArgument;
import org.apache.openaz.xacml.admin.jpa.FunctionDefinition;
import org.apache.openaz.xacml.admin.jpa.Obadvice;
import org.apache.openaz.xacml.admin.jpa.ObadviceExpression;
import org.apache.openaz.xacml.admin.jpa.PIPConfiguration;
import org.apache.openaz.xacml.admin.jpa.PIPResolver;
import org.apache.openaz.xacml.admin.jpa.PIPType;
import org.apache.openaz.xacml.admin.jpa.PolicyAlgorithms;
import org.apache.openaz.xacml.admin.jpa.RuleAlgorithms;
import org.apache.openaz.xacml.admin.model.MatchFunctionQueryDelegate;
import org.apache.openaz.xacml.admin.util.RESTfulPAPEngine;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.rest.XACMLRestProperties;
import org.apache.openaz.xacml.util.XACMLProperties;
import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.google.gwt.thirdparty.guava.common.base.Splitter;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingLocalEntityProvider;
import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.UI;

@Push
@SuppressWarnings("serial")
@Theme("xacml_pap_admin")
public class XacmlAdminUI extends UI implements PAPNotificationBroadcaster.PAPNotificationBroadcastListener {
	//
	// All static declarations
	//
	public static final String PERSISTENCE_UNIT = "XACML-PAP-ADMIN";
	private static Log logger	= LogFactory.getLog(XacmlAdminUI.class); //NOPMD
	
	/*
	 * These objects are shared amongst sessions.
	 */
	private static Path repositoryPath;
	private static Repository repository;
	private static EntityManagerFactory emf;
	private static JDBCConnectionPool pool;
	
	/*
	 * These objects are created each session.
	 */
	private Path workspacePath;
	private Path gitPath;
	//
	// Our Persistence Fields. For general use. NOTE: Be careful applying
	// filters to these container objects. If one window applies a filter, then
	// when another window uses the object, that filter will show up and cause confusion.
	// If filters are needed within a window, then create another instance instead of
	// using one of these pointers.
	//
	private EntityManager em;
	private JPAContainer<Attribute> 			attributes;
	private JPAContainer<ConstraintType> 		constraintTypes;
	private JPAContainer<Obadvice> 				obadvice;
	private JPAContainer<ObadviceExpression> 	obadviceExpressions;
	private JPAContainer<Category> 				categories;
	private JPAContainer<Datatype> 				datatypes;
	private JPAContainer<PolicyAlgorithms> 		policyAlgorithms;
	private JPAContainer<RuleAlgorithms> 		ruleAlgorithms;
	private JPAContainer<PIPConfiguration>		pipConfigurations;
	private JPAContainer<PIPResolver>			pipResolvers;
	private JPAContainer<PIPType>				pipTypes;
	private JPAContainer<FunctionDefinition>	functionDefinitions;
	private JPAContainer<FunctionArgument>		functionArguments;
	private SQLContainer matchFunctionContainer;
	private SQLContainer higherorderBagContainer;
	//
	// Our authorization object
	//
	XacmlAdminAuthorization authorizer = new XacmlAdminAuthorization();
	//
	// The PAP Engine
	//
	private PAPEngine papEngine;
	//
	// GUI navigation
	//
	private Navigator navigator = null;
	private XacmlAdminConsole console = null;
	//
	// Vaadin Init
	//
	@Override
	protected void init(VaadinRequest request) {
		//
		// Set our title
		//
		this.getPage().setTitle("Apache OpenAZ Admin Console");
		//
		// Create our authorization object
		//
		this.authorizer = new XacmlAdminAuthorization();
		//
		// Is the user authorized to use the application?
		//
		if (this.authorizer.isAuthorized(this.getUserid(), 
									XacmlAdminAuthorization.AdminAction.ACTION_ACCESS, 
									XacmlAdminAuthorization.AdminResource.RESOURCE_APPLICATION) == false) {
			logger.error("user " + this.getUserid() + " is not authorized.");
			//
			// Create a navigator to manage all our views
			//
			this.navigator = new Navigator(this, this);
			//
			// Redirect to an error page
			//
			this.navigator.addView(XacmlErrorHandler.VIEWNAME, new XacmlErrorHandler("User " + this.getUserid() + " is not authorized to access application", null));
			this.navigator.navigateTo(XacmlErrorHandler.VIEWNAME);
			return;
		}
		try {
			//
			// Initialize user's Git repository
			//
			this.workspacePath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_WORKSPACE), this.getUserid());
			this.gitPath = XacmlAdminUI.initializeUserRepository(this.workspacePath, this.getUserid(), this.getUserEmail());
		} catch (Exception e) {
			logger.error("Git Setup failure", e);
			//
			// Create a navigator to manage all our views
			//
			this.navigator = new Navigator(this, this);
			//
			// Redirect to an error page
			//
			this.navigator.addView(XacmlErrorHandler.VIEWNAME, new XacmlErrorHandler(e.getMessage(), null));
			this.navigator.navigateTo(XacmlErrorHandler.VIEWNAME);
			return;
		}
		//
		// Create a navigator to manage all our views
		//
		this.navigator = new Navigator(this, this);
		//
		// Set our converter factory
		//
		this.getSession().setConverterFactory(new XacmlConverterFactory());
		//
		// Initialize our data objects
		//
		try {
			//
			// Initialize JPA and SQL. Create our custom entity manager.
			//
			logger.info("Creating Persistence Entity Manager");
			//
			// Now create the entity manager. This is used throughout the application to create JPA
			// containers of the entities located in the database.
			//
			this.em = XacmlAdminUI.emf.createEntityManager();
			//
			// Our Read-Only containers
			//
			logger.info("Creating JPA read-only containers");
			this.constraintTypes = new JPAContainer<ConstraintType>(ConstraintType.class);
			this.constraintTypes.setEntityProvider(new CachingLocalEntityProvider<ConstraintType>(ConstraintType.class, this.em));
			
			this.categories = new JPAContainer<Category>(Category.class);
			this.categories.setEntityProvider(new CachingLocalEntityProvider<Category>(Category.class, this.em));

			this.datatypes = new JPAContainer<Datatype>(Datatype.class);
			this.datatypes.setEntityProvider(new CachingLocalEntityProvider<Datatype>(Datatype.class, this.em));
			
			this.policyAlgorithms = new JPAContainer<PolicyAlgorithms>(PolicyAlgorithms.class);
			this.policyAlgorithms.setEntityProvider(new CachingLocalEntityProvider<PolicyAlgorithms>(PolicyAlgorithms.class, this.em));
			
			this.ruleAlgorithms = new JPAContainer<RuleAlgorithms>(RuleAlgorithms.class);
			this.ruleAlgorithms.setEntityProvider(new CachingLocalEntityProvider<RuleAlgorithms>(RuleAlgorithms.class, this.em));
			
			this.pipTypes = new JPAContainer<PIPType>(PIPType.class);
			this.pipTypes.setEntityProvider(new CachingLocalEntityProvider<PIPType>(PIPType.class, this.em));
			
			this.functionDefinitions = new JPAContainer<FunctionDefinition>(FunctionDefinition.class);
			this.functionDefinitions.setEntityProvider(new CachingLocalEntityProvider<FunctionDefinition>(FunctionDefinition.class, this.em));

			this.functionArguments = new JPAContainer<FunctionArgument>(FunctionArgument.class);
			this.functionArguments.setEntityProvider(new CachingLocalEntityProvider<FunctionArgument>(FunctionArgument.class, this.em));
			//
			// Our writable containers. NOTE: The dictionaries have their own JPA instance since they can
			// apply filters to their table views. If you update these, then refresh the dictionary containers
			// by calling the appropriate refresh method defined in XacmlAdminUI.
			//
			logger.info("Creating JPA writable containers");
			this.attributes = new JPAContainer<Attribute>(Attribute.class);
			this.attributes.setEntityProvider(new CachingMutableLocalEntityProvider<Attribute>(Attribute.class, this.em));

			this.obadvice = new JPAContainer<Obadvice>(Obadvice.class);
			this.obadvice.setEntityProvider(new CachingMutableLocalEntityProvider<Obadvice>(Obadvice.class, this.em));

			this.obadviceExpressions = new JPAContainer<ObadviceExpression>(ObadviceExpression.class);
			this.obadviceExpressions.setEntityProvider(new CachingMutableLocalEntityProvider<ObadviceExpression>(ObadviceExpression.class, this.em));

			this.pipConfigurations = new JPAContainer<PIPConfiguration>(PIPConfiguration.class);
			this.pipConfigurations.setEntityProvider(new CachingMutableLocalEntityProvider<PIPConfiguration>(PIPConfiguration.class, this.em));
			
			this.pipResolvers = new JPAContainer<PIPResolver>(PIPResolver.class);
			this.pipResolvers.setEntityProvider(new CachingMutableLocalEntityProvider<PIPResolver>(PIPResolver.class, this.em));
			//
			// Sort our persistence data
			//
			logger.info("Sorting containers");
			this.categories.sort(new String[]{"xacmlId"}, new boolean[]{true});
			this.datatypes.sort(new String[]{"xacmlId"}, new boolean[]{true});
			this.policyAlgorithms.sort(new String[]{"xacmlId"}, new boolean[]{true});
			this.ruleAlgorithms.sort(new String[]{"xacmlId"}, new boolean[]{true});
			this.functionDefinitions.sort(new String[]{"xacmlid"}, new boolean[]{true});
			//this.functionArguments.sort(new String[]{"datatypeBean"}, new boolean[]{true});
			//
			// Create our special query for MatchType functions. We need a custom
			// QueryDelegate because these functions are accessible via a View (vs a Table).
			// The basic FreeformQuery does not work with filters on a View (via Vaadin).
			//
			// TODO: Consider putting this into a couple of Map's. Doing so would speed up
			// access. However, if we want to support custom functions, then there needs to
			// be a way for those custom functions to get into the Map. This is why a database
			// is being used to store ALL the functions, both standard and custom.
			//
			logger.info("Creating SQL Queries");
			MatchFunctionQueryDelegate delegate = new MatchFunctionQueryDelegate();
			FreeformQuery query = new FreeformQuery("SELECT * FROM match_functions", XacmlAdminUI.pool, new String[] {});
			query.setDelegate(delegate);
			this.matchFunctionContainer = new SQLContainer(query);
			//
			// Same for this one
			//
			delegate = new MatchFunctionQueryDelegate();
			query = new FreeformQuery("SELECT * FROM higherorder_bag_functions", XacmlAdminUI.pool, new String[] {});
			query.setDelegate(delegate);
			this.higherorderBagContainer = new SQLContainer(query);
			//
			// Load our PAP engine
			//
			logger.info("Creating PAP engine");
			String myRequestURL = VaadinServletService.getCurrentServletRequest().getRequestURL().toString();
			try {
				//
				// Set the URL for the RESTful PAP Engine
				//
				papEngine = new RESTfulPAPEngine(myRequestURL);
			} catch (PAPException e  ) {
				logger.error("Failed to create PAP engine", e);
			} catch (Exception e) {
				logger.error("Failed to create PAP engine", e);
			}
			logger.info("done creating connections");
		} catch(Exception e) {
			//
			// Redirect to an error page
			//
			logger.error(e);
			e.printStackTrace();
			this.navigator.addView("", new XacmlErrorHandler(e.getMessage(), null));
			this.navigator.navigateTo("");
			return;
		}
		logger.info("Creating main layout");
		//
		// Create our main component layout
		//
		this.console = new XacmlAdminConsole();
		this.navigator.addView("", console);
		this.navigator.setErrorView(new XacmlErrorHandler(null, null));
		//
		// Navigate to our view
		//
		this.navigator.navigateTo("");
		//
		// Register to receive PAP change notifications broadcasts
		//
		PAPNotificationBroadcaster.register(this);
	}
	
	public static void servletInit() throws ServletException {
		//
		// Initialize GIT repository.
		//
		XacmlAdminUI.initializeGitRepository();
		//
		// Initialize Entity Factory
		//
		XacmlAdminUI.initializeEntityFactory();
		//
		// If we get here, then the configuration information
		// seems ok.
		//
	}

	public static void servletDestroy() {
		if (XacmlAdminUI.repository != null) {
			XacmlAdminUI.repository.close();
		}
	}
	
	/**
	 * An Update Notification has arrived from the PAP.
	 * Tell the Vaadin users to change their data.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void doPAPNotification(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//
			// Notify all user instances to update groups
			//
			PAPNotificationBroadcaster.updateAllGroups();
		} catch (Exception e) {
			logger.error("Unable to process PAP request: "+e, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	// Must unregister when the UI expires
	@Override
	public void detach() {
		PAPNotificationBroadcaster.unregister(this);
		super.detach();
	}
	
	/**
	 * This will initialize the JPA Entity Manager Factory. This will determine if
	 * the database connection settings are correct.
	 * 
	 * @throws ServletException
	 */
	private static void initializeEntityFactory() throws ServletException {
		logger.info("intializing Persistence Entity Factory");
		//
		// Pull custom persistence settings
		//
		Properties properties;
		try {
			properties = XACMLProperties.getProperties();
		} catch (IOException e) {
			throw new ServletException(e.getMessage(), e.getCause());
		}
		//
		// Create the factory
		//
		emf = Persistence.createEntityManagerFactory(XacmlAdminUI.PERSISTENCE_UNIT, properties);
		//
		// Did it get created?
		//
		if (emf == null) {
			throw new ServletException("Unable to create Entity Manager Factory");
		}
		//
		// Create our JDBC connection pool
		//
		try {
			logger.info("intializing JDBC Connection Pool");
			XacmlAdminUI.pool = new XacmlJDBCConnectionPool(
					properties.getProperty(PersistenceUnitProperties.JDBC_DRIVER),
					properties.getProperty(PersistenceUnitProperties.JDBC_URL),
					properties.getProperty(PersistenceUnitProperties.JDBC_USER),
					properties.getProperty(PersistenceUnitProperties.JDBC_PASSWORD));
		} catch (SQLException e) {
			throw new ServletException(e.getMessage(), e.getCause());
		}
	}

	private static void initializeGitRepository() throws ServletException {
		XacmlAdminUI.repositoryPath = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_REPOSITORY));
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			XacmlAdminUI.repository = builder.setGitDir(XacmlAdminUI.repositoryPath.toFile()).readEnvironment().findGitDir().setBare().build();
			if (Files.notExists(XacmlAdminUI.repositoryPath) || Files.notExists(Paths.get(XacmlAdminUI.repositoryPath.toString(), "HEAD"))) {
				//
				// Create it if it doesn't exist. As a bare repository
				//
				logger.info("Creating bare git repository: " + XacmlAdminUI.repositoryPath.toString());
				XacmlAdminUI.repository.create();
				//
				// Add the magic file so remote works.
				//
				Path daemon = Paths.get(XacmlAdminUI.repositoryPath.toString(), "git-daemon-export-ok");
				Files.createFile(daemon);					
			}
		} catch (IOException e) {
			logger.error("Failed to build repository: " + repository, e);
			throw new ServletException(e.getMessage(), e.getCause());
		}
		//
		// Make sure the workspace directory is created
		//
		Path workspace = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_WORKSPACE));
		workspace = workspace.toAbsolutePath();
		if (Files.notExists(workspace)) {
			try {
				Files.createDirectory(workspace);
			} catch (IOException e) {
				logger.error("Failed to build workspace: " + workspace, e);
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Create the user workspace directory
		//
		workspace = Paths.get(workspace.toString(), "pe");
		if (Files.notExists(workspace)) {
			try {
				Files.createDirectory(workspace);
			} catch (IOException e) {
				logger.error("Failed to create directory: " + workspace, e);
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Get the path to where the repository is going to be
		//
		Path gitPath = Paths.get(workspace.toString(), XacmlAdminUI.repositoryPath.getFileName().toString());
		if (Files.notExists(gitPath)) {
			try {
				Files.createDirectory(gitPath);
			} catch (IOException e) {
				logger.error("Failed to create directory: " + gitPath, e);
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		//
		// Initialize the domain structure
		//
		String base = null;
		String domain = XacmlAdminUI.getDomain();
		if (domain != null) {
			for (String part : Splitter.on(':').trimResults().split(domain)) {
				if (base == null) {
					base = part;
				}
				Path subdir = Paths.get(gitPath.toString(), part);
				if (Files.notExists(subdir)) {
					try {
						Files.createDirectory(subdir);
						Files.createFile(Paths.get(subdir.toString(), ".svnignore"));
					} catch (IOException e) {
						logger.error("Failed to create: " + subdir, e);
						throw new ServletException(e.getMessage(), e.getCause());
					}
				}
			}
		} else {
			try {
				Files.createFile(Paths.get(workspace.toString(), ".svnignore"));
				base = ".svnignore";
			} catch (IOException e) {
				logger.error("Failed to create file", e);
				throw new ServletException(e.getMessage(), e.getCause());
			}
		}
		try {
			//
			// These are the sequence of commands that must be done initially to
			// finish setting up the remote bare repository.
			//
			Git git = Git.init().setDirectory(gitPath.toFile()).setBare(false).call();
			git.add().addFilepattern(base).call();
			git.commit().setMessage("Initialize Bare Repository").call();
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", XacmlAdminUI.repositoryPath.toAbsolutePath().toString());
			config.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
			config.save();
			git.push().setRemote("origin").add("master").call();
			/*
			 * This will not work unless git.push().setRemote("origin").add("master").call();
			 * is called first. Otherwise it throws an exception. However, if the push() is
			 * called then calling this function seems to add nothing.
			 * 
			git.branchCreate().setName("master")
				.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
				.setStartPoint("origin/master").setForce(true).call();
			*/
		} catch (GitAPIException | IOException e) {
			logger.error(e);
			throw new ServletException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Initializes a user's git repository.
	 * 
	 * 
	 * @param workspacePath
	 * @param userId
	 * @param email
	 * @return
	 * @throws IOException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	private static Path initializeUserRepository(Path workspacePath, String userId, URI email) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		Path gitPath = null;
		//
		// Initialize the User's Git repository
		//
		if (Files.notExists(workspacePath)) {
			logger.info("Creating user workspace: " + workspacePath.toAbsolutePath().toString());
			//
			// Create our user's directory
			//
			Files.createDirectory(workspacePath);
		}
		gitPath = Paths.get(workspacePath.toString(), XacmlAdminUI.repositoryPath.getFileName().toString());
		if (Files.notExists(gitPath)) {
			//
			// It doesn't exist yet, so Clone it and check it out
			//
			logger.info("Cloning user git directory: " + gitPath.toAbsolutePath().toString());
			Git.cloneRepository().setURI(XacmlAdminUI.repositoryPath.toUri().toString())
							.setDirectory(gitPath.toFile())
							.setNoCheckout(false)
							.call();
			//
			// Set userid
			//
			Git git = Git.open(gitPath.toFile());
			StoredConfig config = git.getRepository().getConfig();
			config.setString("user", null, "name", userId);
			if (email != null && email.getPath() != null) {
				config.setString("user", null, "email", email.toString());
			}
			config.save();
		}
		return gitPath;
	}


	public static String	getDomain() {
		return XACMLProperties.getProperty(XACMLRestProperties.PROP_ADMIN_DOMAIN, "urn");
	}
	
	public static JDBCConnectionPool	getConnectionPool() {
		return pool;
	}
	
	public SQLContainer	getMatchFunctionContainer() {
		return this.matchFunctionContainer;
	}
	
	public SQLContainer getHigherOrderBagContainer() {
		return this.higherorderBagContainer;
	}
	
	public EntityManager getEntityManager() {
		return this.em;
	}
	
	public JPAContainer<Attribute>	getAttributes() {
		return this.attributes;
	}
	
	public void refreshAttributes() {
		this.attributes.refresh();
		this.console.refreshAttributes();
	}

	public JPAContainer<ConstraintType>	getConstraintTypes() {
		return this.constraintTypes;
	}
	
	public JPAContainer<Category>	getCategories() {
		return this.categories;
	}
	
	public JPAContainer<Datatype>	getDatatypes() {
		return this.datatypes;
	}

	public JPAContainer<PolicyAlgorithms> getPolicyAlgorithms() {
		return this.policyAlgorithms;
	}
	
	public JPAContainer<RuleAlgorithms> getRuleAlgorithms() {
		return this.ruleAlgorithms;
	}

	public JPAContainer<Obadvice> getObadvice() {
		return this.obadvice;
	}

	public JPAContainer<ObadviceExpression> getObadviceExpressions() {
		return this.obadviceExpressions;
	}
	
	public void refreshObadvice() {
		this.obadvice.refresh();
		this.obadviceExpressions.refresh();
		this.console.refreshObadvice();
	}

	public JPAContainer<FunctionDefinition>	getFunctionDefinitions() {
		return this.functionDefinitions;
	}
	
	public JPAContainer<FunctionArgument> getFunctionArguments() {
		return this.functionArguments;
	}
	
	public JPAContainer<PIPConfiguration> getPIPConfigurations() {
		return this.pipConfigurations;
	}
	
	public JPAContainer<PIPResolver>	getPIPResolvers() {
		return this.pipResolvers;
	}
	
	public JPAContainer<PIPType>	getPIPTypes() {
		return this.pipTypes;
	}

	public void refreshPIPConfiguration() {
		this.pipConfigurations.refresh();
		this.console.refreshPIPConfiguration();
	}
	
	public Category	getDefaultCategory() throws Exception {
		for (Object id : categories.getItemIds()) {
			Category cat = categories.getItem(id).getEntity();
			if (cat.getIdentifer().equals(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT)) {
				return cat;
			}
		}
		throw new Exception("There is no default category.");
	}
	
	public Datatype getDefaultDatatype() throws Exception {
		for (Object id: this.datatypes.getItemIds()) {
			Datatype dt = this.datatypes.getItem(id).getEntity();
			if (dt.getIdentifer().equals(XACML3.ID_DATATYPE_STRING)) {
				return dt;
			}
		}
		throw new Exception("There is no default datatype.");
	}
	
	public XacmlAdminAuthorization getAuthorizer() {
		return this.authorizer;
	}
	
	public boolean	isAuthorized(AdminAction action, AdminResource resource) {
		return this.authorizer.isAuthorized(this.getUserid(), action, resource);
	}
	
	public String	getUserid() {
		Object id = this.getSession().getSession().getAttribute("xacml.rest.admin.user.id");
		if (id == null) {
			return XACMLProperties.getProperty("xacml.rest.admin.user.id", "guest");
		}
		String str = id.toString();
		if (str == null || str.isEmpty()) {
			return "guest";
		}
		return str;
	}
	
	public String 	getUserName() {
		Object id = this.getSession().getSession().getAttribute("xacml.rest.admin.user.name");
		if (id == null) {
			return XACMLProperties.getProperty("xacml.rest.admin.user.name", "guest");
		}
		String str = id.toString();
		if (str == null || str.isEmpty()) {
			return "guest";
		}
		return str;
	}
	
	public URI		getUserEmail() {
		Object id = this.getSession().getSession().getAttribute("xacml.rest.admin.user.email");
		if (id == null) {
			return URI.create(XACMLProperties.getProperty("xacml.rest.admin.user.email", "guest"));
		}
		String str = id.toString();
		if (str == null || str.isEmpty()) {
			return null;
		}
		return URI.create(str);
	}
	
	public Path		getUserWorkspace() {
		return this.workspacePath;
	}
	
	public Path		getUserGitPath() {
		return this.gitPath;
	}
	
	public PAPEngine	getPAPEngine() {
		return this.papEngine;
	}
	
	public String	newPolicyID() {
		return Joiner.on(':').skipNulls().join((XacmlAdminUI.getDomain().startsWith("urn") ? null : "urn"),
												XacmlAdminUI.getDomain().replaceAll("[/\\\\.]", ":"), 
												"xacml", "policy", "id", UUID.randomUUID());
	}

	public String	newRuleID() {
		return Joiner.on(':').skipNulls().join((XacmlAdminUI.getDomain().startsWith("urn") ? null : "urn"),
												XacmlAdminUI.getDomain().replaceAll("[/\\\\.]", ":"), 
												"xacml", "rule", "id", UUID.randomUUID());
	}
	//
	// PAPNotificationBroadcaster Interface implementation
	//
	/**
	 * Got a notification that the PAP has changed the PDP data,
	 * so update ALL PDPGroups.
	 * This is called once for each Vaadin instance for each PAP change Notification.
	 */
	public void updateAllGroups() {
		access(new Runnable() {
			@Override
			public void run() {
				//
				// locking is needed to avoid race conditions.
				// Shows up as Exception: "A connector should not be marked as dirty while a response is being written."
				//
				getUI().getSession().lock();
				try {
					//
					// Tell the console to refresh its PDP group information
					//
					console.refreshPDPGroups();
				} finally {
					getUI().getSession().unlock();
				}
			}
		});
	}
}
