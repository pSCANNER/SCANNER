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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "data_set_instance", schema = "scanner_registry")
public class DataSetInstance implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "data_set_instance_id")
    private Integer dataSetInstanceId;
    @Basic(optional = false)
    @Column(name = "data_set_instance_name")
    private String dataSetInstanceName;
    @Column(name = "description")
    private String description;    
    @Basic(optional = false)
    @Column(name = "data_source")
    private String dataSource;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="dataSetName")
    @JsonIdentityReference(alwaysAsId=true)    
    @JoinColumn(name = "data_set_definition_id", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinition;    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetInstance")
    private Set<AnalysisPolicyStatement> analysisPolicyStatements;
    @JoinColumn(name = "node_id", referencedColumnName = "node_id")
    @ManyToOne(optional = false)
    private Node node;

    public DataSetInstance() {
    }

    public DataSetInstance(Integer dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public DataSetInstance(Integer dataSetInstanceId, String dataSource) {
        this.dataSetInstanceId = dataSetInstanceId;
        this.dataSource = dataSource;
    }

    public Integer getDataSetInstanceId() {
        return dataSetInstanceId;
    }

    public void setDataSetInstanceId(Integer dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public String getDataSetInstanceName() {
        return dataSetInstanceName;
    }

    public void setDataSetInstanceName(String dataSetInstanceName) {
        this.dataSetInstanceName = dataSetInstanceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Set<AnalysisPolicyStatement> getAnalysisPolicyStatements() {
        return analysisPolicyStatements;
    }

    public void setAnalysisPolicyStatements(Set<AnalysisPolicyStatement> analysisPolicyStatements) {
        this.analysisPolicyStatements = analysisPolicyStatements;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public DataSetDefinition getDataSetDefinition() {
        return dataSetDefinition;
    }

    public void setDataSetDefinition(DataSetDefinition dataSetDefinition) {
        this.dataSetDefinition = dataSetDefinition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataSetInstanceId != null ? dataSetInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetInstance)) {
            return false;
        }
        DataSetInstance other = (DataSetInstance) object;
        if ((this.dataSetInstanceId == null && other.dataSetInstanceId != null) || (this.dataSetInstanceId != null && !this.dataSetInstanceId.equals(other.dataSetInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance[ dataSetInstanceId=" + dataSetInstanceId + " ]";
    }
    
}
