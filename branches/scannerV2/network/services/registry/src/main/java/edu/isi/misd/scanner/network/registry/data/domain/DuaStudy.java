
package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "dua_study", schema = "scanner_registry")
public class DuaStudy implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DuaStudyPK duaStudyPK;

    public DuaStudy() {
    }

    public DuaStudy(DuaStudyPK duaStudyPK) {
        this.duaStudyPK = duaStudyPK;
    }

    public DuaStudy(int duaId, int studyId) {
        this.duaStudyPK = new DuaStudyPK(duaId, studyId);
    }

    public DuaStudyPK getDuaStudyPK() {
        return duaStudyPK;
    }

    public void setDuaStudyPK(DuaStudyPK duaStudyPK) {
        this.duaStudyPK = duaStudyPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (duaStudyPK != null ? duaStudyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DuaStudy)) {
            return false;
        }
        DuaStudy other = (DuaStudy) object;
        if ((this.duaStudyPK == null && other.duaStudyPK != null) || (this.duaStudyPK != null && !this.duaStudyPK.equals(other.duaStudyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DuaStudy[ duaStudyPK=" + duaStudyPK + " ]";
    }
    
}
