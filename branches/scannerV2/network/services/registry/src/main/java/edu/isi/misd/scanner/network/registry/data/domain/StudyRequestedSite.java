package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "study_requested_site", schema = "scanner_registry")
public class StudyRequestedSite implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "study_requested_site_id")
    private Integer studyRequestedSiteId;   
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;    
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    @ManyToOne(optional = false)
    private Site site;

    public StudyRequestedSite() {
    }

    public StudyRequestedSite(Integer studyRequestedSiteId) {
        this.studyRequestedSiteId = studyRequestedSiteId;
    }

    public Integer getStudyRequestedSiteId() {
        return studyRequestedSiteId;
    }

    public void setStudyRequestedSiteId(Integer studyRequestedSiteId) {
        this.studyRequestedSiteId = studyRequestedSiteId;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyRequestedSiteId != null ? studyRequestedSiteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyRequestedSite)) {
            return false;
        }
        StudyRequestedSite other = (StudyRequestedSite) object;
        if ((this.studyRequestedSiteId == null && other.studyRequestedSiteId != null) || (this.studyRequestedSiteId != null && !this.studyRequestedSiteId.equals(other.studyRequestedSiteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.StudyRequestedSite[ studyRequestedSiteId=" + studyRequestedSiteId + " ]";
    }
    
}
