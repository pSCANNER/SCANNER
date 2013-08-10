package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
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
    @Column(name = "library_version")
    private String libraryVersion;
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "toolParentLibraryId", fetch=FetchType.EAGER)
    @JsonManagedReference("tool_library-analysis_tool")
    private List<AnalysisTool> analysisTool;

    public ToolLibrary() {
    }

    public ToolLibrary(Integer libraryId) {
        this.libraryId = libraryId;
    }

    public ToolLibrary(Integer libraryId, String libraryName, String libraryVersion) {
        this.libraryId = libraryId;
        this.libraryName = libraryName;
        this.libraryVersion = libraryVersion;
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

    public String getLibraryVersion() {
        return libraryVersion;
    }

    public void setLibraryVersion(String libraryVersion) {
        this.libraryVersion = libraryVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AnalysisTool> getAnalysisToolList() {
        return analysisTool;
    }

    public void setAnalysisToolList(List<AnalysisTool> analysisToolList) {
        this.analysisTool = analysisToolList;
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