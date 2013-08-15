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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "abstract_policy", schema = "scanner_registry")
public class AbstractPolicy implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "abstract_policy_id")
    private Integer abstractPolicyId;
    @Basic(optional = false)
    @Column(name = "attestation")
    private String attestation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentAbstractPolicyId")
    private List<PolicyStatement> policyStatementList;
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study studyId;
    @JoinColumn(name = "policy_originator", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser policyOriginator;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private ScannerRole roleId;
    @JoinColumn(name = "policy_status_id", referencedColumnName = "policy_status_type_id")
    @ManyToOne(optional = false)
    private PolicyStatusType policyStatusId;
    @JoinColumn(name = "policy_authority", referencedColumnName = "data_set_policy_authority_id")
    @ManyToOne(optional = false)
    private DataSetPolicyAuthority policyAuthority;
    @JoinColumn(name = "data_set_definition_id", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinitionId;
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisToolId;
    @JoinColumn(name = "access_mode", referencedColumnName = "access_mode_id")
    @ManyToOne(optional = false)
    private AccessMode accessMode;

    public AbstractPolicy() {
    }

    public AbstractPolicy(Integer abstractPolicyId) {
        this.abstractPolicyId = abstractPolicyId;
    }

    public AbstractPolicy(Integer abstractPolicyId, String attestation) {
        this.abstractPolicyId = abstractPolicyId;
        this.attestation = attestation;
    }

    public Integer getAbstractPolicyId() {
        return abstractPolicyId;
    }

    public void setAbstractPolicyId(Integer abstractPolicyId) {
        this.abstractPolicyId = abstractPolicyId;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public List<PolicyStatement> getPolicyStatementList() {
        return policyStatementList;
    }

    public void setPolicyStatementList(List<PolicyStatement> policyStatementList) {
        this.policyStatementList = policyStatementList;
    }

    public Study getStudyId() {
        return studyId;
    }

    public void setStudyId(Study studyId) {
        this.studyId = studyId;
    }

    public ScannerUser getPolicyOriginator() {
        return policyOriginator;
    }

    public void setPolicyOriginator(ScannerUser policyOriginator) {
        this.policyOriginator = policyOriginator;
    }

    public ScannerRole getRoleId() {
        return roleId;
    }

    public void setRoleId(ScannerRole roleId) {
        this.roleId = roleId;
    }

    public PolicyStatusType getPolicyStatusId() {
        return policyStatusId;
    }

    public void setPolicyStatusId(PolicyStatusType policyStatusId) {
        this.policyStatusId = policyStatusId;
    }

    public DataSetPolicyAuthority getPolicyAuthority() {
        return policyAuthority;
    }

    public void setPolicyAuthority(DataSetPolicyAuthority policyAuthority) {
        this.policyAuthority = policyAuthority;
    }

    public DataSetDefinition getDataSetDefinitionId() {
        return dataSetDefinitionId;
    }

    public void setDataSetDefinitionId(DataSetDefinition dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    public AnalysisTool getAnalysisToolId() {
        return analysisToolId;
    }

    public void setAnalysisToolId(AnalysisTool analysisToolId) {
        this.analysisToolId = analysisToolId;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (abstractPolicyId != null ? abstractPolicyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AbstractPolicy)) {
            return false;
        }
        AbstractPolicy other = (AbstractPolicy) object;
        if ((this.abstractPolicyId == null && other.abstractPolicyId != null) || (this.abstractPolicyId != null && !this.abstractPolicyId.equals(other.abstractPolicyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.AbstractPolicy[ abstractPolicyId=" + abstractPolicyId + " ]";
    }
    
}
