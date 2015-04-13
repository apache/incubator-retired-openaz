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
package com.att.research.xacmlatt.pdp.policy;

import java.util.Properties;

import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.FactoryFinder;
import com.att.research.xacmlatt.pdp.util.ATTPDPProperties;

/**
 * PolicyFinderFactory provides methods for loading XACML 3.0 policies and policy sets that are used
 * by the {@link com.att.research.xacmlatt.pdp.PDPEngine} to evaluate requests.
 * 
 * @author car
 * @version $Revision: 1.3 $
 */
public abstract class PolicyFinderFactory {
	private static final String	FACTORYID					= ATTPDPProperties.PROP_POLICYFINDERFACTORY;
	private static final String DEFAULT_FACTORY_CLASSNAME	= "com.att.research.xacmlatt.pdp.std.StdPolicyFinderFactory";
	
	protected PolicyFinderFactory() {
	}
	
	protected PolicyFinderFactory(Properties properties) {
	}
	
	public static PolicyFinderFactory newInstance() throws FactoryException {
		return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PolicyFinderFactory.class);
	}
	
	public static PolicyFinderFactory newInstance(Properties properties) throws FactoryException {
		return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PolicyFinderFactory.class, properties);
	}
	
	public static PolicyFinderFactory newInstance(String className, ClassLoader classLoader) throws FactoryException {
		return FactoryFinder.newInstance(className, PolicyFinderFactory.class, classLoader, false);
	}
	
	public static PolicyFinderFactory newInstance(String className) throws FactoryException {
		return FactoryFinder.newInstance(className, PolicyFinderFactory.class, null, true);
	}

	/**
	 * Gets the configured {@link PolicyFinder}.
	 * 
	 * @return the configured <code>PolicyFinder</code>
	 */
	abstract public PolicyFinder getPolicyFinder() throws FactoryException;

	/**
	 * Gets the configured {@link PolicyFinder}.
	 * 
	 * @return the configured <code>PolicyFinder</code>
	 */
	abstract public PolicyFinder getPolicyFinder(Properties properties) throws FactoryException;
}
