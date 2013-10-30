/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.registry.data.domain; 

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "study_status_type", schema = "scanner_registry")
public class StudyStatusType implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "study_status_type_id")
    private Integer studyStatusTypeId;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "study_status_type_name")
    private String studyStatusTypeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyStatusType")
    private List<Study> studies;

    public StudyStatusType() {
    }

    public StudyStatusType(Integer studyStatusTypeId) {
        this.studyStatusTypeId = studyStatusTypeId;
    }

    public StudyStatusType(Integer studyStatusTypeId, String studyStatusTypeName, String description) {
        this.studyStatusTypeId = studyStatusTypeId;
        this.studyStatusTypeName = studyStatusTypeName;
        this.description = description;
    }

    public Integer getStudyStatusTypeId() {
        return studyStatusTypeId;
    }

    public void setStudyStatusTypeId(Integer studyStatusTypeId) {
        this.studyStatusTypeId = studyStatusTypeId;
    }

    public String getStudyStatusTypeName() {
        return studyStatusTypeName;
    }

    public void setStudyStatusTypeName(String studyStatusTypeName) {
        this.studyStatusTypeName = studyStatusTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studyStatusTypeId != null ? studyStatusTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyStatusType)) {
            return false;
        }
        StudyStatusType other = (StudyStatusType) object;
        if ((this.studyStatusTypeId == null && other.studyStatusTypeId != null) || (this.studyStatusTypeId != null && !this.studyStatusTypeId.equals(other.studyStatusTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.StudyStatusType[ studyStatusTypeId=" + studyStatusTypeId + " ]";
    }
    
}
