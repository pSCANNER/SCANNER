package edu.isi.misd.scanner.network.registry.data.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dataSetInstance")
    private Set<PolicyStatement> policyStatements;
    @JsonIgnore
    @JoinColumn(name = "study_id", referencedColumnName = "study_id")
    @ManyToOne(optional = false)
    private Study study;
    @JoinColumn(name = "source_data_warehouse_id", referencedColumnName = "source_data_warehouse_id")
    @ManyToOne
    private SourceDataWarehouse sourceDataWarehouse;
    @JsonIgnore
    @JoinColumn(name = "curator_uid", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private ScannerUser curator;
    @JoinColumn(name = "node_id", referencedColumnName = "node_id")
    @ManyToOne(optional = false)
    private Node node;
    @JsonBackReference("DataSetDefinition-DataSetInstance")    
    @JoinColumn(name = "data_set_definition_id", referencedColumnName = "data_set_definition_id")
    @ManyToOne(optional = false)
    private DataSetDefinition dataSetDefinition;

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

    public Set<PolicyStatement> getPolicyStatements() {
        return policyStatements;
    }

    public void setPolicyStatementList(Set<PolicyStatement> policyStatements) {
        this.policyStatements = policyStatements;
    }

    public Study getStudy() {
        return study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public SourceDataWarehouse getSourceDataWarehouse() {
        return sourceDataWarehouse;
    }

    public void setSourceDataWarehouse(SourceDataWarehouse sourceDataWarehouse) {
        this.sourceDataWarehouse = sourceDataWarehouse;
    }

    public ScannerUser getCurator() {
        return curator;
    }

    public void setCurator(ScannerUser curator) {
        this.curator = curator;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public DataSetDefinition getDataSetDefinition() {
        return dataSetDefinition;
    }

    public void setDataSetDefinition(DataSetDefinition dataSetDefinition) {
        this.dataSetDefinition = dataSetDefinition;
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
