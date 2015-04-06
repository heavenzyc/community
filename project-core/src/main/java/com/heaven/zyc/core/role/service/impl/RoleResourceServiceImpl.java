package com.heaven.zyc.core.role.service.impl;

import com.heaven.zyc.core.role.dao.RoleResourceDao;
import com.heaven.zyc.core.role.domain.RoleResource;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.service.RoleResourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-21
 * Time: 下午9:21
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RoleResourceServiceImpl implements RoleResourceService {
    @Resource
    private RoleResourceDao roleResourceDao;

    @Override
    public List<RoleResource> getListByRole(SysRole role) {
        return roleResourceDao.getByRole(role);
    }
}
