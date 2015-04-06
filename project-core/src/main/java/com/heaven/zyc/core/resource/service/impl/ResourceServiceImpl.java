package com.heaven.zyc.core.resource.service.impl;

import com.heaven.zyc.core.resource.dao.ResourceDao;
import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.resource.service.ResourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:20
 * To change this template use File | Settings | File Templates.
 */
@Service(value = "resourceService")
public class ResourceServiceImpl implements ResourceService {
    @Resource
    private ResourceDao resourceDao;
    @Override
    public List<SysResource> getByResourceGroup(ResourceGroup resourceGroup) {
        return resourceDao.getByGroup(resourceGroup);
    }

    @Override
    public List<SysResource> getAllResource() {
        return resourceDao.getAll();
    }

    @Override
    public SysResource getById(Integer id) {
        return resourceDao.get(id);
    }
}
