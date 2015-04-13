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
package com.att.research.xacml.std.pip.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.att.research.xacml.api.Status;
import com.att.research.xacml.api.pip.PIPEngine;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.pip.StdMutablePIPResponse;
import com.att.research.xacml.std.pip.StdPIPResponse;

/**
 * EngineFinder implements the {@link com.att.research.xacml.api.pip.PIPFinder} interface by maintaining a simple list of
 * registered {@link com.att.research.xacml.api.pip.PIPEngine} objects.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class EngineFinder implements PIPFinder {
	private Map<String,List<PIPEngine>> pipEngines	= new HashMap<String,List<PIPEngine>>();

	/**
	 * Creates an empty <code>EngineFinder</code>
	 */
	public EngineFinder() {
	}
	
	/**
	 * Registers a new <code>PIPEngine</code> with this <code>EngineFinder</code>.
	 * 
	 * @param pipEngine the <code>PIPEngine</code> to register
	 */
	public void register(PIPEngine pipEngine) {
		if (pipEngine != null) {
			List<PIPEngine> pipEnginesForName	= this.pipEngines.get(pipEngine.getName());
			if (pipEnginesForName == null) {
				pipEnginesForName	= new ArrayList<PIPEngine>();
				this.pipEngines.put(pipEngine.getName(), pipEnginesForName);
			}
			pipEnginesForName.add(pipEngine);
		}
	}
	
	@Override
	public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude, PIPFinder pipFinderParent) throws PIPException {
		StdMutablePIPResponse pipResponse	= new StdMutablePIPResponse();
		Status firstErrorStatus	= null;
		Iterator<List<PIPEngine>> iterPIPEngineLists	= this.pipEngines.values().iterator();
		while (iterPIPEngineLists.hasNext()) {
			List<PIPEngine> listPIPEngines	= iterPIPEngineLists.next();
			for (PIPEngine pipEngine : listPIPEngines) {
				if (pipEngine != exclude) {
					PIPResponse pipResponseEngine = null;
					try {
						pipResponseEngine = pipEngine.getAttributes(pipRequest, pipFinderParent);
					} catch (Exception e) {
						pipResponseEngine = new StdPIPResponse(new
								StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR));
					}
					if (pipResponseEngine != null) {
						if (pipResponseEngine.getStatus() == null || pipResponseEngine.getStatus().isOk()) {
							pipResponse.addAttributes(pipResponseEngine.getAttributes());
						} else if (firstErrorStatus == null) {
							firstErrorStatus = pipResponseEngine.getStatus();
						}
					}
				}
			}
		}
		if (pipResponse.getAttributes().size() == 0 && firstErrorStatus != null) {
			pipResponse.setStatus(firstErrorStatus);
		}
		
		return new StdPIPResponse(pipResponse);
	}
	
	@Override
	public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude, PIPFinder pipFinderParent) throws PIPException {
		return StdPIPResponse.getMatchingResponse(pipRequest, this.getAttributes(pipRequest, exclude, pipFinderParent));
	}
	
	@Override
	public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
		return this.getAttributes(pipRequest, exclude, this);
	}
	
	@Override
	public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
		return StdPIPResponse.getMatchingResponse(pipRequest, this.getAttributes(pipRequest, exclude));
	}

	@Override
	public Collection<PIPEngine> getPIPEngines() {
		List<PIPEngine> engines = new ArrayList<PIPEngine>();
		for (List<PIPEngine> list : this.pipEngines.values()) {
			for (PIPEngine engine : list) {
				engines.add(engine);
			}
		}
		return Collections.unmodifiableList(engines);
	}
}
