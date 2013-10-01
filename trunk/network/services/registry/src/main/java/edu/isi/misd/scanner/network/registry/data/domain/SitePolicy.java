package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "site_policy", schema = "scanner_registry")
public class SitePolicy implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "site_policy_id")
    private Integer sitePolicyId;
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    @ManyToOne(optional = false)
    private Site site;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private StudyRole studyRole;

    public SitePolicy() {
    }

    public SitePolicy(Integer sitePolicyId) {
        this.sitePolicyId = sitePolicyId;
    }

    public Integer getSitePolicyId() {
        return sitePolicyId;
    }

    public void setSitePolicyId(Integer sitePolicyId) {
        this.sitePolicyId = sitePolicyId;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public StudyRole getStudyRole() {
        return studyRole;
    }

    public void setStudyRole(StudyRole studyRole) {
        this.studyRole = studyRole;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sitePolicyId != null ? sitePolicyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SitePolicy)) {
            return false;
        }
        SitePolicy other = (SitePolicy) object;
        if ((this.sitePolicyId == null && other.sitePolicyId != null) || (this.sitePolicyId != null && !this.sitePolicyId.equals(other.sitePolicyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.SitePolicy[ sitePolicyId=" + sitePolicyId + " ]";
    }
    
}
