package com.heaven.zyc.service;

import com.heaven.zyc.base.SpringTest4Abstract;
import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.core.operator.service.OperatorService;
import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.resource.service.ResourceGroupService;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.domain.RoleResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * User: heavenzyc
 * Date: 14-4-9
 * Time: 下午10:48
 * To change this template use File | Settings | File Templates.
 */

public class OperatorServiceTest extends SpringTest4Abstract {

    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ResourceGroupService resourceGroupService;

    @Test
    public void getAllResourceGroupTest(){
        List<ResourceGroup> resourceGroups = resourceGroupService.getAll();
        System.out.print(resourceGroups);
    }

    @Test
    public void getOperatorTest(){
        Operator operator = operatorService.getBy(1);
        Map<ResourceGroup,List<SysResource>> resourceGroupListMap = operator.getResourceGroupListMap();
        List<SysResource> resourceList = new ArrayList<SysResource>();
        SysRole role = operator.getRole();
        Set<RoleResource> roleResources = role.getRoleResources();
        Iterator<RoleResource> iterator = roleResources.iterator();
        while (iterator.hasNext()){
            RoleResource roleResource = iterator.next();
            SysResource resource = roleResource.getResource();
            ResourceGroup group = resource.getResourceGroup();
            if (resourceGroupListMap.get(group) != null){

            }
        }
    }
}
