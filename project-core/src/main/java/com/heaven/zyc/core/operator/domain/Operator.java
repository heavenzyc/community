package com.heaven.zyc.core.operator.domain;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.support.entity.BaseEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-9
 * Time: 下午10:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "operator")
public class Operator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    //账号
    @Column
    private String account;

    //密码
    @Column
    private String password;

    //密钥
    @Column(name = "password_key")
    private String key;

    @Column
    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;


    @Column
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private SysRole role;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OperatorStatus operatorStatus;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Transient
    private Map<ResourceGroup,List<SysResource>> resourceGroupListMap = new HashMap<ResourceGroup, List<SysResource>>();

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SysRole getRole() {
        return role;
    }

    public void setRole(SysRole role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OperatorStatus getOperatorStatus() {
        return operatorStatus;
    }

    public void setOperatorStatus(OperatorStatus operatorStatus) {
        this.operatorStatus = operatorStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Map<ResourceGroup, List<SysResource>> getResourceGroupListMap() {
        return resourceGroupListMap;
    }

    public void setResourceGroupListMap(Map<ResourceGroup, List<SysResource>> resourceGroupListMap) {
        this.resourceGroupListMap = resourceGroupListMap;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
