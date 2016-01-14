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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openaz.xacml.api.Identifier;

@Entity
@Table(name="PolicyAlgorithms")
@NamedQuery(name="PolicyAlgorithms.findAll", query="SELECT d FROM PolicyAlgorithms d")
public class PolicyAlgorithms implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final char STANDARD = 'S';
	public static final char CUSTOM = 'C';

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="is_standard", nullable=false)
	private char isStandard;

	@Column(name="xacml_id", nullable=false, unique=true, length=255)
	private String xacmlId;
	
	@Column(name="short_name", nullable=false, length=64)
	private String shortName;

	public PolicyAlgorithms(Identifier identifier, char standard) {
		this.isStandard = standard;
		if (identifier != null) {
			this.xacmlId = identifier.stringValue();
		}
	}
	
	public PolicyAlgorithms(Identifier identifier) {
		this(identifier, PolicyAlgorithms.STANDARD);
	}

	public PolicyAlgorithms() {
		this(null, PolicyAlgorithms.STANDARD);
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public char getIsStandard() {
		return this.isStandard;
	}

	public void setIsStandard(char isStandard) {
		this.isStandard = isStandard;
	}

	@Transient
	public boolean isStandard() {
		return this.isStandard == PolicyAlgorithms.STANDARD;
	}
	
	@Transient
	public boolean isCustom() {
		return this.isStandard == PolicyAlgorithms.CUSTOM;
	}

	public String getXacmlId() {
		return this.xacmlId;
	}

	public void setXacmlId(String xacmlId) {
		this.xacmlId = xacmlId;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
