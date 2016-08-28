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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openaz.xacml.api.Identifier;

/**
 * The persistent class for the Obadvice database table.
 * 
 */
@Entity
@Table(name="Obadvice")
@NamedQuery(name="Obadvice.findAll", query="SELECT o FROM Obadvice o")
public class Obadvice implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String OBLIGATION = "Obligation";
	public static final String ADVICE = "Advice";
	public static final String EFFECT_PERMIT = "Permit";
	public static final String EFFECT_DENY = "Deny";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="type", nullable=false)
	private String type;

	@Column(name="xacml_id", nullable=false, length=255)
	private String xacmlId;

	@Column(name="fulfill_on", nullable=true, length=32)
	private String fulfillOn;

	@Column(name="description", nullable=true, length=2048)
	private String description;

	//bi-directional one-to-many association to Attribute Assignment
	@OneToMany(mappedBy="obadvice", orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<ObadviceExpression> obadviceExpressions = new HashSet<ObadviceExpression>(2);

	@Column(name="created_by", nullable=false, length=255)
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", nullable=false, updatable=false)
	private Date createdDate; //NOPMD

	@Column(name="modified_by", nullable=false, length=255)
	private String modifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate; //NOPMD

	public Obadvice() {
		this.type = Obadvice.OBLIGATION;
		this.fulfillOn = Obadvice.EFFECT_PERMIT;
	}
	
	public Obadvice(String domain, String userid) {
		this.xacmlId = domain;
		this.type = Obadvice.OBLIGATION;
		this.fulfillOn = Obadvice.EFFECT_PERMIT;
		this.createdBy = userid;
		this.modifiedBy = userid;
	}

	public Obadvice(Identifier id, String userid) {
		this(id.stringValue(), userid);
	}

	@PrePersist
	public void	prePersist() {
		Date date = new Date();
		this.createdDate = date;
		this.modifiedDate = date;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.modifiedDate = new Date();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFulfillOn() {
		return this.fulfillOn;
	}

	public void setFulfillOn(String fulfillOn) {
		this.fulfillOn = fulfillOn;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getXacmlId() {
		return this.xacmlId;
	}

	public void setXacmlId(String xacmlId) {
		this.xacmlId = xacmlId;
	}

	public Set<ObadviceExpression> getObadviceExpressions() {
		return this.obadviceExpressions;
	}

	public void setObadviceExpressions(Set<ObadviceExpression> obadviceExpressions) {
		this.obadviceExpressions = obadviceExpressions;
	}

	public ObadviceExpression addObadviceExpression(ObadviceExpression obadviceExpression) {
		this.obadviceExpressions.add(obadviceExpression);
		obadviceExpression.setObadvice(this);

		return obadviceExpression;
	}

	public ObadviceExpression removeObadviceExpression(ObadviceExpression obadviceExpression) {
		this.obadviceExpressions.remove(obadviceExpression);
		obadviceExpression.setObadvice(null);

		return obadviceExpression;
	}
	
	public void removeAllExpressions() {
		if (this.obadviceExpressions == null) {
			return;
		}
		for (ObadviceExpression expression : this.obadviceExpressions) {
			expression.setObadvice(null);
		}
		this.obadviceExpressions.clear();
	}

	@Transient
	public Obadvice clone() {
		Obadvice obadvice = new Obadvice();
		
		obadvice.type = this.type;
		obadvice.xacmlId = this.xacmlId;
		obadvice.fulfillOn = this.fulfillOn;
		obadvice.description = this.description;
		obadvice.createdBy = this.createdBy;
		obadvice.modifiedBy = this.modifiedBy;
		for (ObadviceExpression exp: this.obadviceExpressions) {
			obadvice.addObadviceExpression(exp.clone());
		}

		return obadvice;
	}
}
