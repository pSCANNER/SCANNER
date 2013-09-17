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
@Table(name = "study_management_policy", schema = "scanner_registry")
public class StudyManagementPolicy implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "study_policy_id")
    private Integer studyPolicyId;  
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private StudyRole studyRole;
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;

    public StudyManagementPolicy() {
    }

    public StudyManagementPolicy(Integer studyPolicyId) {
        this.studyPolicyId = studyPolicyId;
    }

    public Integer getStudyPolicyId() {
        return studyPolicyId;
    }

    public void setStudyPolicyId(Integer studyPolicyId) {
        this.studyPolicyId = studyPolicyId;
    }

    public StudyRole getStudyRole() {
        return studyRole;
    }

    public void setStudyRoleId(StudyRole studyRole) {
        this.studyRole = studyRole;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyPolicyId != null ? studyPolicyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyManagementPolicy)) {
            return false;
        }
        StudyManagementPolicy other = (StudyManagementPolicy) object;
        if ((this.studyPolicyId == null && other.studyPolicyId != null) || (this.studyPolicyId != null && !this.studyPolicyId.equals(other.studyPolicyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy[ studyPolicyId=" + studyPolicyId + " ]";
    }
    
}
