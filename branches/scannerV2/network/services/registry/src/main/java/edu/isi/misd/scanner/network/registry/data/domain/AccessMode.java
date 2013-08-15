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
 * @author mdarcy
 */
@Entity
@Table(name = "access_mode", schema = "scanner_registry")
public class AccessMode implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "access_mode_id")
    private Integer accessModeId;
    @Basic(optional = false)
    @Column(name = "access_mode_name")
    private String accessModeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accessModeId")
    private List<PolicyStatement> policyStatementList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accessMode")
    private List<AbstractPolicy> abstractPolicyList;

    public AccessMode() {
    }

    public AccessMode(Integer accessModeId) {
        this.accessModeId = accessModeId;
    }

    public AccessMode(Integer accessModeId, String accessModeName, String description) {
        this.accessModeId = accessModeId;
        this.accessModeName = accessModeName;
        this.description = description;
    }

    public Integer getAccessModeId() {
        return accessModeId;
    }

    public void setAccessModeId(Integer accessModeId) {
        this.accessModeId = accessModeId;
    }

    public String getAccessModeName() {
        return accessModeName;
    }

    public void setAccessModeName(String accessModeName) {
        this.accessModeName = accessModeName;
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
        hash += (accessModeId != null ? accessModeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccessMode)) {
            return false;
        }
        AccessMode other = (AccessMode) object;
        if ((this.accessModeId == null && other.accessModeId != null) || (this.accessModeId != null && !this.accessModeId.equals(other.accessModeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.AccessMode[ accessModeId=" + accessModeId + " ]";
    }
    
}
