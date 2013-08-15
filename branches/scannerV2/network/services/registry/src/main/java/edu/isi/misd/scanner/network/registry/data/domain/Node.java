package edu.isi.misd.scanner.network.registry.data.domain;

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
    @Column(name = "host_name")
    private String hostName;
    @Basic(optional = false)
    @Column(name = "host_port")
    private int hostPort;
    @Basic(optional = false)
    @Column(name = "base_path")
    private String basePath;
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nodeId")
    private List<DataSetInstance> dataSetInstanceList;
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    @ManyToOne(optional = false)
    private Site siteId;
    @JoinColumn(name = "node_type_id", referencedColumnName = "node_type_id")
    @ManyToOne(optional = false)
    private NodeType nodeTypeId;

    public Node() {
    }

    public Node(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Node(Integer nodeId, String hostName, int hostPort, String basePath) {
        this.nodeId = nodeId;
        this.hostName = hostName;
        this.hostPort = hostPort;
        this.basePath = basePath;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public List<DataSetInstance> getDataSetInstanceList() {
        return dataSetInstanceList;
    }

    public void setDataSetInstanceList(List<DataSetInstance> dataSetInstanceList) {
        this.dataSetInstanceList = dataSetInstanceList;
    }

    public Site getSiteId() {
        return siteId;
    }

    public void setSiteId(Site siteId) {
        this.siteId = siteId;
    }

    public NodeType getNodeTypeId() {
        return nodeTypeId;
    }

    public void setNodeTypeId(NodeType nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
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
