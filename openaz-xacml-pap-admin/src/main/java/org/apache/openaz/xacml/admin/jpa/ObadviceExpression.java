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
// import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * The persistent class for the ObadviceExpressions database table.
 * 
 */
@Entity
@Table(name="ObadviceExpressions")
@NamedQuery(name="ObadviceExpression.findAll", query="SELECT o FROM ObadviceExpression o")
public class ObadviceExpression implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String EXPRESSION_APPLY = "Apply";
	public static final String EXPRESSION_SELECTOR = "Attribute Selector";
	public static final String EXPRESSION_VALUE = "Attribute Value";
	public static final String EXPRESSION_FUNCTION = "Function";
	public static final String EXPRESSION_REFERENCE = "Varable Reference";
	public static final String EXPRESSION_DESIGNATOR = "Attribute Designator";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	//unidirectional one-to-one association to Attribute
	@OneToOne
	@JoinColumn(name="attribute_id")
	private Attribute attribute;

	@Column(name="type", nullable=false)
	private String type;
	
	/*
	@Lob
	@Column(name="expression", nullable=false)
	private byte[] expression;
	*/

	//bi-directional many-to-one association to Obadvice
	@ManyToOne
	@JoinColumn(name="obadvice_id")
	private Obadvice obadvice;

	public ObadviceExpression() {
		type = EXPRESSION_VALUE;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Obadvice getObadvice() {
		return this.obadvice;
	}

	public void setObadvice(Obadvice obadvice) {
		this.obadvice = obadvice;
	}

	public ObadviceExpression clone() {
		ObadviceExpression expression = new ObadviceExpression();
		
		expression.attribute = this.attribute;
		expression.type = this.type;
		expression.obadvice = this.obadvice;
		
		return expression;
	}
}
