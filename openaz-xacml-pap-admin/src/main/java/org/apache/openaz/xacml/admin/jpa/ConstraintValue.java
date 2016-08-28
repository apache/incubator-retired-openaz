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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ConstraintValues database table.
 * 
 */
@Entity
@Table(name="ConstraintValues")
@NamedQuery(name="ConstraintValue.findAll", query="SELECT c FROM ConstraintValue c")
public class ConstraintValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="property")
	private String property;

	@Column(name="value")
	private String value;

	//bi-directional many-to-one association to Attribute
	@ManyToOne
	@JoinColumn(name="attribute_id")
	private Attribute attribute;

	public ConstraintValue() {
	}

	public ConstraintValue(String property, String value) {
		this.property = property;
		this.value = value;
	}
	
	public ConstraintValue(ConstraintValue value) {
		this.property = value.getProperty();
		this.value = value.getValue();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProperty() {
		return this.property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	
	public ConstraintValue clone() {
		ConstraintValue constraint = new ConstraintValue();
		
		constraint.property = this.property;
		constraint.value = this.value;
		constraint.attribute = this.attribute;
		
		return constraint;
	}
}
