package com.heaven.zyc.core.role.dao;

import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.role.domain.RoleResource;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.generic.BaseDaoHibernateImpl;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.SimpleParametersFinder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-21
 * Time: 下午9:22
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class RoleResourceDao extends BaseDaoHibernateImpl<RoleResource,Integer> {

    public List<RoleResource> getByRole(SysRole role){
        String hql = " from RoleResource r where r.role=:role";
        Finder finder = new SimpleParametersFinder(hql,"role",role);
        return find(finder);
    }
}
