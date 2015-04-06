package com.heaven.zyc.core.role.service;

import com.heaven.zyc.core.role.domain.RoleResource;
import com.heaven.zyc.core.role.domain.SysRole;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-21
 * Time: 下午9:20
 * To change this template use File | Settings | File Templates.
 */
public interface RoleResourceService {

    public List<RoleResource> getListByRole(SysRole role);
}
