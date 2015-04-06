package com.heaven.zyc.core.operator.service.impl;

import com.heaven.zyc.core.operator.dao.OperatorDao;
import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.core.operator.domain.OperatorStatus;
import com.heaven.zyc.core.operator.service.OperatorService;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.generic.finder.PropertiesFinder;
import com.heaven.zyc.pagination.Pagination;
import com.heaven.zyc.web.HttpParameterParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-17
 * Time: 下午11:16
 * To change this template use File | Settings | File Templates.
 */
@Service(value = "operatorService")
public class OperatorServiceImpl implements OperatorService {
    @Resource
    private OperatorDao operatorDao;

    @Override
    public Operator getBy(Integer id) {
        return operatorDao.get(id);
    }

    @Override
    public Operator login(String account, String password) throws BusinessException{
        Operator operator = operatorDao.getByPassword(account,password);
        if (operator == null){
            throw new BusinessException("用户名或密码不正确！");
        }else {
            if (operator.getOperatorStatus().equals(OperatorStatus.FREEZE)){
                throw new BusinessException("account is already freeze！");
            }
        }
        return operator;
    }

    @Override
    public List<Operator> getAllOperator(Pagination pagination) {
        Map<String,Object> params = new HashMap<String, Object>();
        return operatorDao.findNotDeleteOperator(params, pagination);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void save(Operator operator) {
        operatorDao.save(operator);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update(Operator operator) {
        operator.setUpdateTime(new Date());
        operatorDao.update(operator);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(Operator operator) {
        operator.setOperatorStatus(OperatorStatus.DELETE);
        this.update(operator);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void freeze(Operator operator) {
        if (operator.getOperatorStatus().equals(OperatorStatus.FREEZE)){
            operator.setOperatorStatus(OperatorStatus.ACTIVE);
        }else {
            operator.setOperatorStatus(OperatorStatus.FREEZE);
        }
        this.update(operator);
    }

    @Override
    public Operator findByAccount(String account) {
        PropertiesFinder finder = new PropertiesFinder(Operator.class,"account",account);
        return operatorDao.getBy(finder);
    }
}
