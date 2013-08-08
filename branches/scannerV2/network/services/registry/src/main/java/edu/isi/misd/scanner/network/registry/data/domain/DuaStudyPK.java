
package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 */
@Embeddable
public class DuaStudyPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "dua_id")
    private int duaId;
    @Basic(optional = false)
    @Column(name = "study_id")
    private int studyId;

    public DuaStudyPK() {
    }

    public DuaStudyPK(int duaId, int studyId) {
        this.duaId = duaId;
        this.studyId = studyId;
    }

    public int getDuaId() {
        return duaId;
    }

    public void setDuaId(int duaId) {
        this.duaId = duaId;
    }

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) duaId;
        hash += (int) studyId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DuaStudyPK)) {
            return false;
        }
        DuaStudyPK other = (DuaStudyPK) object;
        if (this.duaId != other.duaId) {
            return false;
        }
        if (this.studyId != other.studyId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DuaStudyPK[ duaId=" + duaId + ", studyId=" + studyId + " ]";
    }
    
}
