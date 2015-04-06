package com.heaven.zyc.core.resource.service;

import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.resource.domain.SysResource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:19
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceService {

    List<SysResource> getByResourceGroup(ResourceGroup resourceGroup);

    List<SysResource> getAllResource();

    SysResource getById(Integer id);
}
