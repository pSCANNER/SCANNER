package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "node_type", schema = "scanner_registry")
public class NodeType implements Serializable 
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "node_type_id")
    private Integer nodeTypeId;
    @Basic(optional = false)
    @Column(name = "node_type_name")
    private String nodeTypeName;
    @Basic(optional = false)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nodeTypeId")
    private List<Node> nodeList;

    public NodeType() {
    }

    public NodeType(Integer nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    public NodeType(Integer nodeTypeId, String nodeTypeName, String description) {
        this.nodeTypeId = nodeTypeId;
        this.nodeTypeName = nodeTypeName;
        this.description = description;
    }

    public Integer getNodeTypeId() {
        return nodeTypeId;
    }

    public void setNodeTypeId(Integer nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public void setNodeTypeName(String nodeTypeName) {
        this.nodeTypeName = nodeTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nodeTypeId != null ? nodeTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NodeType)) {
            return false;
        }
        NodeType other = (NodeType) object;
        if ((this.nodeTypeId == null && other.nodeTypeId != null) || (this.nodeTypeId != null && !this.nodeTypeId.equals(other.nodeTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.NodeType[ nodeTypeId=" + nodeTypeId + " ]";
    }
    
}
