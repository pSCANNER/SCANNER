package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "scanner_role", schema = "scanner_registry")
public class ScannerRole implements Serializable 
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
    @JoinTable(name = "investigator_role", schema = "scanner_registry", 
        joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "role_id")}, inverseJoinColumns = {
        @JoinColumn(name = "investigator_id", referencedColumnName = "user_id")})
    @ManyToMany
    @JsonBackReference("user-role")    
    private List<ScannerUser> scannerUsers; 
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="studyName")
    @JsonIdentityReference(alwaysAsId=true)  
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<InvestigatorRole> investigatorRoles;    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<SitePolicy> sitePolicies;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<PolicyStatement> policyStatements;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "role")
    private List<AbstractPolicy> abstractPolicies;

    public ScannerRole() {
    }

    public ScannerRole(Integer roleId) {
        this.roleId = roleId;
    }

    public ScannerRole(Integer roleId, String roleWithinStudy) {
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

    public List<SitePolicy> getSitePolicies() {
        return sitePolicies;
    }

    public void setSitePolicies(List<SitePolicy> sitePolicies) {
        this.sitePolicies = sitePolicies;
    }

    public List<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatements(List<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }

    public List<AbstractPolicy> getAbstractPolicies() {
        return abstractPolicies;
    }

    public void setAbstractPolicies(List<AbstractPolicy> abstractPolicies) {
        this.abstractPolicies = abstractPolicies;
    }

    public List<InvestigatorRole> getInvestigatorRoles() {
        return investigatorRoles;
    }

    public void setInvestigatorRoles(List<InvestigatorRole> investigatorRoles) {
        this.investigatorRoles = investigatorRoles;
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
        if (!(object instanceof ScannerRole)) {
            return false;
        }
        ScannerRole other = (ScannerRole) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ScannerRole[ roleId=" + roleId + " ]";
    }
    
}
