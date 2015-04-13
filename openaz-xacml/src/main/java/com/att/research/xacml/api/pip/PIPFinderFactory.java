/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.api.pip;

import java.util.Properties;

import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.FactoryFinder;
import com.att.research.xacml.util.XACMLProperties;

/**
 * PIPFinderFactory is the factory class for getting the configured {@link PIPFinder}.
 * 
 * @author car
 * @version $Revision: 1.4 $
 */
public abstract class PIPFinderFactory {
	private static final String	FACTORYID					= XACMLProperties.PROP_PIPFINDERFACTORY;
	private static final String	DEFAULT_FACTORY_CLASSNAME	= "com.att.research.xacml.std.pip.StdPIPFinderFactory";
	
	protected Properties properties = null;
	
	/**
	 * Protected constructor so this class cannot be instantiated.
	 */
	protected PIPFinderFactory() {
	}

	/**
	 * Protected constructor so this class cannot be instantiated.
	 */
	protected PIPFinderFactory(Properties properties) {
		this.properties = properties;
	}

	/**
	 * Gets an instance of the configured <code>PIPFinderFactory</code> class.
	 * 
	 * @return the configured <code>PIPFinderFactory</code>
	 * @throws FactoryException if there is an error instantiating the factory
	 */
	public static PIPFinderFactory newInstance() throws FactoryException {
		return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PIPFinderFactory.class);
	}
	
	public static PIPFinderFactory newInstance(Properties properties) throws FactoryException {
		return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PIPFinderFactory.class, properties);
	}
	
	public static PIPFinderFactory newInstance(String factoryClassName, ClassLoader classLoader) throws FactoryException {
		return FactoryFinder.newInstance(factoryClassName, PIPFinderFactory.class, classLoader, false);
	}
	
	public static PIPFinderFactory newInstance(String factoryClassName) throws FactoryException {
		return FactoryFinder.newInstance(factoryClassName, PIPFinderFactory.class, null, true);
	}
	
	/**
	 * Gets an instance of the configured <code>PIPFinder</code> class.
	 * 
	 * @return an instance of the configured <code>PIPFinder</code>
	 */
	abstract public PIPFinder getFinder() throws PIPException ;

	/**
	 * Gets an instance of the configured <code>PIPFinder</code> class.
	 * 
	 * @return an instance of the configured <code>PIPFinder</code>
	 */
	abstract public PIPFinder getFinder(Properties properties) throws PIPException ;
}
