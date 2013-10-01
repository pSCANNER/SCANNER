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
 *
 */
@Entity
@Table(name = "confidentiality_level", schema = "scanner_registry")
public class ConfidentialityLevel implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "level_id")
    private Integer levelId;
    @JsonIgnore    
    @Basic(optional = false)
    @Column(name = "level_name")
    private String levelName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetConfidentialityLevel")
    private List<DataSetDefinition> dataSetDefinitions;

    public ConfidentialityLevel() {
    }

    public ConfidentialityLevel(Integer levelId) {
        this.levelId = levelId;
    }

    public ConfidentialityLevel(Integer levelId, String levelName, String description) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DataSetDefinition> getDataSetDefinitions() {
        return dataSetDefinitions;
    }

    public void setDataSetDefinitions(List<DataSetDefinition> dataSetDefinitions) {
        this.dataSetDefinitions = dataSetDefinitions;
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
        if (!(object instanceof ConfidentialityLevel)) {
            return false;
        }
        ConfidentialityLevel other = (ConfidentialityLevel) object;
        if ((this.levelId == null && other.levelId != null) || (this.levelId != null && !this.levelId.equals(other.levelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ConfidentialityLevel[ levelId=" + levelId + " ]";
    }
    
}
