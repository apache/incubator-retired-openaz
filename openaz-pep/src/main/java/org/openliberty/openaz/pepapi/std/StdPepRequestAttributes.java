package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdMutableAttribute;
import com.att.research.xacml.std.StdMutableRequestAttributes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.PepRequestAttributes;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


final class StdPepRequestAttributes implements PepRequestAttributes {
	
	private static final Log log = LogFactory.getLog(StdPepRequestAttributes.class);

	private final String id;

	private final Identifier categoryIdentifier;

	private String issuer;

	private StdMutableRequestAttributes wrappedRequestAttributes;

	//Internal map to hold mutable attributes as StdMutableRequestAttributes
	// does not return a mutable view of Attributes.
	private Map<Identifier, StdMutableAttribute> attributeMapById;

	StdPepRequestAttributes(String id, Identifier categoryIdentifier) {
		this.id = id;
		this.categoryIdentifier = categoryIdentifier;
		this.attributeMapById = new HashMap<Identifier, StdMutableAttribute>();
		this.wrappedRequestAttributes =  new StdMutableRequestAttributes();
		this.wrappedRequestAttributes.setCategory(categoryIdentifier);
		this.wrappedRequestAttributes.setXmlId(id);
	}

	@Override
	public Identifier getCategory() {
		return categoryIdentifier;
	}

	@Override
	public void addAttribute(String name, Date... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_DATE);
	}

	@Override
	public void addAttribute(String name, String... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_STRING);
	}

	@Override
	public void addAttribute(String name, Integer... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_INTEGER);
	}

	@Override
	public void addAttribute(String name, Boolean... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_BOOLEAN);
	}

	@Override
	public void addAttribute(String name, Long... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_INTEGER);
	}

	@Override
	public void addAttribute(String name, Double... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_DOUBLE);
	}

	@Override
	public void addAttribute(String name, URI... values) {
		addAttribute(name, values, XACML3.ID_DATATYPE_ANYURI);
	}

	private <T> void addAttribute(String name, T[] values, Identifier dataTypeId) {
		if(values == null) {
			throw new IllegalArgumentException("Null attribute value provided for attribute: " + name);
		}
		Identifier attributeId = new IdentifierImpl(name);
		StdMutableAttribute mutableAttribute = attributeMapById.get(attributeId);
		if(mutableAttribute == null) {
			mutableAttribute = new StdMutableAttribute();
			mutableAttribute.setAttributeId(new IdentifierImpl(name));
			mutableAttribute.setCategory(categoryIdentifier);
			mutableAttribute.setIncludeInResults(false);
			mutableAttribute.setIssuer(issuer == null?"":issuer);
			attributeMapById.put(attributeId, mutableAttribute);
			wrappedRequestAttributes.add(mutableAttribute);
		}
		for(T value: values) {
			if(value != null) {
				mutableAttribute.addValue(new StdAttributeValue<T>(dataTypeId, value));
			}
		}
	}

	@Override
	public RequestAttributes getWrappedRequestAttributes() {
		return wrappedRequestAttributes;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

}
