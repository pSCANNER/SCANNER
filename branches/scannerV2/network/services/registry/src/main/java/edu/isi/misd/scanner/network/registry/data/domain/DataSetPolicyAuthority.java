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
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "data_set_policy_authority", schema = "scanner_registry")
public class DataSetPolicyAuthority implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "authority_id")
    private Integer authorityId;
    @Basic(optional = false)
    @Column(name = "authority_name")
    private String authorityName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyAuthority")
    private List<AbstractPolicy> abstractPolicyList;

    public DataSetPolicyAuthority() {
    }

    public DataSetPolicyAuthority(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public DataSetPolicyAuthority(Integer authorityId, String authorityName) {
        this.authorityId = authorityId;
        this.authorityName = authorityName;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
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
        hash += (authorityId != null ? authorityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetPolicyAuthority)) {
            return false;
        }
        DataSetPolicyAuthority other = (DataSetPolicyAuthority) object;
        if ((this.authorityId == null && other.authorityId != null) || (this.authorityId != null && !this.authorityId.equals(other.authorityId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetPolicyAuthority[ authorityId=" + authorityId + " ]";
    }
    
}
