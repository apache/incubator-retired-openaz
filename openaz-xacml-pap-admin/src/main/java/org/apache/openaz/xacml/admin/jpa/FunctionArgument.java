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

import javax.persistence.*;


/**
 * The persistent class for the FunctionArguments database table.
 * 
 */
@Entity
@Table(name="FunctionArguments")
@NamedQuery(name="FunctionArgument.findAll", query="SELECT f FROM FunctionArgument f")
public class FunctionArgument implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="is_bag", nullable=false)
	private int isBag;

	//bi-directional many-to-one association to FunctionDefinition
	@ManyToOne
	@JoinColumn(name="function_id")
	private FunctionDefinition functionDefinition;

	@Column(name="arg_index", nullable=false)
	private int argIndex;

	//bi-directional many-to-one association to Datatype
	@ManyToOne
	@JoinColumn(name="datatype_id")
	private Datatype datatypeBean;

	public FunctionArgument() {
	}

	public FunctionArgument(final FunctionArgument argument) {
		this.argIndex = argument.argIndex;
		this.datatypeBean = argument.datatypeBean;
		this.isBag = argument.isBag;
		this.functionDefinition = argument.functionDefinition;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArgIndex() {
		return this.argIndex;
	}

	public void setArgIndex(int argIndex) {
		this.argIndex = argIndex;
	}

	public Datatype getDatatypeBean() {
		return this.datatypeBean;
	}

	public void setDatatypeBean(Datatype datatypeBean) {
		this.datatypeBean = datatypeBean;
	}

	public FunctionDefinition getFunctionDefinition() {
		return this.functionDefinition;
	}

	public int getIsBag() {
		return isBag;
	}

	public void setIsBag(int isBag) {
		this.isBag = isBag;
	}

	public void setFunctionDefinition(FunctionDefinition functionDefinition) {
		this.functionDefinition = functionDefinition;
	}

	@Transient
	@Override
	public String toString() {
		return "FunctionArgument [id=" + id + ", argIndex=" + argIndex
				+ ", datatypeBean=" + datatypeBean + ", isBag=" + isBag
				+ ", functionDefinition=" + functionDefinition + "]";
	}
	
	@Transient
	public boolean isBag() {
		return this.isBag == 1;
	}

}
