package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "dua", schema = "scanner_registry")
public class Dua implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "dua_id")
    private Integer duaId;
    @Column(name = "dua_detail")
    private String duaDetail;

    public Dua() {
    }

    public Dua(Integer duaId) {
        this.duaId = duaId;
    }

    public Integer getDuaId() {
        return duaId;
    }

    public void setDuaId(Integer duaId) {
        this.duaId = duaId;
    }

    public String getDuaDetail() {
        return duaDetail;
    }

    public void setDuaDetail(String duaDetail) {
        this.duaDetail = duaDetail;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (duaId != null ? duaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dua)) {
            return false;
        }
        Dua other = (Dua) object;
        if ((this.duaId == null && other.duaId != null) || (this.duaId != null && !this.duaId.equals(other.duaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.Dua[ duaId=" + duaId + " ]";
    }
    
}
