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
@Table(name = "policy_statement", schema = "scanner_registry")
public class PolicyStatement implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "policy_statement_id")
    private Integer policyStatementId;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private ScannerRole role;
    @JoinColumn(name = "policy_status_type_id", referencedColumnName = "policy_status_type_id")
    @ManyToOne(optional = false)
    private PolicyStatusType policyStatusType;
    @JoinColumn(name = "data_set_instance_id", referencedColumnName = "data_set_instance_id")
    @ManyToOne(optional = false)
    private DataSetInstance dataSetInstance;
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisTool;
    @JoinColumn(name = "access_mode_id", referencedColumnName = "access_mode_id")
    @ManyToOne(optional = false)
    private AccessMode accessMode;
    @JoinColumn(name = "parent_abstract_policy_id", referencedColumnName = "abstract_policy_id")
    @ManyToOne(optional = false)
    private AbstractPolicy parentAbstractPolicy;

    public PolicyStatement() {
    }

    public PolicyStatement(Integer policyStatementId) {
        this.policyStatementId = policyStatementId;
    }

    public Integer getPolicyStatementId() {
        return policyStatementId;
    }

    public void setPolicyStatementId(Integer policyStatementId) {
        this.policyStatementId = policyStatementId;
    }

    public ScannerRole getRole() {
        return role;
    }

    public void setRole(ScannerRole role) {
        this.role = role;
    }

    public PolicyStatusType getPolicyStatusType() {
        return policyStatusType;
    }

    public void setPolicyStatusType(PolicyStatusType policyStatusType) {
        this.policyStatusType = policyStatusType;
    }

    public DataSetInstance getDataSetInstance() {
        return dataSetInstance;
    }

    public void setDataSetInstance(DataSetInstance dataSetInstance) {
        this.dataSetInstance = dataSetInstance;
    }

    public AnalysisTool getAnalysisTool() {
        return analysisTool;
    }

    public void setAnalysisTool(AnalysisTool analysisTool) {
        this.analysisTool = analysisTool;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    public AbstractPolicy getParentAbstractPolicy() {
        return parentAbstractPolicy;
    }

    public void setParentAbstractPolicy(AbstractPolicy parentAbstractPolicy) {
        this.parentAbstractPolicy = parentAbstractPolicy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (policyStatementId != null ? policyStatementId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PolicyStatement)) {
            return false;
        }
        PolicyStatement other = (PolicyStatement) object;
        if ((this.policyStatementId == null && other.policyStatementId != null) || (this.policyStatementId != null && !this.policyStatementId.equals(other.policyStatementId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.PolicyStatement[ policyStatementId=" + policyStatementId + " ]";
    }
    
}
