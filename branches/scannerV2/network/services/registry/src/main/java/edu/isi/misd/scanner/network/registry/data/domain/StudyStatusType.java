package edu.isi.misd.scanner.network.registry.data.domain;

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
@Table(name = "study_status_type", schema = "scanner_registry")
public class StudyStatusType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "study_status_type_id")
    private Integer studyStatusTypeId;
    @Basic(optional = false)
    @Column(name = "study_status_type_name")
    private String studyStatusTypeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyStatusTypeId")
    private List<Study> studyList;

    public StudyStatusType() {
    }

    public StudyStatusType(Integer studyStatusTypeId) {
        this.studyStatusTypeId = studyStatusTypeId;
    }

    public StudyStatusType(Integer studyStatusTypeId, String studyStatusTypeName, String description) {
        this.studyStatusTypeId = studyStatusTypeId;
        this.studyStatusTypeName = studyStatusTypeName;
        this.description = description;
    }

    public Integer getStudyStatusTypeId() {
        return studyStatusTypeId;
    }

    public void setStudyStatusTypeId(Integer studyStatusTypeId) {
        this.studyStatusTypeId = studyStatusTypeId;
    }

    public String getStudyStatusTypeName() {
        return studyStatusTypeName;
    }

    public void setStudyStatusTypeName(String studyStatusTypeName) {
        this.studyStatusTypeName = studyStatusTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Study> getStudyList() {
        return studyList;
    }

    public void setStudyList(List<Study> studyList) {
        this.studyList = studyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyStatusTypeId != null ? studyStatusTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyStatusType)) {
            return false;
        }
        StudyStatusType other = (StudyStatusType) object;
        if ((this.studyStatusTypeId == null && other.studyStatusTypeId != null) || (this.studyStatusTypeId != null && !this.studyStatusTypeId.equals(other.studyStatusTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyStatusType[ studyStatusTypeId=" + studyStatusTypeId + " ]";
    }
    
}
