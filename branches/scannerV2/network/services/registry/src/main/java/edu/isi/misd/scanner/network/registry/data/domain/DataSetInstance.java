
package edu.isi.misd.scanner.network.registry.data.domain;

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
@Table(name = "data_set_instance", schema = "scanner_registry")
public class DataSetInstance implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "data_set_instance_id")
    private Integer dataSetInstanceId;
    @Basic(optional = false)
    @Column(name = "data_set_instance_location")
    private String dataSetInstanceLocation;
    @Column(name = "data_slice_id")
    private Integer dataSliceId;
    @JoinColumn(name = "curator_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users curatorUid;
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study studyId;
    @JoinColumn(name = "source_data_warehouse_id", referencedColumnName = "source_data_warehouse_id")
    @ManyToOne(optional = false)
    private SourceDataWarehouse sourceDataWarehouseId;
    @JoinColumn(name = "data_set_definition_id", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinitionId;

    public DataSetInstance() {
    }

    public DataSetInstance(Integer dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public DataSetInstance(Integer dataSetInstanceId, String dataSetInstanceLocation) {
        this.dataSetInstanceId = dataSetInstanceId;
        this.dataSetInstanceLocation = dataSetInstanceLocation;
    }

    public Integer getDataSetInstanceId() {
        return dataSetInstanceId;
    }

    public void setDataSetInstanceId(Integer dataSetInstanceId) {
        this.dataSetInstanceId = dataSetInstanceId;
    }

    public String getDataSetInstanceLocation() {
        return dataSetInstanceLocation;
    }

    public void setDataSetInstanceLocation(String dataSetInstanceLocation) {
        this.dataSetInstanceLocation = dataSetInstanceLocation;
    }

    public Integer getDataSliceId() {
        return dataSliceId;
    }

    public void setDataSliceId(Integer dataSliceId) {
        this.dataSliceId = dataSliceId;
    }

    public Users getCuratorUid() {
        return curatorUid;
    }

    public void setCuratorUid(Users curatorUid) {
        this.curatorUid = curatorUid;
    }

    public Study getStudyId() {
        return studyId;
    }

    public void setStudyId(Study studyId) {
        this.studyId = studyId;
    }

    public SourceDataWarehouse getSourceDataWarehouseId() {
        return sourceDataWarehouseId;
    }

    public void setSourceDataWarehouseId(SourceDataWarehouse sourceDataWarehouseId) {
        this.sourceDataWarehouseId = sourceDataWarehouseId;
    }

    public DataSetDefinition getDataSetDefinitionId() {
        return dataSetDefinitionId;
    }

    public void setDataSetDefinitionId(DataSetDefinition dataSetDefinitionId) {
        this.dataSetDefinitionId = dataSetDefinitionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dataSetInstanceId != null ? dataSetInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataSetInstance)) {
            return false;
        }
        DataSetInstance other = (DataSetInstance) object;
        if ((this.dataSetInstanceId == null && other.dataSetInstanceId != null) || (this.dataSetInstanceId != null && !this.dataSetInstanceId.equals(other.dataSetInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.DataSetInstance[ dataSetInstanceId=" + dataSetInstanceId + " ]";
    }
    
}
