package com.heaven.zyc.core.resource.dao;

import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.generic.BaseDaoHibernateImpl;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.SimpleParametersFinder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:18
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ResourceGroupDao extends BaseDaoHibernateImpl<ResourceGroup,Integer> {

    public List<ResourceGroup> getAll(){
        String hql = " from ResourceGroup order by sort";
        Finder finder = new SimpleParametersFinder(hql,null);
        return find(finder);
    }
}
