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
    private ScannerRole roleId;
    @JoinColumn(name = "policy_status_type_id", referencedColumnName = "policy_status_type_id")
    @ManyToOne(optional = false)
    private PolicyStatusType policyStatusTypeId;
    @JoinColumn(name = "data_set_instance_id", referencedColumnName = "data_set_instance_id")
    @ManyToOne(optional = false)
    private DataSetInstance dataSetInstanceId;
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisToolId;
    @JoinColumn(name = "access_mode_id", referencedColumnName = "access_mode_id")
    @ManyToOne(optional = false)
    private AccessMode accessModeId;
    @JoinColumn(name = "parent_abstract_policy_id", referencedColumnName = "abstract_policy_id")
    @ManyToOne(optional = false)
    private AbstractPolicy parentAbstractPolicyId;

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

    public ScannerRole getRoleId() {
        return roleId;
    }

    public void setRoleId(ScannerRole roleId) {
        this.roleId = roleId;
    }

    public PolicyStatusType getPolicyStatusTypeId() {
        return policyStatusTypeId;
    }

    public void setPolicyStatusTypeId(PolicyStatusType policyStatusTypeId) {
        this.policyStatusTypeId = policyStatusTypeId;
    }

    public DataSetInstance getDataSetInstanceId() {
        return dataSetInstanceId;
    }

    public void setDataSetInstanceId(DataSetInstance dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public AnalysisTool getAnalysisToolId() {
        return analysisToolId;
    }

    public void setAnalysisToolId(AnalysisTool analysisToolId) {
        this.analysisToolId = analysisToolId;
    }

    public AccessMode getAccessModeId() {
        return accessModeId;
    }

    public void setAccessModeId(AccessMode accessModeId) {
        this.accessModeId = accessModeId;
    }

    public AbstractPolicy getParentAbstractPolicyId() {
        return parentAbstractPolicyId;
    }

    public void setParentAbstractPolicyId(AbstractPolicy parentAbstractPolicyId) {
        this.parentAbstractPolicyId = parentAbstractPolicyId;
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
