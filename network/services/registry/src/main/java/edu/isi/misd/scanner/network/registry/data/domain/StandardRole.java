/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.registry.data.domain; 

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "standard_role", schema = "scanner_registry")
public class StandardRole implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "standard_role_id")
    private Integer standardRoleId;
    @Basic(optional = false)
    @Column(name = "standard_role_name")
    private String standardRoleName;
    @Column(name = "description")
    private String description;
    @Column(name = "create_by_default")
    private Boolean createByDefault;
    @Column(name = "add_to_study_policy_by_default")
    private Boolean addToStudyPolicyByDefault;
    @Column(name = "add_to_user_role_by_default")
    private Boolean addToUserRoleByDefault;
    
    public StandardRole() {
    }

    public StandardRole(Integer standardRoleId) {
        this.standardRoleId = standardRoleId;
    }

    public StandardRole(Integer standardRoleId, String standardRoleName) {
        this.standardRoleId = standardRoleId;
        this.standardRoleName = standardRoleName;
    }

    public Integer getStandardRoleId() {
        return standardRoleId;
    }

    public void setStandardRoleId(Integer standardRoleId) {
        this.standardRoleId = standardRoleId;
    }

    public String getStandardRoleName() {
        return standardRoleName;
    }

    public void setStandardRoleName(String standardRoleName) {
        this.standardRoleName = standardRoleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCreateByDefault() {
        return createByDefault;
    }

    public void setCreateByDefault(Boolean createByDefault) {
        this.createByDefault = createByDefault;
    }

    public Boolean getAddToStudyPolicyByDefault() {
        return addToStudyPolicyByDefault;
    }

    public void setAddToStudyPolicyByDefault(Boolean addToStudyPolicyByDefault) {
        this.addToStudyPolicyByDefault = addToStudyPolicyByDefault;
    }

    public Boolean getAddToUserRoleByDefault() {
        return addToUserRoleByDefault;
    }

    public void setAddToUserRoleByDefault(Boolean addToUserRoleByDefault) {
        this.addToUserRoleByDefault = addToUserRoleByDefault;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (standardRoleId != null ? standardRoleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StandardRole)) {
            return false;
        }
        StandardRole other = (StandardRole) object;
        if ((this.standardRoleId == null && other.standardRoleId != null) || (this.standardRoleId != null && !this.standardRoleId.equals(other.standardRoleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StandardRole[ standardRoleId=" + standardRoleId + " ]";
    }
    
}
