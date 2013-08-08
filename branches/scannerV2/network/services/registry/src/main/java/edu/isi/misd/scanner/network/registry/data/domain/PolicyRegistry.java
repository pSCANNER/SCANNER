
package edu.isi.misd.scanner.network.registry.data.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "policy_registry", schema = "scanner_registry")
public class PolicyRegistry implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "policy_id")
    private Integer policyId;
    @Basic(optional = false)
    @Column(name = "authority_type")
    private String authorityType;
    @Basic(optional = false)
    @Column(name = "resource_type_governed")
    private String resourceTypeGoverned;
    @Basic(optional = false)
    @Column(name = "assertion")
    private String assertion;
    @Basic(optional = false)
    @Column(name = "data_resource_instance_resource_id")
    private int dataResourceInstanceResourceId;
    @Basic(optional = false)
    @Column(name = "data_resource_definition_id")
    private int dataResourceDefinitionId;
    @Basic(optional = false)
    @Column(name = "method_resource_id")
    private int methodResourceId;
    @Basic(optional = false)
    @Column(name = "data_source_id")
    private int dataSourceId;
    @Basic(optional = false)
    @Column(name = "study_id")
    private int studyId;
    @JoinColumn(name = "data_resource_confidentiality_level", referencedColumnName = "level_id")
    @ManyToOne(optional = false)
    private ConfidentialityLevels dataResourceConfidentialityLevel;

    public PolicyRegistry() {
    }

    public PolicyRegistry(Integer policyId) {
        this.policyId = policyId;
    }

    public PolicyRegistry(Integer policyId, String authorityType, String resourceTypeGoverned, String assertion, int dataResourceInstanceResourceId, int dataResourceDefinitionId, int methodResourceId, int dataSourceId, int studyId) {
        this.policyId = policyId;
        this.authorityType = authorityType;
        this.resourceTypeGoverned = resourceTypeGoverned;
        this.assertion = assertion;
        this.dataResourceInstanceResourceId = dataResourceInstanceResourceId;
        this.dataResourceDefinitionId = dataResourceDefinitionId;
        this.methodResourceId = methodResourceId;
        this.dataSourceId = dataSourceId;
        this.studyId = studyId;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getAuthorityType() {
        return authorityType;
    }

    public void setAuthorityType(String authorityType) {
        this.authorityType = authorityType;
    }

    public String getResourceTypeGoverned() {
        return resourceTypeGoverned;
    }

    public void setResourceTypeGoverned(String resourceTypeGoverned) {
        this.resourceTypeGoverned = resourceTypeGoverned;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public int getDataResourceInstanceResourceId() {
        return dataResourceInstanceResourceId;
    }

    public void setDataResourceInstanceResourceId(int dataResourceInstanceResourceId) {
        this.dataResourceInstanceResourceId = dataResourceInstanceResourceId;
    }

    public int getDataResourceDefinitionId() {
        return dataResourceDefinitionId;
    }

    public void setDataResourceDefinitionId(int dataResourceDefinitionId) {
        this.dataResourceDefinitionId = dataResourceDefinitionId;
    }

    public int getMethodResourceId() {
        return methodResourceId;
    }

    public void setMethodResourceId(int methodResourceId) {
        this.methodResourceId = methodResourceId;
    }

    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    public ConfidentialityLevels getDataResourceConfidentialityLevel() {
        return dataResourceConfidentialityLevel;
    }

    public void setDataResourceConfidentialityLevel(ConfidentialityLevels dataResourceConfidentialityLevel) {
        this.dataResourceConfidentialityLevel = dataResourceConfidentialityLevel;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (policyId != null ? policyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PolicyRegistry)) {
            return false;
        }
        PolicyRegistry other = (PolicyRegistry) object;
        if ((this.policyId == null && other.policyId != null) || (this.policyId != null && !this.policyId.equals(other.policyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.isi.misd.scanner.network.registry.data.domain.PolicyRegistry[ policyId=" + policyId + " ]";
    }
    
}
