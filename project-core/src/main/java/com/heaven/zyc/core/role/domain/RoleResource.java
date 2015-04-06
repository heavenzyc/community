package com.heaven.zyc.core.role.domain;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.support.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "role_resource")
public class RoleResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
  //  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private SysRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
 //   @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private SysResource resource;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SysRole getRole() {
        return role;
    }

    public void setRole(SysRole role) {
        this.role = role;
    }

    public SysResource getResource() {
        return resource;
    }

    public void setResource(SysResource resource) {
        this.resource = resource;
    }
}
