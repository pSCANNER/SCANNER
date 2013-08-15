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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "scanner_user", schema = "scanner_registry")
public class ScannerUser implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id")
    private Integer userId;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Column(name = "hspc_documents")
    private String hspcDocuments;
    @Basic(optional = false)
    @Column(name = "phone")
    private String phone;
    @Column(name = "reports_to")
    private Integer reportsTo;
    @Basic(optional = false)
    @Column(name = "active")
    private boolean active;
    @Basic(optional = false)
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_initial")
    private String middleInitial;
    @Basic(optional = false)
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "pubmed_author_id")
    private String pubmedAuthorId;
    @Basic(optional = false)
    @Column(name = "is_superuser")
    private boolean isSuperuser;
    @ManyToMany(mappedBy = "scannerUserList")
    private List<ScannerRole> scannerRoleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataManagerId")
    private List<SourceDataWarehouse> sourceDataWarehouseList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connectivityManagerId")
    private List<SourceDataWarehouse> sourceDataWarehouseList1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "principalInvestigatorUid")
    private List<Study> studyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorUid")
    private List<DataSetDefinition> dataSetDefinitionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "curatorUid")
    private List<DataSetInstance> dataSetInstanceList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyOriginator")
    private List<AbstractPolicy> abstractPolicyList;

    public ScannerUser() {
    }

    public ScannerUser(Integer userId) {
        this.userId = userId;
    }

    public ScannerUser(Integer userId, String username, String email, String phone, boolean active, String firstName, String lastName, boolean isSuperuser) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isSuperuser = isSuperuser;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHspcDocuments() {
        return hspcDocuments;
    }

    public void setHspcDocuments(String hspcDocuments) {
        this.hspcDocuments = hspcDocuments;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Integer reportsTo) {
        this.reportsTo = reportsTo;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPubmedAuthorId() {
        return pubmedAuthorId;
    }

    public void setPubmedAuthorId(String pubmedAuthorId) {
        this.pubmedAuthorId = pubmedAuthorId;
    }

    public boolean getIsSuperuser() {
        return isSuperuser;
    }

    public void setIsSuperuser(boolean isSuperuser) {
        this.isSuperuser = isSuperuser;
    }

    public List<ScannerRole> getScannerRoleList() {
        return scannerRoleList;
    }

    public void setScannerRoleList(List<ScannerRole> scannerRoleList) {
        this.scannerRoleList = scannerRoleList;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouseList() {
        return sourceDataWarehouseList;
    }

    public void setSourceDataWarehouseList(List<SourceDataWarehouse> sourceDataWarehouseList) {
        this.sourceDataWarehouseList = sourceDataWarehouseList;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouseList1() {
        return sourceDataWarehouseList1;
    }

    public void setSourceDataWarehouseList1(List<SourceDataWarehouse> sourceDataWarehouseList1) {
        this.sourceDataWarehouseList1 = sourceDataWarehouseList1;
    }

    public List<Study> getStudyList() {
        return studyList;
    }

    public void setStudyList(List<Study> studyList) {
        this.studyList = studyList;
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

    public List<AbstractPolicy> getAbstractPolicyList() {
        return abstractPolicyList;
    }

    public void setAbstractPolicyList(List<AbstractPolicy> abstractPolicyList) {
        this.abstractPolicyList = abstractPolicyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScannerUser)) {
            return false;
        }
        ScannerUser other = (ScannerUser) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ScannerUser[ userId=" + userId + " ]";
    }
    
}
