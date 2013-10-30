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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "analysis_policy_statement", schema = "scanner_registry")
public class AnalysisPolicyStatement implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "analysis_policy_statement_id")
    private Integer analysisPolicyStatementId;         
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private StudyRole studyRole;
    @JoinColumn(name = "policy_status_id", referencedColumnName = "policy_status_type_id")
    @ManyToOne(optional = false)
    private PolicyStatusType policyStatus;    
    @JoinColumn(name = "data_set_instance_id", referencedColumnName = "data_set_instance_id")
    @ManyToOne(optional = false)
    private DataSetInstance dataSetInstance;      
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisTool;      
    @JoinColumn(name = "access_mode_id", referencedColumnName = "access_mode_id")
    @ManyToOne(optional = false)
    private AccessMode accessMode;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="studyPolicyStatementId")
    @JsonIdentityReference(alwaysAsId=true)        
    @JoinColumn(name = "parent_study_policy_statement_id", referencedColumnName = "study_policy_statement_id")
    @ManyToOne(optional = false)
    private StudyPolicyStatement parentStudyPolicyStatement;

    public AnalysisPolicyStatement() {
    }

    public AnalysisPolicyStatement(Integer analysisPolicyStatementId) {
        this.analysisPolicyStatementId = analysisPolicyStatementId;
    }

    public Integer getAnalysisPolicyStatementId() {
        return analysisPolicyStatementId;
    }

    public void setAnalysisPolicyStatementId(Integer analysisPolicyStatementId) {
        this.analysisPolicyStatementId = analysisPolicyStatementId;
    }

    public StudyRole getStudyRole() {
        return studyRole;
    }

    public void setStudyRole(StudyRole studyRole) {
        this.studyRole = studyRole;
    }

    public PolicyStatusType getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatusType policyStatus) {
        this.policyStatus = policyStatus;
    }

    public DataSetInstance getDataSetInstance() {
        return dataSetInstance;
    }

    public void setDataSetInstance(DataSetInstance dataSetInstance) {
        this.dataSetInstance = dataSetInstance;
    }

    public AnalysisTool getAnalysisTool() {
        return analysisTool;
    }

    public void setAnalysisTool(AnalysisTool analysisTool) {
        this.analysisTool = analysisTool;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    public StudyPolicyStatement getParentStudyPolicyStatement() {
        return parentStudyPolicyStatement;
    }

    public void setParentStudyPolicyStatement(StudyPolicyStatement parentStudyPolicyStatement) {
        this.parentStudyPolicyStatement = parentStudyPolicyStatement;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (analysisPolicyStatementId != null ? analysisPolicyStatementId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnalysisPolicyStatement)) {
            return false;
        }
        AnalysisPolicyStatement other = (AnalysisPolicyStatement) object;
        if ((this.analysisPolicyStatementId == null && other.analysisPolicyStatementId != null) || (this.analysisPolicyStatementId != null && !this.analysisPolicyStatementId.equals(other.analysisPolicyStatementId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.PolicyStatement[ analysisPolicyStatementId=" + analysisPolicyStatementId + " ]";
    }
    
}
