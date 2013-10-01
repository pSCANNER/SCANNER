package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "study_role", schema = "scanner_registry")
public class StudyRole implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "role_id")
    private Integer roleId;
    @Basic(optional = false)
    @Column(name = "role_within_study")
    private String roleWithinStudy;       
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="studyName")
    @JsonIdentityReference(alwaysAsId=true)      
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;       
    @JoinTable(name = "user_role", schema = "scanner_registry", 
        joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "role_id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "user_id")})
    @ManyToMany
    @JsonIgnore
    private List<ScannerUser> scannerUsers; 
    @JsonIgnore     
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyRole")
    private List<UserRole> userRoles;    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyRole")
    private List<SitePolicy> sitePolicies;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyRole")
    private List<AnalysisPolicyStatement> analysisPolicyStatements;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyRole")
    private List<StudyPolicyStatement> studyPolicyStatements;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyRole")
    private List<StudyManagementPolicy> studyManagementPolicies;
    
    public StudyRole() {
    }

    public StudyRole(Integer roleId) {
        this.roleId = roleId;
    }

    public StudyRole(Integer roleId, String roleWithinStudy) {
        this.roleId = roleId;
        this.roleWithinStudy = roleWithinStudy;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleWithinStudy() {
        return roleWithinStudy;
    }

    public void setRoleWithinStudy(String roleWithinStudy) {
        this.roleWithinStudy = roleWithinStudy;
    }

    public List<SitePolicy> getSitePolicies() {
        return sitePolicies;
    }

    public void setSitePolicies(List<SitePolicy> sitePolicies) {
        this.sitePolicies = sitePolicies;
    }

    public List<AnalysisPolicyStatement> getAnalysisPolicyStatements() {
        return analysisPolicyStatements;
    }

    public void setAnalysisPolicyStatements(List<AnalysisPolicyStatement> analysisPolicyStatements) {
        this.analysisPolicyStatements = analysisPolicyStatements;
    }

    public List<ScannerUser> getScannerUsers() {
        return scannerUsers;
    }

    public void setScannerUsers(List<ScannerUser> scannerUsers) {
        this.scannerUsers = scannerUsers;
    }
    
    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<StudyPolicyStatement> getStudyPolicyStatements() {
        return studyPolicyStatements;
    }

    public void setStudyPolicyStatements(List<StudyPolicyStatement> studyPolicyStatements) {
        this.studyPolicyStatements = studyPolicyStatements;
    }

    public List<StudyManagementPolicy> getStudyManagementPolicies() {
        return studyManagementPolicies;
    }

    public void setStudyManagementPolicies(List<StudyManagementPolicy> studyManagementPolicies) {
        this.studyManagementPolicies = studyManagementPolicies;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roleId != null ? roleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyRole)) {
            return false;
        }
        StudyRole other = (StudyRole) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyRole[ roleId=" + roleId + " ]";
    }
    
}
