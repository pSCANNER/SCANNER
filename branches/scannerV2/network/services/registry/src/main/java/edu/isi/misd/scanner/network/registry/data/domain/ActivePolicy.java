package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@Embeddable
public class ActivePolicy implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Column(name = "policy_statement_id")
    private Integer policyStatementId;
    @Column(name = "data_set_instance_id")
    private Integer dataSetInstanceId;
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "analysis_tool_id")
    private Integer analysisToolId;
    @Column(name = "access_mode")
    private Integer accessMode;
    @Column(name = "policy_status_id")
    private Integer policyStatusId;

    public ActivePolicy() {
    }

    public Integer getPolicyStatementId() {
        return policyStatementId;
    }

    public void setPolicyStatementId(Integer policyStatementId) {
        this.policyStatementId = policyStatementId;
    }

    public Integer getDataSetInstanceId() {
        return dataSetInstanceId;
    }

    public void setDataSetInstanceId(Integer dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getAnalysisToolId() {
        return analysisToolId;
    }

    public void setAnalysisToolId(Integer analysisToolId) {
        this.analysisToolId = analysisToolId;
    }

    public Integer getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(Integer accessMode) {
        this.accessMode = accessMode;
    }

    public Integer getPolicyStatusId() {
        return policyStatusId;
    }

    public void setPolicyStatusId(Integer policyStatusId) {
        this.policyStatusId = policyStatusId;
    }
    
}
