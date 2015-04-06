package com.heaven.zyc.core.role.service.impl;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.role.dao.RoleDao;
import com.heaven.zyc.core.role.dao.RoleResourceDao;
import com.heaven.zyc.core.role.domain.RoleResource;
import com.heaven.zyc.core.role.domain.RoleStatus;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.service.RoleService;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.pagination.Pagination;
import com.heaven.zyc.utils.ObjectComparatorUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao ;

    @Resource
    private RoleResourceDao roleResourceDao;

    @Override
    public SysRole getById(Integer id) {
        return roleDao.get(id);
    }

    @Override
    public List<SysRole> getAll(Pagination pagination) {
        return roleDao.getByParams(new HashMap<String, Object>(),pagination);
    }

    @Override
    public List<SysRole> getAll() {
        return roleDao.getByParams(new HashMap<String, Object>());
    }

    @Override
    public List<SysRole> getByParams(Map<String, Object> params) {
        return roleDao.getByParams(params);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void save(SysRole role, List<SysResource> resources) {
        role.setSuperOperator(false);
        String[][] roleSort = new String[][] { new String[] { "sort", ObjectComparatorUtils.ASCE } };
        Collections.sort(resources, new ObjectComparatorUtils(SysRole.class, roleSort));
        for(SysResource resource :resources) {
            role.addResource(resource);
        }
        roleDao.save(role);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update(SysRole role, List<SysResource> resources) throws BusinessException {
        if(role.isSuperOperator()) {
            throw new BusinessException("System role can't update！");
        }
        String[][] roleSort = new String[][] { new String[] { "sort", ObjectComparatorUtils.ASCE } };
        Collections.sort(resources, new ObjectComparatorUtils(SysRole.class, roleSort));
        removeRoleResource(resources, role);
        addRoleResource(role, resources);
    }
    /**
     * 移除需要删除的资源
     * @param resources
     * @param role
     */
    private void removeRoleResource(List<SysResource> resources, SysRole role) {
        Set<RoleResource> oldRoleResources = role.getRoleResources();
        List<RoleResource> needRemove = new ArrayList<RoleResource>();
        for (RoleResource roleResource : oldRoleResources) {
            SysResource oldResource = roleResource.getResource();
            if (!resources.contains(oldResource)) {
                needRemove.add(roleResource);
            }
        }

        for (RoleResource roleResource : needRemove) {
            role.getRoleResources().remove(roleResource);
            roleResourceDao.delete(roleResource);
        }
    }
    private void addRoleResource(SysRole role, List<SysResource> resources) {
        Set<RoleResource> oldRoleResources = role.getRoleResources();

        for (SysResource resource : resources) {
            boolean needAdd = true;
            for (RoleResource roleResource : oldRoleResources) {
                if (roleResource.getResource().equals(resource)) {
                    needAdd = false;
                    break;
                }
            }
            if (needAdd) {
                role.addResource(resource);
            }
        }
    }

    @Override
    public boolean isExistByName(String name) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("name",name);
        List<SysRole> roles = roleDao.getByParams(params);
        if (roles.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public SysRole getByName(String name) {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("name",name);
        List<SysRole> roles = roleDao.getByParams(params);
        if (roles.size() > 0){
            return roles.get(0);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(SysRole role) throws BusinessException{
        if (role.isSuperOperator()){
            throw new BusinessException("System role can't delete!");
        }
        role.setRoleStatus(RoleStatus.DELETE);
        role.setUpdateTime(new Date());
        roleDao.update(role);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void freeze(SysRole role) throws BusinessException {
        if (role.isSuperOperator()){
            throw new BusinessException("System role can't freeze!");
        }
        if (role.getRoleStatus().equals(RoleStatus.FREEZE)){
            role.setRoleStatus(RoleStatus.ACTIVE);
        }else {
            role.setRoleStatus(RoleStatus.FREEZE);
        }
        role.setUpdateTime(new Date());
        roleDao.update(role);
    }
}
