package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "scanner_grant", schema = "scanner_registry")
public class ScannerGrant implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "grant_id")
    private Integer grantId;
    @Column(name = "grant_detail")
    private String grantDetail;
    @JoinTable(name = "study_grant", joinColumns = {
        @JoinColumn(name = "grant_id", referencedColumnName = "grant_id")}, inverseJoinColumns = {
        @JoinColumn(name = "study_id", referencedColumnName = "study_id")})
    @ManyToMany
    private List<Study> studies;

    public ScannerGrant() {
    }

    public ScannerGrant(Integer grantId) {
        this.grantId = grantId;
    }

    public Integer getGrantId() {
        return grantId;
    }

    public void setGrantId(Integer grantId) {
        this.grantId = grantId;
    }

    public String getGrantDetail() {
        return grantDetail;
    }

    public void setGrantDetail(String grantDetail) {
        this.grantDetail = grantDetail;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (grantId != null ? grantId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScannerGrant)) {
            return false;
        }
        ScannerGrant other = (ScannerGrant) object;
        if ((this.grantId == null && other.grantId != null) || (this.grantId != null && !this.grantId.equals(other.grantId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ScannerGrant[ grantId=" + grantId + " ]";
    }
    
}
