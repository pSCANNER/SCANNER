
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "users", schema = "scanner_registry")
public class Users implements Serializable
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
    @Basic(optional = false)
    @Column(name = "hspc_documents")
    private String hspcDocuments;
    @Basic(optional = false)
    @Column(name = "primary_affliation")
    private int primaryAffliation;
    @Basic(optional = false)
    @Column(name = "secondary_affliliation")
    private int secondaryAffliliation;
    @Basic(optional = false)
    @Column(name = "phone")
    private String phone;
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
    @ManyToMany(mappedBy = "usersList")
    private List<Roles> rolesList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataManagerId")
    private List<SourceDataWarehouse> sourceDataWarehouseListAsDataManager;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "connectivityManagerId")
    private List<SourceDataWarehouse> sourceDataWarehouseListAsConnectivityManager;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reportsTo")
    private List<Users> usersList;
    @JoinColumn(name = "reports_to", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users reportsTo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "principalInvestigatorUid")
    private List<Study> studyList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorUid")
    private List<DataSetDefinition> dataSetDefinitionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "curatorUid")
    private List<DataSetInstance> dataSetInstanceList;

    public Users() {
    }

    public Users(Integer userId) {
        this.userId = userId;
    }

    public Users(Integer userId, String username, String email, String hspcDocuments, int primaryAffliation, int secondaryAffliliation, String phone, boolean active, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.hspcDocuments = hspcDocuments;
        this.primaryAffliation = primaryAffliation;
        this.secondaryAffliliation = secondaryAffliliation;
        this.phone = phone;
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public int getPrimaryAffliation() {
        return primaryAffliation;
    }

    public void setPrimaryAffliation(int primaryAffliation) {
        this.primaryAffliation = primaryAffliation;
    }

    public int getSecondaryAffliliation() {
        return secondaryAffliliation;
    }

    public void setSecondaryAffliliation(int secondaryAffliliation) {
        this.secondaryAffliliation = secondaryAffliliation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public List<Roles> getRolesList() {
        return rolesList;
    }

    public void setRolesList(List<Roles> rolesList) {
        this.rolesList = rolesList;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouseListDataManager() {
        return sourceDataWarehouseListAsDataManager;
    }

    public void setSourceDataWarehouseList(
        List<SourceDataWarehouse> sourceDataWarehouseListAsDataManager) {
        this.sourceDataWarehouseListAsDataManager = 
            sourceDataWarehouseListAsDataManager;
    }

    public List<SourceDataWarehouse> getSourceDataWarehouseListConnectivityManager() {
        return sourceDataWarehouseListAsConnectivityManager;
    }

    public void setSourceDataWarehouseListConnectivityManager(
        List<SourceDataWarehouse> sourceDataWarehouseListAsConnectivityManager) {
        this.sourceDataWarehouseListAsConnectivityManager = 
            sourceDataWarehouseListAsConnectivityManager;
    }

    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }

    public Users getReportsTo() {
        return reportsTo;
    }

    public void setReportsTo(Users reportsTo) {
        this.reportsTo = reportsTo;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.Users[ userId=" + userId + " ]";
    }
    
}
