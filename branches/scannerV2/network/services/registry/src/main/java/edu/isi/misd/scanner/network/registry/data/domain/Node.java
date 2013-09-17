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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "node", schema = "scanner_registry")
public class Node implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "node_id")
    private Integer nodeId;
    @Basic(optional = false)
    @Column(name = "node_name")
    private String nodeName;    
    @Basic(optional = false)
    @Column(name = "host_url")
    private String hostUrl;
    @Basic(optional = false)
    @Column(name = "host_port")
    private int hostPort;
    @Basic(optional = false)
    @Column(name = "base_path")
    private String basePath;
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "is_master")
    private boolean isMaster;     
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    @ManyToOne(optional = false)
    private Site site;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "node")
    private List<DataSetInstance> dataSetInstances;    

    public Node() {
    }

    public Node(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Node(Integer nodeId, String nodeName, String hostUrl, int hostPort, String basePath) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;        
        this.hostUrl = hostUrl;
        this.hostPort = hostPort;
        this.basePath = basePath;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
    
    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(boolean isMaster) {
        this.isMaster = isMaster;
    }
    
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
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
        hash += (nodeId != null ? nodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Node)) {
            return false;
        }
        Node other = (Node) object;
        if ((this.nodeId == null && other.nodeId != null) || (this.nodeId != null && !this.nodeId.equals(other.nodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.Node[ nodeId=" + nodeId + " ]";
    }
    
}
