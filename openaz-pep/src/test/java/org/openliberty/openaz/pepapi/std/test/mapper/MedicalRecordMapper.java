package org.openliberty.openaz.pepapi.std.test.mapper;

import com.att.research.xacml.api.XACML3;
import org.openliberty.openaz.pepapi.*;

public class MedicalRecordMapper implements ObjectMapper {

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;

	@Override
	public Class<?> getMappedClass() {
		return MedicalRecord.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		MedicalRecord md = (MedicalRecord) o;
		PepRequestAttributes resourceAttributes = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
		resourceAttributes.addAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-type", "PatientMedicalRecord");
		resourceAttributes.addAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id", md.getId());
		for(String accessUser: md.getAccessUserGroup()) {
			resourceAttributes.addAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-access-group", accessUser);
		}
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
