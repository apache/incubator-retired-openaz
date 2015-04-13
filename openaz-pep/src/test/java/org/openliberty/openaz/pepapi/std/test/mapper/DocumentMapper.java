package org.openliberty.openaz.pepapi.std.test.mapper;

import com.att.research.xacml.api.XACML3;
import org.openliberty.openaz.pepapi.*;

public class DocumentMapper implements ObjectMapper {

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;
	
	@Override
	public Class<?> getMappedClass() {
		return Document.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		Document d = (Document)o;
		PepRequestAttributes resourceAttributes = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
		resourceAttributes.addAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id", d.getDocumentId());
		resourceAttributes.addAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-type", "Document");
		resourceAttributes.addAttribute("jpmc:document:document-name", d.getDocumentName());
		resourceAttributes.addAttribute("jpmc:document:client-name", d.getClientName());
		resourceAttributes.addAttribute("jpmc:document:document-owner", d.getDocumentOwner());
	}

	@Override
	public void setMapperRegistry(MapperRegistry mapperRegistry) {
		this.mapperRegistry = mapperRegistry;
	}

	@Override
	public void setPepConfig(PepConfig pepConfig) {
		this.pepConfig = pepConfig;
	}
}