package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "policy_status_type", schema = "scanner_registry")
public class PolicyStatusType implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "policy_status_type_id")
    private Integer policyStatusTypeId;
    @Basic(optional = false)
    @Column(name = "policy_status_type_name")
    private String policyStatusTypeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyStatusTypeId")
    private List<PolicyStatement> policyStatementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyStatusId")
    private List<AbstractPolicy> abstractPolicyList;

    public PolicyStatusType() {
    }

    public PolicyStatusType(Integer policyStatusTypeId) {
        this.policyStatusTypeId = policyStatusTypeId;
    }

    public PolicyStatusType(Integer policyStatusTypeId, String policyStatusTypeName, String description) {
        this.policyStatusTypeId = policyStatusTypeId;
        this.policyStatusTypeName = policyStatusTypeName;
        this.description = description;
    }

    public Integer getPolicyStatusTypeId() {
        return policyStatusTypeId;
    }

    public void setPolicyStatusTypeId(Integer policyStatusTypeId) {
        this.policyStatusTypeId = policyStatusTypeId;
    }

    public String getPolicyStatusTypeName() {
        return policyStatusTypeName;
    }

    public void setPolicyStatusTypeName(String policyStatusTypeName) {
        this.policyStatusTypeName = policyStatusTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        hash += (policyStatusTypeId != null ? policyStatusTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PolicyStatusType)) {
            return false;
        }
        PolicyStatusType other = (PolicyStatusType) object;
        if ((this.policyStatusTypeId == null && other.policyStatusTypeId != null) || (this.policyStatusTypeId != null && !this.policyStatusTypeId.equals(other.policyStatusTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.PolicyStatusType[ policyStatusTypeId=" + policyStatusTypeId + " ]";
    }
    
}
