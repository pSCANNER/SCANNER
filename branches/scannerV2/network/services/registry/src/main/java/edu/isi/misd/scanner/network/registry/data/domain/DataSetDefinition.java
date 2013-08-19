package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Set;
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

/**
 *
 */
@Entity
@Table(name = "data_set_definition", schema = "scanner_registry")
public class DataSetDefinition implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "data_set_definition_id")
    private Integer dataSetDefinitionId;
    @Basic(optional = false)
    @Column(name = "data_description_xml")
    private String dataDescriptionXml;
    @Basic(optional = false)
    @Column(name = "data_processing_xml")
    private String dataProcessingXml;
    @Basic(optional = false)
    @Column(name = "data_processing_program")
    private String dataProcessingProgram;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="studyName")
    @JsonIdentityReference(alwaysAsId=true)      
    @JoinColumn(name = "originating_study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study originatingStudy;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userName")
    @JsonIdentityReference(alwaysAsId=true)      
    @JoinColumn(name = "author_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser author;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="description")
    @JsonIdentityReference(alwaysAsId=true)       
    @JoinColumn(name = "data_set_confidentiality_level", referencedColumnName = "level_id")
    @ManyToOne(optional = false)
    private ConfidentialityLevel dataSetConfidentialityLevel;
    @JsonManagedReference("DataSetDefinition-DataSetInstance")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinition", fetch=FetchType.EAGER)
    private Set<DataSetInstance> dataSetInstances;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinition")
    private Set<AbstractPolicy> abstractPolicies;

    public DataSetDefinition() {
    }

    public DataSetDefinition(Integer dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    public DataSetDefinition(Integer dataSetDefinitionId, String dataDescriptionXml, String dataProcessingXml, String dataProcessingProgram) {
        this.dataSetDefinitionId = dataSetDefinitionId;
        this.dataDescriptionXml = dataDescriptionXml;
        this.dataProcessingXml = dataProcessingXml;
        this.dataProcessingProgram = dataProcessingProgram;
    }

    public Integer getDataSetDefinitionId() {
        return dataSetDefinitionId;
    }

    public void setDataSetDefinitionId(Integer dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    public String getDataDescriptionXml() {
        return dataDescriptionXml;
    }

    public void setDataDescriptionXml(String dataDescriptionXml) {
        this.dataDescriptionXml = dataDescriptionXml;
    }

    public String getDataProcessingXml() {
        return dataProcessingXml;
    }

    public void setDataProcessingXml(String dataProcessingXml) {
        this.dataProcessingXml = dataProcessingXml;
    }

    public String getDataProcessingProgram() {
        return dataProcessingProgram;
    }

    public void setDataProcessingProgram(String dataProcessingProgram) {
        this.dataProcessingProgram = dataProcessingProgram;
    }

    public Study getOriginatingStudy() {
        return originatingStudy;
    }

    public void setOriginatingStudy(Study originatingStudy) {
        this.originatingStudy = originatingStudy;
    }

    public ScannerUser getAuthor() {
        return author;
    }

    public void setAuthor(ScannerUser author) {
        this.author = author;
    }

    public ConfidentialityLevel getDataSetConfidentialityLevel() {
        return dataSetConfidentialityLevel;
    }

    public void setDataSetConfidentialityLevel(ConfidentialityLevel dataSetConfidentialityLevel) {
        this.dataSetConfidentialityLevel = dataSetConfidentialityLevel;
    }

    public Set<DataSetInstance> getDataSetInstances() {
        return dataSetInstances;
    }

    public void setDataSetInstances(Set<DataSetInstance> dataSetInstances) {
        this.dataSetInstances = dataSetInstances;
    }

    public Set<AbstractPolicy> getAbstractPolicies() {
        return abstractPolicies;
    }

    public void setAbstractPolicies(Set<AbstractPolicy> abstractPolicies) {
        this.abstractPolicies = abstractPolicies;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataSetDefinitionId != null ? dataSetDefinitionId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetDefinition)) {
            return false;
        }
        DataSetDefinition other = (DataSetDefinition) object;
        if ((this.dataSetDefinitionId == null && other.dataSetDefinitionId != null) || (this.dataSetDefinitionId != null && !this.dataSetDefinitionId.equals(other.dataSetDefinitionId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetDefinition[ dataSetDefinitionId=" + dataSetDefinitionId + " ]";
    }
    
}
