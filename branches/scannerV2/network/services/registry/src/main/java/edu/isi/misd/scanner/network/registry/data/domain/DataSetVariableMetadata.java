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
 *
 */
@Entity
@Table(name = "data_set_variable_metadata", schema = "scanner_registry")
public class DataSetVariableMetadata implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "data_set_variable_metadata_id")
    private Integer dataSetVariableMetadataId;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="dataSetName")
    @JsonIdentityReference(alwaysAsId=true)    
    @JoinColumn(name = "data_set_definition", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinition;    
    @Basic(optional = false)
    @Column(name = "variable_name")
    private String variableName;
    @Basic(optional = false)
    @Column(name = "variable_type")
    private String variableType;
    @Column(name = "variable_description")
    private String variableDescription;
    @Column(name = "variable_options")
    private String variableOptions;

    public DataSetVariableMetadata() {
    }

    public DataSetVariableMetadata(Integer dataSetVariableMetadataId) {
        this.dataSetVariableMetadataId = dataSetVariableMetadataId;
    }

    public DataSetVariableMetadata(Integer dataSetVariableMetadataId, String variableName, String variableType) {
        this.dataSetVariableMetadataId = dataSetVariableMetadataId;
        this.variableName = variableName;
        this.variableType = variableType;
    }

    public Integer getDataSetVariableMetadataId() {
        return dataSetVariableMetadataId;
    }

    public void setDataSetVariableMetadataId(Integer dataSetVariableMetadataId) {
        this.dataSetVariableMetadataId = dataSetVariableMetadataId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableType() {
        return variableType;
    }

    public void setVariableType(String variableType) {
        this.variableType = variableType;
    }

    public String getVariableDescription() {
        return variableDescription;
    }

    public void setVariableDescription(String variableDescription) {
        this.variableDescription = variableDescription;
    }

    public String getVariableOptions() {
        return variableOptions;
    }

    public void setVariableOptions(String variableOptions) {
        this.variableOptions = variableOptions;
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
        hash += (dataSetVariableMetadataId != null ? dataSetVariableMetadataId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetVariableMetadata)) {
            return false;
        }
        DataSetVariableMetadata other = (DataSetVariableMetadata) object;
        if ((this.dataSetVariableMetadataId == null && other.dataSetVariableMetadataId != null) || (this.dataSetVariableMetadataId != null && !this.dataSetVariableMetadataId.equals(other.dataSetVariableMetadataId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetVariableMetadata[ dataSetVariableMetadataId=" + dataSetVariableMetadataId + " ]";
    }
    
}
