package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "site", schema = "scanner_registry")
public class Site implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "site_id")
    private Integer siteId;
    @Basic(optional = false)
    @Column(name = "site_name")
    private String siteName;
    @Column(name = "description")
    private String description;    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "site")
    private List<SitePolicy> sitePolicies;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "site")
    private List<Node> nodes;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "site")
    private List<StudyRequestedSite> studyRequestedSites;
    
    public Site() {
    }

    public Site(Integer siteId) {
        this.siteId = siteId;
    }

    public Site(Integer siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<SitePolicy> getSitePolicies() {
        return sitePolicies;
    }

    public void setSitePolicies(List<SitePolicy> sitePolicies) {
        this.sitePolicies = sitePolicies;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<StudyRequestedSite> getStudyRequestedSites() {
        return studyRequestedSites;
    }

    public void setStudyRequestedSites(List<StudyRequestedSite> studyRequestedSites) {
        this.studyRequestedSites = studyRequestedSites;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (siteId != null ? siteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site) object;
        if ((this.siteId == null && other.siteId != null) || (this.siteId != null && !this.siteId.equals(other.siteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.Site[ siteId=" + siteId + " ]";
    }
    
}
