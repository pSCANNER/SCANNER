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
@Table(name = "study_policy_statement", schema = "scanner_registry")
public class StudyPolicyStatement implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "study_policy_statement_id")
    private Integer studyPolicyStatementId;
    @Column(name = "attestation")
    private String attestation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentStudyPolicyStatement")
    private List<PolicyStatement> policyStatements;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @ManyToOne(optional = false)
    private StudyRole role;
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;
    @JoinColumn(name = "policy_originator", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser policyOriginator;
    @JoinColumn(name = "policy_status_id", referencedColumnName = "policy_status_type_id")
    @ManyToOne(optional = false)
    private PolicyStatusType policyStatus;
    @JoinColumn(name = "policy_authority", referencedColumnName = "data_set_policy_authority_id")
    @ManyToOne(optional = false)
    private DataSetPolicyAuthority policyAuthority;
    @JoinColumn(name = "data_set_definition_id", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinition;
    @JoinColumn(name = "analysis_tool_id", referencedColumnName = "tool_id")
    @ManyToOne(optional = false)
    private AnalysisTool analysisTool;
    @JoinColumn(name = "access_mode", referencedColumnName = "access_mode_id")
    @ManyToOne(optional = false)
    private AccessMode accessMode;

    public StudyPolicyStatement() {
    }

    public StudyPolicyStatement(Integer studyPolicyStatementId) {
        this.studyPolicyStatementId = studyPolicyStatementId;
    }

    public Integer getStudyPolicyStatementId() {
        return studyPolicyStatementId;
    }

    public void setStudyPolicyStatementId(Integer studyPolicyStatementId) {
        this.studyPolicyStatementId = studyPolicyStatementId;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public List<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatements(List<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }

    public StudyRole getRole() {
        return role;
    }

    public void setRole(StudyRole role) {
        this.role = role;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public ScannerUser getPolicyOriginator() {
        return policyOriginator;
    }

    public void setPolicyOriginator(ScannerUser policyOriginator) {
        this.policyOriginator = policyOriginator;
    }

    public PolicyStatusType getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(PolicyStatusType policyStatus) {
        this.policyStatus = policyStatus;
    }

    public DataSetPolicyAuthority getPolicyAuthority() {
        return policyAuthority;
    }

    public void setPolicyAuthority(DataSetPolicyAuthority policyAuthority) {
        this.policyAuthority = policyAuthority;
    }

    public DataSetDefinition getDataSetDefinition() {
        return dataSetDefinition;
    }

    public void setDataSetDefinition(DataSetDefinition dataSetDefinition) {
        this.dataSetDefinition = dataSetDefinition;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyPolicyStatementId != null ? studyPolicyStatementId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyPolicyStatement)) {
            return false;
        }
        StudyPolicyStatement other = (StudyPolicyStatement) object;
        if ((this.studyPolicyStatementId == null && other.studyPolicyStatementId != null) || (this.studyPolicyStatementId != null && !this.studyPolicyStatementId.equals(other.studyPolicyStatementId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyPolicyStatement[ studyPolicyStatementId=" + studyPolicyStatementId + " ]";
    }
    
}
