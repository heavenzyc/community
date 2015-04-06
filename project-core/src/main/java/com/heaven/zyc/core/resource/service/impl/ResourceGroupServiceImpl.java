package com.heaven.zyc.core.resource.service.impl;

import com.heaven.zyc.core.resource.dao.ResourceGroupDao;
import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.resource.service.ResourceGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:20
 * To change this template use File | Settings | File Templates.
 */
@Service

public class ResourceGroupServiceImpl implements ResourceGroupService {
    @Resource
    private ResourceGroupDao resourceGroupDao;

    @Override
    @Transactional(readOnly = true,propagation = Propagation.REQUIRED)
    public List<ResourceGroup> getAll() {
        return resourceGroupDao.getAll();
    }
}
