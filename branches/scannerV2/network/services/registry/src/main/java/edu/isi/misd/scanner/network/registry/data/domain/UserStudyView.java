package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "user_study_view", schema = "scanner_registry")
public class UserStudyView implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)    
    @Column(name = "user_study_id")
    private String userStudyId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "study_id")
    private Integer studyId;
    @Column(name = "study_name")
    private String studyName;

    public UserStudyView() {
    }

    public String getUserStudyId() {
        return userStudyId;
    }

    public void setUserStudyId(String userStudyId) {
        this.userStudyId = userStudyId;
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
    
}
