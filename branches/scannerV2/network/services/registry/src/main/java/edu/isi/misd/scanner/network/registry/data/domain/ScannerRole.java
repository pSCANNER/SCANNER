package edu.isi.misd.scanner.network.registry.data.domain;

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
    @JoinTable(name = "investigator_role", joinColumns = {
        @JoinColumn(name = "role_id", referencedColumnName = "role_id")}, inverseJoinColumns = {
        @JoinColumn(name = "investigator_id", referencedColumnName = "user_id")})
    @ManyToMany
    private List<ScannerUser> scannerUserList;
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study studyId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleId")
    private List<PolicyStatement> policyStatementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roleId")
    private List<AbstractPolicy> abstractPolicyList;

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

    public List<ScannerUser> getScannerUserList() {
        return scannerUserList;
    }

    public void setScannerUserList(List<ScannerUser> scannerUserList) {
        this.scannerUserList = scannerUserList;
    }

    public Study getStudyId() {
        return studyId;
    }

    public void setStudyId(Study studyId) {
        this.studyId = studyId;
    }

    public List<PolicyStatement> getPolicyStatementList() {
        return policyStatementList;
    }

    public void setPolicyStatementList(List<PolicyStatement> policyStatementList) {
        this.policyStatementList = policyStatementList;
    }

    public List<AbstractPolicy> getAbstractPolicyList() {
        return abstractPolicyList;
    }

    public void setAbstractPolicyList(List<AbstractPolicy> abstractPolicyList) {
        this.abstractPolicyList = abstractPolicyList;
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
