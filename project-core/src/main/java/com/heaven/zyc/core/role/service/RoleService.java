package com.heaven.zyc.core.role.service;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.pagination.Pagination;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:22
 * To change this template use File | Settings | File Templates.
 */
public interface RoleService {

    public SysRole getById(Integer id);

    public List<SysRole> getAll(Pagination pagination);

    public List<SysRole> getAll();

    public List<SysRole> getByParams(Map<String,Object> params);

    public void save(SysRole role, List<SysResource> resources);

    public void update(SysRole role, List<SysResource> resources) throws BusinessException;

    /**
     * Whether role's name is exist.
     * @param name
     * @return true:name is exist, false name is not exist
     */
    public boolean isExistByName(String name);

    public SysRole getByName(String name);

    /**
     * logic delete
     * @param role
     * @throws BusinessException
     */
    public void delete(SysRole role) throws BusinessException;

    public void freeze(SysRole role) throws BusinessException;
}
