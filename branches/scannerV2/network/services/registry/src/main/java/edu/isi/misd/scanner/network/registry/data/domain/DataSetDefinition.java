package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
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
    @Column(name = "data_set_name")
    private String dataSetName; 
    @Column(name = "description")
    private String description;
    @Column(name = "data_processing_program")
    private String dataProcessingProgram;
    @JoinColumn(name = "originating_study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study originatingStudy;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userName")
    @JsonIdentityReference(alwaysAsId=true)      
    @JoinColumn(name = "author_uid", referencedColumnName = "user_id")
    @ManyToOne
    private ScannerUser author;
    @JoinColumn(name = "data_set_confidentiality_level", referencedColumnName = "level_id")
    @ManyToOne(optional = false)
    private ConfidentialityLevel dataSetConfidentialityLevel;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinition")
    private Set<DataSetInstance> dataSetInstances;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinition")
    private List<StudyPolicyStatement> studyPolicyStatements;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinition")
    private List<DataSetVariableMetadata> dataSetVariableMetadata;    

    public DataSetDefinition() {
    }

    public DataSetDefinition(Integer dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    public DataSetDefinition(Integer dataSetDefinitionId, String dataSetName) {
        this.dataSetDefinitionId = dataSetDefinitionId;
        this.dataSetName = dataSetName;
    }

    public Integer getDataSetDefinitionId() {
        return dataSetDefinitionId;
    }

    public void setDataSetDefinitionId(Integer dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<StudyPolicyStatement> getStudyPolicyStatements() {
        return studyPolicyStatements;
    }

    public void setStudyPolicyStatements(List<StudyPolicyStatement> studyPolicyStatements) {
        this.studyPolicyStatements = studyPolicyStatements;
    }

    public List<DataSetVariableMetadata> getDataSetVariableMetadata() {
        return dataSetVariableMetadata;
    }

    public void setDataSetVariableMetadata(List<DataSetVariableMetadata> dataSetVariableMetadata) {
        this.dataSetVariableMetadata = dataSetVariableMetadata;
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
