
package edu.isi.misd.scanner.network.registry.data.domain;

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
    @Basic(optional = false)
    @Column(name = "grant_ids")
    private String grantIds;
    @Basic(optional = false)
    @Column(name = "data_set_ids")
    private String dataSetIds;
    @Basic(optional = false)
    @Column(name = "dua_ids")
    private String duaIds;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
    private List<Roles> rolesList;
    @JoinColumn(name = "principal_investigator_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users principalInvestigatorUid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "originatingStudyId")
    private List<DataSetDefinition> dataSetDefinitionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
    private List<DataSetInstance> dataSetInstanceList;

    public Study() {
    }

    public Study(Integer studyId) {
        this.studyId = studyId;
    }

    public Study(Integer studyId, int irbId, String protocol, Date startDate, Date endDate, int clinicalTrialsId, String analysisPlan, String grantIds, String dataSetIds, String duaIds) {
        this.studyId = studyId;
        this.irbId = irbId;
        this.protocol = protocol;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clinicalTrialsId = clinicalTrialsId;
        this.analysisPlan = analysisPlan;
        this.grantIds = grantIds;
        this.dataSetIds = dataSetIds;
        this.duaIds = duaIds;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
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

    public String getGrantIds() {
        return grantIds;
    }

    public void setGrantIds(String grantIds) {
        this.grantIds = grantIds;
    }

    public String getDataSetIds() {
        return dataSetIds;
    }

    public void setDataSetIds(String dataSetIds) {
        this.dataSetIds = dataSetIds;
    }

    public String getDuaIds() {
        return duaIds;
    }

    public void setDuaIds(String duaIds) {
        this.duaIds = duaIds;
    }

    public List<Roles> getRolesList() {
        return rolesList;
    }

    public void setRolesList(List<Roles> rolesList) {
        this.rolesList = rolesList;
    }

    public Users getPrincipalInvestigatorUid() {
        return principalInvestigatorUid;
    }

    public void setPrincipalInvestigatorUid(Users principalInvestigatorUid) {
        this.principalInvestigatorUid = principalInvestigatorUid;
    }

    public List<DataSetDefinition> getDataSetDefinitionList() {
        return dataSetDefinitionList;
    }

    public void setDataSetDefinitionList(List<DataSetDefinition> dataSetDefinitionList) {
        this.dataSetDefinitionList = dataSetDefinitionList;
    }

    public List<DataSetInstance> getDataSetInstanceList() {
        return dataSetInstanceList;
    }

    public void setDataSetInstanceList(List<DataSetInstance> dataSetInstanceList) {
        this.dataSetInstanceList = dataSetInstanceList;
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
