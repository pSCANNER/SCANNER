package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "source_data_warehouse", schema = "scanner_registry")
public class SourceDataWarehouse implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "source_data_warehouse_id")
    private Integer sourceDataWarehouseId;
    @Basic(optional = false)
    @Column(name = "schema_documentation")
    private String schemaDocumentation;
    @Basic(optional = false)
    @Column(name = "etl_documentation")
    private String etlDocumentation;
    @Basic(optional = false)
    @Column(name = "etl_programs")
    private String etlPrograms;
    @JsonIgnore    
    @JoinTable(name = "study_data_warehouse", joinColumns = {
        @JoinColumn(name = "source_data_warehouse_id", referencedColumnName = "source_data_warehouse_id")}, inverseJoinColumns = {
        @JoinColumn(name = "study_id", referencedColumnName = "study_id")})    
    @ManyToMany
    private List<Study> studies;
    @JsonIgnore
    @JoinColumn(name = "data_manager_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser dataManager;
    @JsonIgnore    
    @JoinColumn(name = "connectivity_manager_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser connectivityManager;
    @JsonIgnore    
    @JoinColumn(name = "data_warehouse_confidentiality_level", referencedColumnName = "level_id")
    @ManyToOne(optional = false)
    private ConfidentialityLevel dataWarehouseConfidentialityLevel;
    @JsonIgnore    
    @OneToMany(mappedBy = "sourceDataWarehouse")
    private List<DataSetInstance> dataSetInstances;

    public SourceDataWarehouse() {
    }

    public SourceDataWarehouse(Integer sourceDataWarehouseId) {
        this.sourceDataWarehouseId = sourceDataWarehouseId;
    }

    public SourceDataWarehouse(Integer sourceDataWarehouseId, String schemaDocumentation, String etlDocumentation, String etlPrograms) {
        this.sourceDataWarehouseId = sourceDataWarehouseId;
        this.schemaDocumentation = schemaDocumentation;
        this.etlDocumentation = etlDocumentation;
        this.etlPrograms = etlPrograms;
    }

    public Integer getSourceDataWarehouseId() {
        return sourceDataWarehouseId;
    }

    public void setSourceDataWarehouseId(Integer sourceDataWarehouseId) {
        this.sourceDataWarehouseId = sourceDataWarehouseId;
    }

    public String getSchemaDocumentation() {
        return schemaDocumentation;
    }

    public void setSchemaDocumentation(String schemaDocumentation) {
        this.schemaDocumentation = schemaDocumentation;
    }

    public String getEtlDocumentation() {
        return etlDocumentation;
    }

    public void setEtlDocumentation(String etlDocumentation) {
        this.etlDocumentation = etlDocumentation;
    }

    public String getEtlPrograms() {
        return etlPrograms;
    }

    public void setEtlPrograms(String etlPrograms) {
        this.etlPrograms = etlPrograms;
    }

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

    public ScannerUser getDataManager() {
        return dataManager;
    }

    public void setDataManager(ScannerUser dataManager) {
        this.dataManager = dataManager;
    }

    public ScannerUser getConnectivityManager() {
        return connectivityManager;
    }

    public void setConnectivityManager(ScannerUser connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public ConfidentialityLevel getDataWarehouseConfidentialityLevel() {
        return dataWarehouseConfidentialityLevel;
    }

    public void setDataWarehouseConfidentialityLevel(ConfidentialityLevel dataWarehouseConfidentialityLevel) {
        this.dataWarehouseConfidentialityLevel = dataWarehouseConfidentialityLevel;
    }

    public List<DataSetInstance> getDataSetInstances() {
        return dataSetInstances;
    }

    public void setDataSetInstances(List<DataSetInstance> dataSetInstances) {
        this.dataSetInstances = dataSetInstances;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sourceDataWarehouseId != null ? sourceDataWarehouseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SourceDataWarehouse)) {
            return false;
        }
        SourceDataWarehouse other = (SourceDataWarehouse) object;
        if ((this.sourceDataWarehouseId == null && other.sourceDataWarehouseId != null) || (this.sourceDataWarehouseId != null && !this.sourceDataWarehouseId.equals(other.sourceDataWarehouseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.SourceDataWarehouse[ sourceDataWarehouseId=" + sourceDataWarehouseId + " ]";
    }
    
}
