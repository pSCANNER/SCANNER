package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
    @Basic(optional = false)
    @Column(name = "irb_id")
    private int irbId;
    @Basic(optional = false)
    @Column(name = "protocol")
    private String protocol;
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Basic(optional = false)
    @Column(name = "clinical_trials_id")
    private int clinicalTrialsId;
    @Basic(optional = false)
    @Column(name = "analysis_plan")
    private String analysisPlan;
    @JsonIgnore
    @ManyToMany(mappedBy = "studies")
    private List<SourceDataWarehouse> sourceDataWarehouses;
    @JsonIgnore    
    @ManyToMany(mappedBy = "studies")
    private List<ScannerGrant> scannerGrants;   
    @JsonManagedReference("study-role")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study", fetch=FetchType.EAGER)
    private List<ScannerRole> scannerRoles;    
    @JoinColumn(name = "study_status_type_id", referencedColumnName = "study_status_type_id")
    @ManyToOne(optional = false)
    private StudyStatusType studyStatusType;  
    @JoinColumn(name = "principal_investigator_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser principalInvestigator;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "originatingStudy")
    private List<DataSetDefinition> dataSetDefinitions;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<DataSetInstance> dataSetInstances;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study")
    private List<AbstractPolicy> abstractPolicies;

    public Study() {
    }

    public Study(Integer studyId) {
        this.studyId = studyId;
    }

    public Study(Integer studyId, int irbId, String protocol, Date startDate, Date endDate, int clinicalTrialsId, String analysisPlan) {
        this.studyId = studyId;
        this.irbId = irbId;
        this.protocol = protocol;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clinicalTrialsId = clinicalTrialsId;
        this.analysisPlan = analysisPlan;
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
    
    public int getIrbId() {
        return irbId;
    }

    public void setIrbId(int irbId) {
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

    public int getClinicalTrialsId() {
        return clinicalTrialsId;
    }

    public void setClinicalTrialsId(int clinicalTrialsId) {
        this.clinicalTrialsId = clinicalTrialsId;
    }

    public String getAnalysisPlan() {
        return analysisPlan;
    }

    public void setAnalysisPlan(String analysisPlan) {
        this.analysisPlan = analysisPlan;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouses() {
        return sourceDataWarehouses;
    }

    public void setSourceDataWarehouses(List<SourceDataWarehouse> sourceDataWarehouses) {
        this.sourceDataWarehouses = sourceDataWarehouses;
    }

    public List<ScannerGrant> getScannerGrants() {
        return scannerGrants;
    }

    public void setScannerGrants(List<ScannerGrant> scannerGrants) {
        this.scannerGrants = scannerGrants;
    }

    public List<ScannerRole> getScannerRoles() {
        return scannerRoles;
    }

    public void setScannerRoles(List<ScannerRole> scannerRoles) {
        this.scannerRoles = scannerRoles;
    }

    public StudyStatusType getStudyStatusType() {
        return studyStatusType;
    }

    public void setStudyStatusType(StudyStatusType studyStatusType) {
        this.studyStatusType = studyStatusType;
    }

    public ScannerUser getPrincipalInvestigator() {
        return principalInvestigator;
    }

    public void setPrincipalInvestigatorUid(ScannerUser principalInvestigator) {
        this.principalInvestigator = principalInvestigator;
    }

    public List<DataSetDefinition> getDataSetDefinitions() {
        return dataSetDefinitions;
    }

    public void setDataSetDefinition(List<DataSetDefinition> dataSetDefinitions) {
        this.dataSetDefinitions = dataSetDefinitions;
    }

    public List<DataSetInstance> getDataSetInstances() {
        return dataSetInstances;
    }

    public void setDataSetInstances(List<DataSetInstance> dataSetInstances) {
        this.dataSetInstances = dataSetInstances;
    }

    public List<AbstractPolicy> getAbstractPolicies() {
        return abstractPolicies;
    }

    public void setAbstractPolicies(List<AbstractPolicy> abstractPolicies) {
        this.abstractPolicies = abstractPolicies;
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
