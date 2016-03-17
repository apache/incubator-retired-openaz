/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.openaz.xacml.admin.jpa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ConstraintType")
@NamedQuery(name="ConstraintType.findAll", query="SELECT a FROM ConstraintType a")
public class ConstraintType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static String ENUMERATION_TYPE = "Enumeration";
	public static String RANGE_TYPE = "Range";
	public static String REGEXP_TYPE = "Regular Expression";
	
	public static Map<String, String> defaults = new HashMap<String, String>();
	static {
		defaults.put(ENUMERATION_TYPE, "Enumerate a set of values that the attribute may be set to during policy creation.");
		defaults.put(RANGE_TYPE, "Set a range of min and/or max integer/double values the attribute can be set to during policy creation.");
		defaults.put(REGEXP_TYPE, "Define a regular expression the attribute must match against during policy creation.");
	}
	public static final String[] RANGE_TYPES = {"minExclusive", "minInclusive", "maxExclusive", "maxInclusive"};
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="constraint_type", nullable=false, length=64)
	private String constraintType;
	
	@Column(name="description", nullable=false, length=255)
	private String description;

	//bi-directional many-to-one association to Attribute
	@OneToMany(mappedBy="constraintType")
	private Set<Attribute> attributes = new HashSet<>();

	public ConstraintType() {
		
	}

	public ConstraintType(String constraintType) {
		this();
		this.constraintType = constraintType;
	}
	
	public ConstraintType(String constraintType, String description) {
		this(constraintType);
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

}
