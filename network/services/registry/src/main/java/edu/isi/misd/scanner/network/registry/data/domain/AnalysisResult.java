package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "analysis_result", schema = "scanner_registry")
public class AnalysisResult implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "result_id")
    private Integer resultId;
    @Basic(optional = false)
    @Column(name = "result_status")
    private String resultStatus;
    @Column(name = "result_status_detail")
    private String resultStatusDetail;
    @Basic(optional = false)
    @Column(name = "result_url")
    private String resultUrl;
    @JsonIgnore
    @JoinColumn(name = "analysis_id", referencedColumnName = "analysis_id")
    @ManyToOne(optional = false)
    private AnalysisInstance analysisInstance;

    public AnalysisResult() {
    }

    public AnalysisResult(Integer resultId) {
        this.resultId = resultId;
    }

    public AnalysisResult(Integer resultId, String resultStatus, String resultUrl) {
        this.resultId = resultId;
        this.resultStatus = resultStatus;
        this.resultUrl = resultUrl;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResultStatusDetail() {
        return resultStatusDetail;
    }

    public void setResultStatusDetail(String resultStatusDetail) {
        this.resultStatusDetail = resultStatusDetail;
    }
    
    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public AnalysisInstance getAnalysisInstance() {
        return analysisInstance;
    }

    public void setAnalysisInstance(AnalysisInstance analysisInstance) {
        this.analysisInstance = analysisInstance;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (resultId != null ? resultId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnalysisResult)) {
            return false;
        }
        AnalysisResult other = (AnalysisResult) object;
        if ((this.resultId == null && other.resultId != null) || (this.resultId != null && !this.resultId.equals(other.resultId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.AnalysisInstanceResult[ resultId=" + resultId + " ]";
    }
    
}
