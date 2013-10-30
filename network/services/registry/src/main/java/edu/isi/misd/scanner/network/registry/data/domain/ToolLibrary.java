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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *  @author Mike D'Arcy 
 */
@Entity
@Table(name = "tool_library", schema = "scanner_registry")
public class ToolLibrary implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "library_id")
    private Integer libraryId;
    @Basic(optional = false)
    @Column(name = "library_name")
    private String libraryName;
    @Basic(optional = false)
    @Column(name = "version")
    private String version;
    @Column(name = "description")
    private String description;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "toolParentLibrary")
    private List<AnalysisTool> analysisTools;

    public ToolLibrary() {
    }

    public ToolLibrary(Integer libraryId) {
        this.libraryId = libraryId;
    }

    public ToolLibrary(Integer libraryId, String libraryName, String version) {
        this.libraryId = libraryId;
        this.libraryName = libraryName;
        this.version = version;
    }

    public Integer getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Integer libraryId) {
        this.libraryId = libraryId;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AnalysisTool> getAnalysisTools() {
        return analysisTools;
    }

    public void setAnalysisTools(List<AnalysisTool> analysisTools) {
        this.analysisTools = analysisTools;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (libraryId != null ? libraryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ToolLibrary)) {
            return false;
        }
        ToolLibrary other = (ToolLibrary) object;
        if ((this.libraryId == null && other.libraryId != null) || (this.libraryId != null && !this.libraryId.equals(other.libraryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary[ libraryId=" + libraryId + " ]";
    }
    
}
