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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;


/**
 * The persistent class for the Attribute database table.
 * 
 */
@Entity
@Table(name="Attribute")
@NamedQuery(name="Attribute.findAll", query="SELECT a FROM Attribute a")
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static String	ATTRIBUTE_DESIGNATOR = "Attribute Designator";
	public static String	ATTRIBUTE_SELECTOR = "Attribute Selector";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private int id;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="constraint_type", nullable=true)
	private ConstraintType constraintType;

	@Column(name="created_by", nullable=false, length=255)
	private String createdBy = "guest";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_date", updatable=false)
	private Date createdDate;

	@Column(name="description", nullable=true, length=2048)
	private String description;

	@Column(name="modified_by", nullable=false, length=255)
	private String modifiedBy = "guest";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="modified_date", nullable=false)
	private Date modifiedDate;

	@Column(name="xacml_id", nullable=false)
	private String xacmlId = "urn";

	//bi-directional many-to-one association to ConstraintValue
	@OneToMany(mappedBy="attribute", orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<ConstraintValue> constraintValues = new HashSet<ConstraintValue>();

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category")
	private Category categoryBean;

	//bi-directional many-to-one association to Datatype
	@ManyToOne
	@JoinColumn(name="datatype")
	private Datatype datatypeBean;

	@Column(name="is_designator", nullable=false)
	private char isDesignator = '1';

	@Column(name="selector_path", nullable=true, length=2048)
	private String selectorPath;
	
	@Transient
	private String issuer = null;
	
	@Transient
	private boolean mustBePresent = false;

	public Attribute() {
	}
	
	public Attribute(String domain) {
		this.xacmlId = domain;
	}
	
	public Attribute(String domain, String user) {
		this(domain);
		this.createdBy = user;
		this.modifiedBy = user;
	}
	public Attribute(Attribute copy, String user) {
		this(copy.getXacmlId() + ":(0)", user);
		this.constraintType = copy.getConstraintType();
		this.categoryBean = copy.getCategoryBean();
		this.datatypeBean = copy.getDatatypeBean();
		this.description = copy.getDescription();
		for (ConstraintValue value : copy.getConstraintValues()) {
			ConstraintValue newValue = new ConstraintValue(value);
			newValue.setAttribute(this);
			this.addConstraintValue(newValue);
		}
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

	public ConstraintType getConstraintType() {
		return this.constraintType;
	}

	public void setConstraintType(ConstraintType constraintType) {
		this.constraintType = constraintType;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getXacmlId() {
		return this.xacmlId;
	}
	
	@Transient
	public Identifier getXacmlIdentifier() {
		return new IdentifierImpl(this.xacmlId);
	}

	public void setXacmlId(String xacmlId) {
		this.xacmlId = xacmlId;
	}

	public Set<ConstraintValue> getConstraintValues() {
		return this.constraintValues;
	}

	public void setConstraintValues(Set<ConstraintValue> constraintValues) {
		for (ConstraintValue value : this.constraintValues) {
			value.setAttribute(this);
		}
		this.constraintValues = constraintValues;
	}

	public ConstraintValue addConstraintValue(ConstraintValue constraintValue) {
		if (this.constraintValues == null) {
			this.constraintValues = new HashSet<ConstraintValue>();
		}
		this.constraintValues.add(constraintValue);
		constraintValue.setAttribute(this);

		return constraintValue;
	}

	public ConstraintValue removeConstraintValue(ConstraintValue constraintValue) {
		this.constraintValues.remove(constraintValue);
		constraintValue.setAttribute(null);

		return constraintValue;
	}
	
	public void removeAllConstraintValues() {
		if (this.constraintValues == null) {
			return;
		}
		for (ConstraintValue value : this.constraintValues) {
			value.setAttribute(null);
		}
		this.constraintValues.clear();
	}

	public Category getCategoryBean() {
		return this.categoryBean;
	}

	public void setCategoryBean(Category categoryBean) {
		this.categoryBean = categoryBean;
	}

	public Datatype getDatatypeBean() {
		return this.datatypeBean;
	}

	public void setDatatypeBean(Datatype datatypeBean) {
		this.datatypeBean = datatypeBean;
	}

	public char getIsDesignator() {
		return this.isDesignator;
	}
	
	public void setIsDesignator(char is) {
		this.isDesignator = is;
	}
	
	public String getSelectorPath() {
		return this.selectorPath;
	}
	
	public void setSelectorPath(String path) {
		this.selectorPath = path;
	}
	
	@Transient
	public String getIssuer() {
		return issuer;
	}

	@Transient
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	@Transient
	public boolean isMustBePresent() {
		return mustBePresent;
	}

	@Transient
	public void setMustBePresent(boolean mustBePresent) {
		this.mustBePresent = mustBePresent;
	}

	@Transient
	public boolean isDesignator() {
		return this.isDesignator == '1';
	}
	
	@Transient
	public void setIsDesignator(boolean is) {
		if (is) {
			this.isDesignator = '1';
		} else {
			this.isDesignator = '0';
		}
	}
}
