
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "confidentiality_levels", schema = "scanner_registry")
public class ConfidentialityLevels implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "level_id")
    private Integer levelId;
    @Basic(optional = false)
    @Column(name = "level_name")
    private String levelName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataWarehouseConfidentialityLevel")
    private List<SourceDataWarehouse> sourceDataWarehouseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataResourceConfidentialityLevel")
    private List<PolicyRegistry> policyRegistryList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetConfidentialityLevel")
    private List<DataSetDefinition> dataSetDefinitionList;

    public ConfidentialityLevels() {
    }

    public ConfidentialityLevels(Integer levelId) {
        this.levelId = levelId;
    }

    public ConfidentialityLevels(Integer levelId, String levelName) {
        this.levelId = levelId;
        this.levelName = levelName;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouseList() {
        return sourceDataWarehouseList;
    }

    public void setSourceDataWarehouseList(List<SourceDataWarehouse> sourceDataWarehouseList) {
        this.sourceDataWarehouseList = sourceDataWarehouseList;
    }

    public List<PolicyRegistry> getPolicyRegistryList() {
        return policyRegistryList;
    }

    public void setPolicyRegistryList(List<PolicyRegistry> policyRegistryList) {
        this.policyRegistryList = policyRegistryList;
    }

    public List<DataSetDefinition> getDataSetDefinitionList() {
        return dataSetDefinitionList;
    }

    public void setDataSetDefinitionList(List<DataSetDefinition> dataSetDefinitionList) {
        this.dataSetDefinitionList = dataSetDefinitionList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (levelId != null ? levelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConfidentialityLevels)) {
            return false;
        }
        ConfidentialityLevels other = (ConfidentialityLevels) object;
        if ((this.levelId == null && other.levelId != null) || (this.levelId != null && !this.levelId.equals(other.levelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ConfidentialityLevels[ levelId=" + levelId + " ]";
    }
    
}
