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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the ObadviceExpressions database table.
 * 
 */
@Entity
@Table(name="AttributeAssignment")
@NamedQuery(name="AttributeAssignment.findAll", query="SELECT a FROM AttributeAssignment a")
public class AttributeAssignment implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String EXPRESSION_APPLY = "Apply";
	public static final String EXPRESSION_SELECTOR = "AttributeSelector";
	public static final String EXPRESSION_VALUE = "AttributeValue";
	public static final String EXPRESSION_FUNCTION = "Function";
	public static final String EXPRESSION_REFERENCE = "VarableReference";
	public static final String EXPRESSION_DESIGNATOR = "AttributeDesignator";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="attribute_id")
	private int attributeId;

	//bi-directional many-to-one association to Obadvice
	@Column(name="expression", nullable=false)
	private String expression;

	//bi-directional many-to-one association to Obadvice
	@ManyToOne
	private Obadvice obadvice; //NOPMD

	public AttributeAssignment() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAttributeId() {
		return this.attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
}
