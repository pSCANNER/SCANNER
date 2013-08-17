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
@Table(name = "investigator_role", schema = "scanner_registry")
public class InvestigatorRole implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "investigator_role_id")
    private Integer investigatorRoleId;
    @JoinColumn(name = "investigator_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser investigator;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private ScannerRole role;

    public InvestigatorRole() {
    }

    public InvestigatorRole(Integer investigatorRoleId) {
        this.investigatorRoleId = investigatorRoleId;
    }

    public Integer getInvestigatorRoleId() {
        return investigatorRoleId;
    }

    public void setInvestigatorRoleId(Integer investigatorRoleId) {
        this.investigatorRoleId = investigatorRoleId;
    }

    public ScannerUser getInvestigator() {
        return investigator;
    }

    public void setInvestigator(ScannerUser investigator) {
        this.investigator = investigator;
    }

    public ScannerRole getRole() {
        return role;
    }

    public void setRole(ScannerRole role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (investigatorRoleId != null ? investigatorRoleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvestigatorRole)) {
            return false;
        }
        InvestigatorRole other = (InvestigatorRole) object;
        if ((this.investigatorRoleId == null && other.investigatorRoleId != null) || (this.investigatorRoleId != null && !this.investigatorRoleId.equals(other.investigatorRoleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.InvestigatorRole[ investigatorRoleId=" + investigatorRoleId + " ]";
    }
    
}
