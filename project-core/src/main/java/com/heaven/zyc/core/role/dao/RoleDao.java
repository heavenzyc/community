package com.heaven.zyc.core.role.dao;

import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.generic.BaseDaoHibernateImpl;
import com.heaven.zyc.generic.finder.DynamicFinder;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.pagination.Pagination;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:21
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class RoleDao extends BaseDaoHibernateImpl<SysRole,Integer> {

    public List<SysRole> getByParams(Map<String,Object> params, Pagination pagination){
        String hql = " from SysRole where 1=1 " +
                " { and name=:name } " +
                " { and status!='DELETE' } " +
                " { and superOperator=:superOperator }" +
                " order by id ";
        Finder finder = new DynamicFinder(hql,params);
        return find(finder,pagination);
    }

    public List<SysRole> getByParams(Map<String,Object> params){
        String hql = " from SysRole where 1=1 " +
                " { and name=:name } " +
                " { and status!='DELETE' } " +
                " { and superOperator=:superOperator }" +
                " order by id ";
        Finder finder = new DynamicFinder(hql,params);
        return find(finder);
    }
}
