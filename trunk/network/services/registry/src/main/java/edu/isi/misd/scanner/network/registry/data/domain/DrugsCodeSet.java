package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 */
@Entity
@Table(name = "drugs_code_set", schema = "scanner_registry")
public class DrugsCodeSet implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "concept_id")
    private Integer conceptId;
    @Basic(optional = false)
    @Column(name = "concept_name")
    private String conceptName;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "valid_start_date")
    @Temporal(TemporalType.DATE)
    private Date validStartDate;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "valid_end_date")
    @Temporal(TemporalType.DATE)
    private Date validEndDate;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "concept_level")
    private Integer conceptLevel;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "concept_class")
    private String conceptClass;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "vocabulary_id")
    private Integer vocabularyId;
    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "concept_code")
    private String conceptCode;

    public DrugsCodeSet() {
    }

    public DrugsCodeSet(Integer conceptId) {
        this.conceptId = conceptId;
    }

    public DrugsCodeSet(Integer conceptId, String conceptName, Date validStartDate, Date validEndDate, Integer conceptLevel, String conceptClass, Integer vocabularyId, String conceptCode) {
        this.conceptId = conceptId;
        this.conceptName = conceptName;
        this.validStartDate = validStartDate;
        this.validEndDate = validEndDate;
        this.conceptLevel = conceptLevel;
        this.conceptClass = conceptClass;
        this.vocabularyId = vocabularyId;
        this.conceptCode = conceptCode;
    }

    public Integer getConceptId() {
        return conceptId;
    }

    public void setConceptId(Integer conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public Date getValidStartDate() {
        return validStartDate;
    }

    public void setValidStartDate(Date validStartDate) {
        this.validStartDate = validStartDate;
    }

    public Date getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(Date validEndDate) {
        this.validEndDate = validEndDate;
    }

    public Integer getConceptLevel() {
        return conceptLevel;
    }

    public void setConceptLevel(Integer conceptLevel) {
        this.conceptLevel = conceptLevel;
    }

    public String getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }

    public Integer getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Integer vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (conceptId != null ? conceptId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DrugsCodeSet)) {
            return false;
        }
        DrugsCodeSet other = (DrugsCodeSet) object;
        if ((this.conceptId == null && other.conceptId != null) || (this.conceptId != null && !this.conceptId.equals(other.conceptId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DrugsCodeSet[ conceptId=" + conceptId + " ]";
    }
    
}
