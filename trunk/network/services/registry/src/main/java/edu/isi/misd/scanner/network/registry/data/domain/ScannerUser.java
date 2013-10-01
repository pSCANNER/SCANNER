package edu.isi.misd.scanner.network.registry.data.domain;

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
    @Column(name = "user_name")
    private String userName;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Column(name = "hspc_documents")
    private String hspcDocuments;
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
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<UserRole> userRoles;      
    @JsonIgnore
    @ManyToMany(mappedBy = "scannerUsers")
    private List<StudyRole> studyRoles;   
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyOwner")
    private List<Study> studies;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<DataSetDefinition> dataSetDefinitions;
    @JsonIgnore    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "policyOriginator")
    private List<StudyPolicyStatement> studyPolicyStatements;    
    
    public ScannerUser() {
    }

    public ScannerUser(Integer userId) {
        this.userId = userId;
    }

    public ScannerUser(Integer userId, String userName, String email, boolean active, String firstName, String lastName, boolean isSuperuser) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
    
    public List<StudyRole> getStudyRoles() {
        return studyRoles;
    }

    public void setStudyRoles(List<StudyRole> studyRoles) {
        this.studyRoles = studyRoles;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    public List<DataSetDefinition> getDataSetDefinitions() {
        return dataSetDefinitions;
    }

    public void setDataSetDefinitions(List<DataSetDefinition> dataSetDefinitions) {
        this.dataSetDefinitions = dataSetDefinitions;
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
