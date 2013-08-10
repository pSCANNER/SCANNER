package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @Column(name = "study_status_id")
    private Integer studyStatusId;
    @Basic(optional = false)
    @Column(name = "study_status_name")
    private String studyStatusName;

    public StudyStatusType() {
    }

    public StudyStatusType(Integer studyStatusId) {
        this.studyStatusId = studyStatusId;
    }

    public StudyStatusType(Integer studyStatusId, String studyStatusName) {
        this.studyStatusId = studyStatusId;
        this.studyStatusName = studyStatusName;
    }

    public Integer getStudyStatusId() {
        return studyStatusId;
    }

    public void setStudyStatusId(Integer studyStatusId) {
        this.studyStatusId = studyStatusId;
    }

    public String getStudyStatusName() {
        return studyStatusName;
    }

    public void setStudyStatusName(String studyStatusName) {
        this.studyStatusName = studyStatusName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyStatusId != null ? studyStatusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyStatusType)) {
            return false;
        }
        StudyStatusType other = (StudyStatusType) object;
        if ((this.studyStatusId == null && other.studyStatusId != null) || (this.studyStatusId != null && !this.studyStatusId.equals(other.studyStatusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyStatusType[ studyStatusId=" + studyStatusId + " ]";
    }
    
}
