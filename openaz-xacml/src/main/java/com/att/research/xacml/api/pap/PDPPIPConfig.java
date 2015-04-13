/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.api.pap;

import java.util.Map;

import com.att.research.xacml.std.pap.StdPDPPIPConfig;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/*
 * The following allows us to use Jackson to convert sub-types of this type into JSON and back to objects.
 */
@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "PDPPIPConfigType")  
@JsonSubTypes({  
	    @Type(value = StdPDPPIPConfig.class, name = "StdPDPPIPConfig") })  
public interface PDPPIPConfig {
	
	public String				getId();
	
	public String 				getName();
	
	public String				getDescription();

	public String 				getClassname();

	public Map<String,String>	getConfiguration();
	
	public boolean				isConfigured();

}
