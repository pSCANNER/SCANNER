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
@Table(name = "data_set_policy_authority", schema = "scanner_registry")
public class DataSetPolicyAuthority implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "data_set_policy_authority_id")
    private Integer dataSetPolicyAuthorityId;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "data_set_policy_authority_name")
    private String dataSetPolicyAuthorityName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyAuthority")
    private List<StudyPolicyStatement> studyPolicyStatements;

    public DataSetPolicyAuthority() {
    }

    public DataSetPolicyAuthority(Integer dataSetPolicyAuthorityId) {
        this.dataSetPolicyAuthorityId = dataSetPolicyAuthorityId;
    }

    public DataSetPolicyAuthority(Integer dataSetPolicyAuthorityId, String dataSetPolicyAuthorityName, String description) {
        this.dataSetPolicyAuthorityId = dataSetPolicyAuthorityId;
        this.dataSetPolicyAuthorityName = dataSetPolicyAuthorityName;
        this.description = description;
    }

    public Integer getDataSetPolicyAuthorityId() {
        return dataSetPolicyAuthorityId;
    }

    public void setDataSetPolicyAuthorityId(Integer dataSetPolicyAuthorityId) {
        this.dataSetPolicyAuthorityId = dataSetPolicyAuthorityId;
    }

    public String getDataSetPolicyAuthorityName() {
        return dataSetPolicyAuthorityName;
    }

    public void setDataSetPolicyAuthorityName(String dataSetPolicyAuthorityName) {
        this.dataSetPolicyAuthorityName = dataSetPolicyAuthorityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        hash += (dataSetPolicyAuthorityId != null ? dataSetPolicyAuthorityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetPolicyAuthority)) {
            return false;
        }
        DataSetPolicyAuthority other = (DataSetPolicyAuthority) object;
        if ((this.dataSetPolicyAuthorityId == null && other.dataSetPolicyAuthorityId != null) || (this.dataSetPolicyAuthorityId != null && !this.dataSetPolicyAuthorityId.equals(other.dataSetPolicyAuthorityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetPolicyAuthority[ dataSetPolicyAuthorityId=" + dataSetPolicyAuthorityId + " ]";
    }
    
}
