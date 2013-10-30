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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "analysis_instance", schema = "scanner_registry")
public class AnalysisInstance implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "analysis_id")
    private Integer analysisId;
    @Basic(optional = false)
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;    
    @Column(name = "status")
    private String status; 
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="studyName")
    @JsonIdentityReference(alwaysAsId=true)     
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userName")
    @JsonIdentityReference(alwaysAsId=true)         
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser user;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="nodeName")
    @JsonIdentityReference(alwaysAsId=true)         
    @JoinColumn(name = "node_id", referencedColumnName = "node_id")
    @ManyToOne(optional = false)
    private Node node;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="toolId")
    @JsonIdentityReference(alwaysAsId=true)        
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisTool;
    @JsonManagedReference("AnalysisInstance-AnalysisResult")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "analysisInstance", fetch=FetchType.EAGER)
    private List<AnalysisResult> analysisResults;

    public AnalysisInstance() {
    }

    public AnalysisInstance(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public AnalysisInstance(Integer analysisId, String transactionId, Date created, Date updated) {
        this.analysisId = analysisId;
        this.transactionId = transactionId;
        this.created = created;
        this.updated = updated;
    }

    public Integer getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(Integer analysisId) {
        this.analysisId = analysisId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public ScannerUser getUser() {
        return user;
    }

    public void setUser(ScannerUser user) {
        this.user = user;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public AnalysisTool getAnalysisTool() {
        return analysisTool;
    }

    public void setAnalysisTool(AnalysisTool analysisTool) {
        this.analysisTool = analysisTool;
    }

    public List<AnalysisResult> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(List<AnalysisResult> analysisResults) {
        this.analysisResults = analysisResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (analysisId != null ? analysisId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnalysisInstance)) {
            return false;
        }
        AnalysisInstance other = (AnalysisInstance) object;
        if ((this.analysisId == null && other.analysisId != null) || (this.analysisId != null && !this.analysisId.equals(other.analysisId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.AnalysisInstance[ analysisId=" + analysisId + " ]";
    }
    
}
