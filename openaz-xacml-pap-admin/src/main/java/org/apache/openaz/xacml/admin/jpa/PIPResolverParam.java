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
 * The persistent class for the PIPResolverParams database table.
 * 
 */
@Entity
@Table(name="PIPResolverParams")
@NamedQuery(name="PIPResolverParam.findAll", query="SELECT p FROM PIPResolverParam p")
public class PIPResolverParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="PARAM_NAME", nullable=false, length=1024)
	private String paramName;

	@Column(name="PARAM_VALUE", nullable=false, length=2048)
	private String paramValue;

	@Column(name="PARAM_DEFAULT", nullable=true, length=2048)
	private String paramDefault;
		
	@Column(name="REQUIRED", nullable=false)
	private char required = '0';

	//bi-directional many-to-one association to PIPResolver
	@ManyToOne
	@JoinColumn(name="ID_RESOLVER")
	private PIPResolver pipresolver;

	public PIPResolverParam() {
	}

	public PIPResolverParam(String name) {
		this.paramName = name;
	}

	public PIPResolverParam(String name, String value) {
		this(name);
		this.paramValue = value;
	}

	public PIPResolverParam(PIPResolverParam param) {
		this(param.getParamName(), param.getParamValue());
		this.paramDefault = param.getParamDefault();
		this.required = param.required;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return this.paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamDefault() {
		return paramDefault;
	}

	public void setParamDefault(String paramDefault) {
		this.paramDefault = paramDefault;
	}

	public char getRequired() {
		return required;
	}

	public void setRequired(char required) {
		this.required = required;
	}

	public PIPResolver getPipresolver() {
		return this.pipresolver;
	}

	public void setPipresolver(PIPResolver pipresolver) {
		this.pipresolver = pipresolver;
	}

	@Transient
	public boolean isRequired() {
		return this.required == '1';
	}
	
	@Transient
	public void setRequired(boolean required) {
		if (required) {
			this.required = '1';
		} else {
			this.required = '0';
		}
	}

	@Transient
	@Override
	public String toString() {
		return "PIPResolverParam [id=" + id + ", paramName=" + paramName
				+ ", paramValue=" + paramValue + ", required=" + required + "]";
	}

}
