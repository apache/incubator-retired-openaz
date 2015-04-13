package org.openliberty.openaz.pepapi.std.test.mapper;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {
	
	private String id;
	
	private List<String> accessUserGroup;
	
	public MedicalRecord(String id){
		this.id = id;
		accessUserGroup = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getAccessUserGroup() {
		return accessUserGroup;
	}

	public void setAccessUserGroup(List<String> accessUserGroup) {
		this.accessUserGroup = accessUserGroup;
	}
	
	public void addUserToAccessGroup(String user) {
		this.accessUserGroup.add(user);
	}
	
}
