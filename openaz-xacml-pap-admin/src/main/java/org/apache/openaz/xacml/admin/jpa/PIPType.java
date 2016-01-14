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


/**
 * The persistent class for the PIPType database table.
 * 
 */
@Entity
@Table(name="PIPType")
@NamedQuery(name="PIPType.findAll", query="SELECT p FROM PIPType p")
public class PIPType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_SQL = "SQL";
	public static final String TYPE_LDAP = "LDAP";
	public static final String TYPE_CSV = "CSV";
	public static final String TYPE_HYPERCSV = "Hyper-CSV";
	public static final String TYPE_CUSTOM = "Custom";

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="type", nullable=false, length=45)
	private String type;

	//bi-directional many-to-one association to PIPConfiguration
	@OneToMany(mappedBy="piptype")
	private Set<PIPConfiguration> pipconfigurations;

	public PIPType() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<PIPConfiguration> getPipconfigurations() {
		return this.pipconfigurations;
	}

	public void setPipconfigurations(Set<PIPConfiguration> pipconfigurations) {
		this.pipconfigurations = pipconfigurations;
	}

	public PIPConfiguration addPipconfiguration(PIPConfiguration pipconfiguration) {
		getPipconfigurations().add(pipconfiguration);
		pipconfiguration.setPiptype(this);

		return pipconfiguration;
	}

	public PIPConfiguration removePipconfiguration(PIPConfiguration pipconfiguration) {
		getPipconfigurations().remove(pipconfiguration);
		pipconfiguration.setPiptype(null);

		return pipconfiguration;
	}
	
	@Transient
	public boolean	isSQL() {
		return this.type.equals(TYPE_SQL);
	}

	@Transient
	public boolean	isLDAP() {
		return this.type.equals(TYPE_LDAP);
	}

	@Transient
	public boolean	isCSV() {
		return this.type.equals(TYPE_CSV);
	}

	@Transient
	public boolean	isHyperCSV() {
		return this.type.equals(TYPE_HYPERCSV);
	}

	@Transient
	public boolean	isCustom() {
		return this.type.equals(TYPE_CUSTOM);
	}

}
