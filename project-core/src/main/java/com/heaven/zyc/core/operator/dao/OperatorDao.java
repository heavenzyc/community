package com.heaven.zyc.core.operator.dao;

import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.generic.BaseDaoHibernateImpl;
import com.heaven.zyc.generic.finder.DynamicFinder;
import com.heaven.zyc.generic.finder.Finder;
import com.heaven.zyc.generic.finder.SimpleParametersFinder;
import com.heaven.zyc.pagination.Pagination;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:13
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class OperatorDao extends BaseDaoHibernateImpl<Operator, Integer> {

    public Operator getByPassword(String account,String password){
        String hql = " from Operator where account=:account and password=:password";
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("account",account);
        params.put("password",password);
        Finder finder = new SimpleParametersFinder(hql,params);
        return getBy(finder);
    }

    public List<Operator> findByParams(Map<String,Object> params,Pagination pagination){
        String hql = " from Operator o where 1=1 " +
                " { and o.name=:name } " +
                " { and o.account=:account } " +
                " { and o.mobile=:mobile } " +
                " { and o.gender=:gender } " +
                " { and o.email=:email } " +
                " { and o.operatorStatus=:operatorStatus} " +
                " order by createTime desc";
        Finder finder = new DynamicFinder(hql,params);
        return find(finder,pagination);
    }

    public List<Operator> findNotDeleteOperator(Map<String,Object> params,Pagination pagination){
        String hql = " from Operator o where 1=1 " +
                " { and o.name=:name } " +
                " { and o.account=:account } " +
                " { and o.mobile=:mobile } " +
                " { and o.gender=:gender } " +
                " { and o.email=:email } " +
                " { and o.operatorStatus!='DELETE'} " +
                " order by createTime desc";
        Finder finder = new DynamicFinder(hql,params);
        return find(finder,pagination);
    }
}
