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
package com.att.research.xacml.std.dom;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.att.research.xacml.api.IdReferenceMatch;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.VersionMatch;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdIdReferenceMatch;
import com.att.research.xacml.std.StdVersionMatch;

/**
 * DOMIdReferenceMatch extends {@link com.att.research.xacml.std.StdIdReferenceMatch} with methods for creation from
 * DOM {@link org.w3c.dom.Node}s.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class DOMIdReferenceMatch extends StdIdReferenceMatch {
	private static final Log logger	= LogFactory.getLog(DOMIdReferenceMatch.class);
	
	protected DOMIdReferenceMatch(Identifier idIn, VersionMatch versionIn, VersionMatch earliestVersionIn, VersionMatch latestVersionIn) {
		super(idIn, versionIn, earliestVersionIn, latestVersionIn);
	}

	public static IdReferenceMatch newInstance(Node nodeIdReferenceMatch) throws DOMStructureException {
		Element	elementIdReferenceMatch		= DOMUtil.getElement(nodeIdReferenceMatch);
		boolean bLenient					= DOMProperties.isLenient();
		
		Identifier	idReferenceMatch		= DOMUtil.getIdentifierContent(elementIdReferenceMatch, !bLenient);
		
		String versionString			= DOMUtil.getStringAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_VERSION);
		String versionEarliestString	= DOMUtil.getStringAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_EARLIESTVERSION);
		String versionLatestString		= DOMUtil.getStringAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_LATESTVERSION);
		
		VersionMatch version			= null;
		VersionMatch versionEarliest	= null;
		VersionMatch versionLatest		= null;
		
		if (versionString != null) {
			try {
				version	= StdVersionMatch.newInstance(versionString);
			} catch (ParseException ex) {
				if (!bLenient) {
					throw new DOMStructureException(nodeIdReferenceMatch, "Invalid " + XACML3.ATTRIBUTE_VERSION + " string \"" + versionString + "\" in \"" + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
				}
			}
		}
		if (versionEarliestString != null) {
			try {
				versionEarliest = StdVersionMatch.newInstance(versionEarliestString);
			} catch (ParseException ex) {
				if (!bLenient) {
					throw new DOMStructureException(nodeIdReferenceMatch, "Invalid " + XACML3.ATTRIBUTE_EARLIESTVERSION + " string \"" + versionEarliestString + "\" in \"" + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
				}
			}
		}
		if (versionLatestString != null) {
			try {
				versionLatest = StdVersionMatch.newInstance(versionLatestString);
			} catch (ParseException ex) {
				if (!bLenient) {
					throw new DOMStructureException(nodeIdReferenceMatch, "Invalid " + XACML3.ATTRIBUTE_LATESTVERSION + " string \"" + versionLatestString + "\" in \"" + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
				}
			}
		}
		
		return new DOMIdReferenceMatch(idReferenceMatch, version, versionEarliest, versionLatest);
	}
	
	public static boolean repair(Node nodeIdReferenceMatch) throws DOMStructureException {
		Element	elementIdReferenceMatch		= DOMUtil.getElement(nodeIdReferenceMatch);
		boolean result						= false;
		
		result	= DOMUtil.repairIdentifierContent(elementIdReferenceMatch, logger) || result;
		result	= DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_VERSION, logger) || result;
		result	= DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_EARLIESTVERSION, logger) || result;
		result	= DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_LATESTVERSION, logger) || result;
		
		return result;
	}
}
