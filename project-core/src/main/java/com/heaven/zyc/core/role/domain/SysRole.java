package com.heaven.zyc.core.role.domain;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.support.entity.BaseEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午10:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "role")
public class SysRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "is_super_operator")
    private boolean superOperator = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoleStatus roleStatus;

    @OneToMany(mappedBy = "role", cascade = {CascadeType.ALL})
    @OrderBy("id asc")
  //  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RoleResource> roleResources = new LinkedHashSet<RoleResource>();

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuperOperator() {
        return superOperator;
    }

    public void setSuperOperator(boolean superOperator) {
        this.superOperator = superOperator;
    }

    public RoleStatus getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(RoleStatus roleStatus) {
        this.roleStatus = roleStatus;
    }

    public Set<RoleResource> getRoleResources() {
        return roleResources;
    }

    public void setRoleResources(Set<RoleResource> roleResources) {
        this.roleResources = roleResources;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 角色添加资源
     * @param resource
     */
    public void addResource(SysResource resource) {
        if (this.roleResources == null)
            this.roleResources = new LinkedHashSet<RoleResource>();
        for(RoleResource roleResource : roleResources) {
            if(roleResource.getResource().equals(resource))
                return;
        }
        RoleResource roleResource = new RoleResource ();
        roleResource.setRole(this);
        roleResource.setResource(resource);
        this.roleResources.add(roleResource);
    }
}
