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

import java.util.List;


/**
 * The persistent class for the FunctionDefinition database table.
 * 
 */
@Entity
@Table(name="FunctionDefinition")
@NamedQuery(name="FunctionDefinition.findAll", query="SELECT f FROM FunctionDefinition f")
public class FunctionDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="short_name", nullable=false, length=64)
	private String shortname;

	@Column(name="xacml_id", nullable=false, length=255)
	private String xacmlid;
	
	//bi-directional many-to-one association to Datatype
	@ManyToOne
	@JoinColumn(name="return_datatype", nullable=true)
	private Datatype datatypeBean;

	@Column(name="is_bag_return", nullable=false)
	private int isBagReturn;
	
	@Column(name="is_higher_order", nullable=false)
	private int isHigherOrder;

	@Column(name="arg_lb", nullable=false)
	private int argLb;

	@Column(name="arg_ub", nullable=false)
	private int argUb;

	@Column(name="ho_arg_lb", nullable=true)
	private int higherOrderArg_LB;
	
	@Column(name="ho_arg_ub", nullable=true)
	private int higherOrderArg_UB;
	
	@Column(name="ho_primitive", nullable=true)
	private char higherOrderIsPrimitive;

	//bi-directional many-to-one association to FunctionArgument
	@OneToMany(mappedBy="functionDefinition")
	private List<FunctionArgument> functionArguments;

	public FunctionDefinition() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArgLb() {
		return this.argLb;
	}

	public void setArgLb(int argLb) {
		this.argLb = argLb;
	}

	public int getArgUb() {
		return this.argUb;
	}

	public void setArgUb(int argUb) {
		this.argUb = argUb;
	}

	public int getIsBagReturn() {
		return isBagReturn;
	}

	public void setIsBagReturn(int isBagReturn) {
		this.isBagReturn = isBagReturn;
	}

	public int getIsHigherOrder() {
		return isHigherOrder;
	}

	public void setIsHigherOrder(int isHigherOrder) {
		this.isHigherOrder = isHigherOrder;
	}

	public Datatype getDatatypeBean() {
		return this.datatypeBean;
	}

	public void setDatatypeBean(Datatype datatypeBean) {
		this.datatypeBean = datatypeBean;
	}

	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getXacmlid() {
		return this.xacmlid;
	}

	public void setXacmlid(String xacmlid) {
		this.xacmlid = xacmlid;
	}

	public int getHigherOrderArg_LB() {
		return higherOrderArg_LB;
	}

	public void setHigherOrderArg_LB(int higherOrderArg_LB) {
		this.higherOrderArg_LB = higherOrderArg_LB;
	}

	public int getHigherOrderArg_UB() {
		return higherOrderArg_UB;
	}

	public void setHigherOrderArg_UB(int higherOrderArg_UB) {
		this.higherOrderArg_UB = higherOrderArg_UB;
	}

	public char getHigherOrderIsPrimitive() {
		return higherOrderIsPrimitive;
	}

	public void setHigherOrderIsPrimitive(char higherOrderIsPrimitive) {
		this.higherOrderIsPrimitive = higherOrderIsPrimitive;
	}

	public List<FunctionArgument> getFunctionArguments() {
		return this.functionArguments;
	}

	public void setFunctionArguments(List<FunctionArgument> functionArguments) {
		this.functionArguments = functionArguments;
	}

	public FunctionArgument addFunctionArgument(FunctionArgument functionArgument) {
		getFunctionArguments().add(functionArgument);
		functionArgument.setFunctionDefinition(this);

		return functionArgument;
	}

	public FunctionArgument removeFunctionArgument(FunctionArgument functionArgument) {
		getFunctionArguments().remove(functionArgument);
		functionArgument.setFunctionDefinition(null);

		return functionArgument;
	}

	@Transient
	@Override
	public String toString() {
		return "FunctionDefinition [id=" + id + ", argLb=" + argLb + ", argUb="
				+ argUb + ", isBagReturn=" + isBagReturn + ", isHigherOrder="
				+ isHigherOrder + ", datatypeBean=" + datatypeBean
				+ ", shortname=" + shortname + ", xacmlid=" + xacmlid
				+ ", higherOrderArg_LB=" + higherOrderArg_LB
				+ ", higherOrderArg_UB=" + higherOrderArg_UB
				+ ", higherOrderIsPrimitive=" + higherOrderIsPrimitive
				+ ", functionArguments=" + functionArguments + "]";
	}

	@Transient
	public boolean isBagReturn() {
		return this.isBagReturn == 1;
	}

	@Transient
	public boolean isHigherOrder() {
		return this.isHigherOrder == 1;
	}

}
