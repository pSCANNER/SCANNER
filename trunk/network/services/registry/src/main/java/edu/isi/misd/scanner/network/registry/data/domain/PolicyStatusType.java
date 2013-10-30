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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "policy_status_type", schema = "scanner_registry")
public class PolicyStatusType implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "policy_status_type_id")
    private Integer policyStatusTypeId;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "policy_status_type_name")
    private String policyStatusTypeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyStatus")
    private List<AnalysisPolicyStatement> analysisPolicyStatements;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyStatus")
    private List<StudyPolicyStatement> studyPolicyStatements;

    public PolicyStatusType() {
    }

    public PolicyStatusType(Integer policyStatusTypeId) {
        this.policyStatusTypeId = policyStatusTypeId;
    }

    public PolicyStatusType(Integer policyStatusTypeId, String policyStatusTypeName, String description) {
        this.policyStatusTypeId = policyStatusTypeId;
        this.policyStatusTypeName = policyStatusTypeName;
        this.description = description;
    }

    public Integer getPolicyStatusTypeId() {
        return policyStatusTypeId;
    }

    public void setPolicyStatusTypeId(Integer policyStatusTypeId) {
        this.policyStatusTypeId = policyStatusTypeId;
    }

    public String getPolicyStatusTypeName() {
        return policyStatusTypeName;
    }

    public void setPolicyStatusTypeName(String policyStatusTypeName) {
        this.policyStatusTypeName = policyStatusTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AnalysisPolicyStatement> getAnalysisPolicyStatements() {
        return analysisPolicyStatements;
    }

    public void setAnalysisPolicyStatements(List<AnalysisPolicyStatement> analysisPolicyStatements) {
        this.analysisPolicyStatements = analysisPolicyStatements;
    }

    public List<StudyPolicyStatement> getStudyPolicyStatements() {
        return studyPolicyStatements;
    }

    public void setStudyPolicyStatements(List<StudyPolicyStatement> studyPolicyStatements) {
        this.studyPolicyStatements = studyPolicyStatements;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (policyStatusTypeId != null ? policyStatusTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PolicyStatusType)) {
            return false;
        }
        PolicyStatusType other = (PolicyStatusType) object;
        if ((this.policyStatusTypeId == null && other.policyStatusTypeId != null) || (this.policyStatusTypeId != null && !this.policyStatusTypeId.equals(other.policyStatusTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.PolicyStatusType[ policyStatusTypeId=" + policyStatusTypeId + " ]";
    }

}
