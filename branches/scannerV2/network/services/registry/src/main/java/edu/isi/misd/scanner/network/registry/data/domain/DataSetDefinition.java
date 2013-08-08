
package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import java.util.List;
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
    @Column(name = "data_description_xml")
    private String dataDescriptionXml;
    @Basic(optional = false)
    @Column(name = "data_processing_xml")
    private String dataProcessingXml;
    @Basic(optional = false)
    @Column(name = "data_processing_program")
    private String dataProcessingProgram;
    @JoinColumn(name = "author_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users authorUid;
    @JoinColumn(name = "originating_study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study originatingStudyId;
    @JoinColumn(name = "data_set_confidentiality_level", referencedColumnName = "level_id")
    @ManyToOne(optional = false)
    private ConfidentialityLevels dataSetConfidentialityLevel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetDefinitionId")
    private List<DataSetInstance> dataSetInstanceList;

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

    public Users getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(Users authorUid) {
        this.authorUid = authorUid;
    }

    public Study getOriginatingStudyId() {
        return originatingStudyId;
    }

    public void setOriginatingStudyId(Study originatingStudyId) {
        this.originatingStudyId = originatingStudyId;
    }

    public ConfidentialityLevels getDataSetConfidentialityLevel() {
        return dataSetConfidentialityLevel;
    }

    public void setDataSetConfidentialityLevel(ConfidentialityLevels dataSetConfidentialityLevel) {
        this.dataSetConfidentialityLevel = dataSetConfidentialityLevel;
    }

    public List<DataSetInstance> getDataSetInstanceList() {
        return dataSetInstanceList;
    }

    public void setDataSetInstanceList(List<DataSetInstance> dataSetInstanceList) {
        this.dataSetInstanceList = dataSetInstanceList;
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
