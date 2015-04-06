package com.heaven.zyc.core.operator.service;

import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.pagination.Pagination;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:16
 * To change this template use File | Settings | File Templates.
 */
public interface OperatorService {

    public Operator getBy(Integer id);

    public Operator login(String account,String password)throws BusinessException;

    public List<Operator> getAllOperator(Pagination pagination);

    public void save(Operator operator);

    public void update(Operator operator);

    public void delete(Operator operator);

    public void freeze(Operator operator);

    public Operator findByAccount(String account);
}
