package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "analysis_tool", schema = "scanner_registry")
public class AnalysisTool implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tool_id")
    private Integer toolId;
    @Basic(optional = false)
    @Column(name = "tool_name")
    private String toolName;
    @Basic(optional = false)
    @Column(name = "tool_path")
    private String toolPath;    
    @Column(name = "tool_description")
    private String toolDescription;
    @Basic(optional = false)
    @Column(name = "input_format_specifications")
    private String inputFormatSpecifications;
    @Basic(optional = false)
    @Column(name = "output_format_specifications")
    private String outputFormatSpecifications;
    @Basic(optional = false)
    @Column(name = "information_email")
    private String informationEmail;
    @JoinColumn(name = "tool_parent_library_id", referencedColumnName = "library_id")
    @ManyToOne(optional = false)
    @JsonBackReference("toolParentLibrary")         
    private ToolLibrary toolParentLibrary;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "analysisTool")
    private List<PolicyStatement> policyStatements;
    @JsonIgnore       
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "analysisTool")
    private List<StudyPolicyStatement> studyPolicyStatements;

    public AnalysisTool() {
    }

    public AnalysisTool(Integer toolId) {
        this.toolId = toolId;
    }

    public AnalysisTool(Integer toolId, String toolName, String toolDescription, String inputFormatSpecifications, String outputFormatSpecifications, int curatorUid, String informationEmail) {
        this.toolId = toolId;
        this.toolName = toolName;
        this.toolDescription = toolDescription;
        this.inputFormatSpecifications = inputFormatSpecifications;
        this.outputFormatSpecifications = outputFormatSpecifications;
        this.informationEmail = informationEmail;
    }

    public Integer getToolId() {
        return toolId;
    }

    public void setToolId(Integer toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
    
    public String getToolPath() {
        return toolPath;
    }

    public void setToolPath(String toolPath) {
        this.toolPath = toolPath;
    }
    
    public String getToolDescription() {
        return toolDescription;
    }

    public void setToolDescription(String toolDescription) {
        this.toolDescription = toolDescription;
    }

    public String getInputFormatSpecifications() {
        return inputFormatSpecifications;
    }

    public void setInputFormatSpecifications(String inputFormatSpecifications) {
        this.inputFormatSpecifications = inputFormatSpecifications;
    }

    public String getOutputFormatSpecifications() {
        return outputFormatSpecifications;
    }

    public void setOutputFormatSpecifications(String outputFormatSpecifications) {
        this.outputFormatSpecifications = outputFormatSpecifications;
    }

    public String getInformationEmail() {
        return informationEmail;
    }

    public void setInformationEmail(String informationEmail) {
        this.informationEmail = informationEmail;
    }

    public ToolLibrary getToolParentLibrary() {
        return toolParentLibrary;
    }

    public void setToolParentLibrary(ToolLibrary toolParentLibrary) {
        this.toolParentLibrary = toolParentLibrary;
    }

    public List<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatements(List<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }
    
    public List<StudyPolicyStatement> getStudyPolicyStatements() {
        return studyPolicyStatements;
    }

    public void setStudyPolicyStatements(List<StudyPolicyStatement> studyPolicyStatements) {
        this.studyPolicyStatements = studyPolicyStatements;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (toolId != null ? toolId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnalysisTool)) {
            return false;
        }
        AnalysisTool other = (AnalysisTool) object;
        if ((this.toolId == null && other.toolId != null) || (this.toolId != null && !this.toolId.equals(other.toolId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool[ toolId=" + toolId + " ]";
    }
    
}
