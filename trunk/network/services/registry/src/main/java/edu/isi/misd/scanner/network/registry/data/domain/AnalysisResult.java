package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @Column(name = "status")
    private String status;
    @Column(name = "status_detail")
    private String statusDetail;
    @Basic(optional = false)
    @Column(name = "url")
    private String url;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="dataSetInstanceId")
    @JsonIdentityReference(alwaysAsId=true)  
    @Basic(optional = false)
    @Column(name = "data_set_instance_id")
    private int dataSetInstanceId;    
    @JsonIgnore
    @JoinColumn(name = "analysis_id", referencedColumnName = "analysis_id")
    @ManyToOne(optional = false)
    private AnalysisInstance analysisInstance;

    public AnalysisResult() {
    }

    public AnalysisResult(Integer resultId) {
        this.resultId = resultId;
    }

    public AnalysisResult(Integer resultId, String status, String url) {
        this.resultId = resultId;
        this.status = status;
        this.url = url;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public int getDataSetInstanceId() {
        return dataSetInstanceId;
    }

    public void setDataSetInstanceId(int dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
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
