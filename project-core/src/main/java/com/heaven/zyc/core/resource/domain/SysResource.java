package com.heaven.zyc.core.resource.domain;

import com.heaven.zyc.support.entity.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午10:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "resource")
public class SysResource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String path;

    @Column
    private int sort;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ResourceGroup resourceGroup;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public ResourceGroup getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(ResourceGroup resourceGroup) {
        this.resourceGroup = resourceGroup;
    }
}
