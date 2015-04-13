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

import java.util.Set;

import com.att.research.xacml.std.pap.StdPDPGroupStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/*
 * The following allows us to use Jackson to convert sub-types of this type into JSON and back to objects.
 */
@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "PDPGroupStatusType")  
@JsonSubTypes({  
	    @Type(value = StdPDPGroupStatus.class, name = "StdPDPGroupStatus") }) 
public interface PDPGroupStatus {
	
	public enum Status {
		OK,
		OUT_OF_SYNCH,
		LOAD_ERRORS,
		UPDATING_CONFIGURATION,
		UNKNOWN
	}
	
	Status						getStatus();
	
	public Set<String>			getLoadErrors();
	
	public Set<String>			getLoadWarnings();
	
	public Set<PDPPolicy>		getLoadedPolicies();
	
	public Set<PDPPolicy>		getFailedPolicies();
	
	public boolean				policiesOK();
	
	public Set<PDPPIPConfig>	getLoadedPipConfigs();
	
	public Set<PDPPIPConfig>	getFailedPipConfigs();
	
	public boolean				pipConfigOK();
	
	public Set<PDP>				getInSynchPDPs();
	
	public Set<PDP>				getOutOfSynchPDPs();
	
	public Set<PDP>				getFailedPDPs();
	
	public Set<PDP>				getUpdatingPDPs();
	
	public Set<PDP>				getLastUpdateFailedPDPs();
	
	public Set<PDP>				getUnknownStatusPDPs();
	
	public boolean				pdpsOK();

	public boolean				isGroupOk();
}
