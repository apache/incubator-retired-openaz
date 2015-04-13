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
package com.att.research.xacmlatt.pdp.std;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacmlatt.pdp.policy.FunctionDefinition;
import com.att.research.xacmlatt.pdp.policy.FunctionDefinitionFactory;

/**
 * StdFunctionDefinitionFactory is the default {@link com.att.research.xacmlatt.pdp.policy.FunctionDefinitionFactory} implementation
 * used if no other <code>FunctionDefinitionFactory</code> implementation is supplied.  It contains all of the standard XACML 3.0
 * functions.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class StdFunctionDefinitionFactory extends FunctionDefinitionFactory {
	private static Map<Identifier,FunctionDefinition> 	mapFunctionDefinitions	= new HashMap<Identifier,FunctionDefinition>();
	private static boolean								needMapInit				= true;
	
	private static void register(FunctionDefinition functionDefinition) {
		mapFunctionDefinitions.put(functionDefinition.getId(), functionDefinition);
	}
		
	private static void initMap() {
		if (needMapInit) {
			synchronized(mapFunctionDefinitions) {
				if (needMapInit) {
					needMapInit	= false;
					Field[] declaredFields	= StdFunctions.class.getDeclaredFields();
					for (Field field : declaredFields) {
						if (Modifier.isStatic(field.getModifiers()) && 
							field.getName().startsWith(StdFunctions.FD_PREFIX) &&
							FunctionDefinition.class.isAssignableFrom(field.getType()) &&
							Modifier.isPublic(field.getModifiers())
						) {
							try {
								register((FunctionDefinition)(field.get(null)));
							} catch (IllegalAccessException ex) {
								
							}
						}
					}
				}
			}
		}
	}
	
	public StdFunctionDefinitionFactory() {
		initMap();
	}

	@Override
	public FunctionDefinition getFunctionDefinition(Identifier functionId) {
		return mapFunctionDefinitions.get(functionId);
	}
}
