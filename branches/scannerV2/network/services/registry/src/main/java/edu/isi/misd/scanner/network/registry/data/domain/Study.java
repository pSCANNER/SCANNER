package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 */
@Entity
@Table(name = "study", schema = "scanner_registry")
public class Study implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "study_id")
    private Integer studyId;
    @Basic(optional = false)
    @Column(name = "study_name")
    private String studyName;
    @Column(name = "description")
    private String description;    
    @Basic(optional = false)
    @Column(name = "irb_id")
    private Integer irbId;
    @Column(name = "protocol")
    private String protocol;
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "clinical_trials_id")
    private Integer clinicalTrialsId;
    @Column(name = "analysis_plan")
    private String analysisPlan; 
    // JsonIgnore studyStatusType since it is not being used right now 
    @JsonIgnore 
    @JoinColumn(name = "study_status_type_id", referencedColumnName = "study_status_type_id")
    @ManyToOne(optional = false)
    // just set studyStatusType by default to the only currently defined value (1)
    private StudyStatusType studyStatusType = new StudyStatusType(1);
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="userId")
    @JsonIdentityReference(alwaysAsId=true)        
    @JoinColumn(name = "study_owner", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser studyOwner;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "originatingStudy")
    private List<DataSetDefinition> dataSetDefinitions;    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<StudyRole> studyRoles;       
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<StudyPolicyStatement> studyPolicyStatements;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<StudyManagementPolicy> studyManagementPolicies;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<StudyRequestedSite> studyRequestedSites;
    
    public Study() {
    }

    public Study(Integer studyId) {
        this.studyId = studyId;
    }

    public Study(Integer studyId, String studyName, Integer irbId) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.irbId = irbId;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getIrbId() {
        return irbId;
    }

    public void setIrbId(Integer irbId) {
        this.irbId = irbId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getClinicalTrialsId() {
        return clinicalTrialsId;
    }

    public void setClinicalTrialsId(Integer clinicalTrialsId) {
        this.clinicalTrialsId = clinicalTrialsId;
    }

    public String getAnalysisPlan() {
        return analysisPlan;
    }

    public void setAnalysisPlan(String analysisPlan) {
        this.analysisPlan = analysisPlan;
    }

    public StudyStatusType getStudyStatusType() {
        return studyStatusType;
    }

    public void setStudyStatusType(StudyStatusType studyStatusType) {
        this.studyStatusType = studyStatusType;
    }

    public ScannerUser getStudyOwner() {
        return studyOwner;
    }

    public void setStudyOwner(ScannerUser studyOwner) {
        this.studyOwner = studyOwner;
    }
    
    public List<StudyRole> getStudyRoles() {
        return studyRoles;
    }

    public void setStudyRoles(List<StudyRole> studyRoles) {
        this.studyRoles = studyRoles;
    }

    public List<DataSetDefinition> getDataSetDefinitions() {
        return dataSetDefinitions;
    }

    public void setDataSetDefinition(List<DataSetDefinition> dataSetDefinitions) {
        this.dataSetDefinitions = dataSetDefinitions;
    }

    public List<StudyPolicyStatement> getStudyPolicyStatements() {
        return studyPolicyStatements;
    }

    public void setStudyPolicyStatements(List<StudyPolicyStatement> studyPolicyStatements) {
        this.studyPolicyStatements = studyPolicyStatements;
    }

    public List<StudyManagementPolicy> getStudyManagementPolicies() {
        return studyManagementPolicies;
    }

    public void setStudyManagementPolicies(List<StudyManagementPolicy> studyManagementPolicies) {
        this.studyManagementPolicies = studyManagementPolicies;
    }
    
    public List<StudyRequestedSite> getStudyRequestedSites() {
        return studyRequestedSites;
    }

    public void setStudyRequestedSites(List<StudyRequestedSite> studyRequestedSites) {
        this.studyRequestedSites = studyRequestedSites;
    }  
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyId != null ? studyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Study)) {
            return false;
        }
        Study other = (Study) object;
        if ((this.studyId == null && other.studyId != null) || (this.studyId != null && !this.studyId.equals(other.studyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.Study[ studyId=" + studyId + " ]";
    }
    
}
