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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.IdentifierImpl;


/**
 * The persistent class for the Datatype database table.
 * 
 */
@Entity
@Table(name="Datatype")
@NamedQuery(name="Datatype.findAll", query="SELECT d FROM Datatype d")
public class Datatype implements Serializable {
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

	//bi-directional many-to-one association to Attribute
	@OneToMany(mappedBy="datatypeBean")
	private Set<Attribute> attributes = new HashSet<>();

	//bi-directional many-to-one association to Attribute
	@OneToMany(mappedBy="datatypeBean")
	private Set<FunctionDefinition> functions = new HashSet<>();

	//bi-directional many-to-one association to Attribute
	@OneToMany(mappedBy="datatypeBean")
	private Set<FunctionArgument> arguments = new HashSet<>();

	public Datatype() {
		this.xacmlId = XACML3.ID_DATATYPE_STRING.stringValue();
		this.isStandard = Datatype.STANDARD;
	}
	
	public Datatype(int id, Datatype dt) {
		this.id = id;
		this.isStandard = dt.isStandard;
		this.xacmlId = dt.xacmlId;
		this.shortName = dt.shortName;
		//
		// Make a copy?
		//
		this.attributes = new HashSet<>();
	}
	
	public Datatype(Identifier identifier, char standard) {
		if (identifier != null) {
			this.xacmlId = identifier.stringValue();
		}
		this.isStandard = standard;
	}
	
	public Datatype(Identifier identifier) {
		this(identifier, Datatype.STANDARD);
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

	public Set<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute addAttribute(Attribute attribute) {
		getAttributes().add(attribute);
		attribute.setDatatypeBean(this);

		return attribute;
	}

	public Attribute removeAttribute(Attribute attribute) {
		getAttributes().remove(attribute);
		attribute.setDatatypeBean(null);

		return attribute;
	}

	public Set<FunctionDefinition> getFunctions() {
		return this.functions;
	}

	public void setFunctions(Set<FunctionDefinition> functions) {
		this.functions = functions;
	}

	public FunctionDefinition addFunction(FunctionDefinition function) {
		getFunctions().add(function);
		function.setDatatypeBean(this);

		return function;
	}

	public FunctionDefinition removeAttribute(FunctionDefinition function) {
		getFunctions().remove(function);
		function.setDatatypeBean(null);

		return function;
	}

	public Set<FunctionArgument> getArguments() {
		return this.arguments;
	}

	public void setArguments(Set<FunctionArgument> argument) {
		this.arguments = argument;
	}

	public FunctionArgument addArgument(FunctionArgument argument) {
		getArguments().add(argument);
		argument.setDatatypeBean(this);

		return argument;
	}

	public FunctionArgument removeArgument(FunctionArgument argument) {
		getArguments().remove(argument);
		argument.setDatatypeBean(null);

		return argument;
	}

	@Transient
	public Identifier getIdentifer() {
		return new IdentifierImpl(this.xacmlId);
	}

	@Transient
	public boolean isStandard() {
		return this.isStandard == Datatype.STANDARD;
	}
	
	@Transient
	public boolean isCustom() {
		return this.isStandard == Datatype.CUSTOM;
	}
	
	@Transient
	@Override
	public String toString() {
		return "Datatype [id=" + id + ", isStandard=" + isStandard
				+ ", xacmlId=" + xacmlId + ", shortName=" + shortName
				+ ", attributes=" + attributes + ", functions=" + functions
				+ ", arguments=" + arguments + "]";
	}

}
