package com.heaven.zyc.controller.operator;

import com.heaven.zyc.core.operator.domain.Gender;
import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.core.operator.domain.OperatorStatus;
import com.heaven.zyc.core.operator.service.OperatorService;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.service.RoleService;
import com.heaven.zyc.pagination.Pagination;
import com.heaven.zyc.support.spring.view.JsonView;
import com.heaven.zyc.support.utils.HttpParameterParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-27
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/se")
public class OperatorController {
    private final String SESSION_OPERATOR = "operator";
    @Resource
    private OperatorService operatorService;
    @Resource
    private RoleService roleService;

    @RequestMapping(value = "/operator")
    public String toOperatorPage(){
        return "/se/content/operator";
    }

    @RequestMapping(value = "/operator.htm")
    public ModelAndView operatorList(Pagination pagination){
        List<Operator> operatorList = operatorService.getAllOperator(pagination);
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("operators",operatorList);
        model.put("pagination",pagination);
        return new ModelAndView(new JsonView("operatorList",model));
    }

    @RequestMapping(value = "/data/json/roles.htm")
    public ModelAndView roleList(){
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("superOperator",false);
        List<SysRole> roles = roleService.getByParams(params);
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("data",roles);
        return new ModelAndView(new JsonView("roleList",model));
    }

    @RequestMapping(value = "/operator/check_account/{account}")
    public ModelAndView checkAddAccountUnique(@PathVariable String account){
        Operator operator = operatorService.findByAccount(account);
        if (operator == null) return new ModelAndView(new JsonView(false));
        return new ModelAndView(new JsonView(true));
    }

    @RequestMapping(value = "/operator/add",method = RequestMethod.PUT)
    public ModelAndView saveOperator(HttpServletRequest request,Operator operator){
        HttpParameterParser parser = new HttpParameterParser(request);
        String name = parser.getString("name");
        String account = parser.getString("account");
        String password = parser.getString("password");
        String mobile = parser.getString("mobile");
        String email = parser.getString("email");
        Integer roleId = parser.getInteger("role");
        String genderStr = parser.getString("gender");
        String description = parser.getString("description");
    //    Operator operator = new Operator();
        Date date = new Date();
        operator.setName(name);
        operator.setOperatorStatus(OperatorStatus.ACTIVE);
        operator.setUpdateTime(date);
        operator.setAccount(account);
        operator.setCreateTime(date);
        operator.setDescription(description);
        operator.setEmail(email);
        operator.setMobile(mobile);
        operator.setPassword(password);
        SysRole role = roleService.getById(roleId);
        operator.setRole(role);
        operator.setGender(Gender.valueOf(genderStr));
        operatorService.save(operator);
        return new ModelAndView(new JsonView(true,"success!"));
    }

    @RequestMapping(value = "/operator/{id}/")
    public ModelAndView getOperator(@PathVariable Integer id){
        Operator operator = operatorService.getBy(id);
        return new ModelAndView(new JsonView("operator",operator));
    }

    @RequestMapping(value = "/operator/edit/{id}/",method = RequestMethod.PUT)
    public ModelAndView updateOperator(@PathVariable Integer id, HttpServletRequest request){
        HttpParameterParser parser = new HttpParameterParser(request);
        String name = parser.getString("name");
        String mobile = parser.getString("mobile");
        String email = parser.getString("email");
        Integer roleId = parser.getInteger("roles");
        String genderStr = parser.getString("gender");
        String description = parser.getString("description");
        Operator operator = operatorService.getBy(id);
        operator.setName(name);
        operator.setOperatorStatus(OperatorStatus.ACTIVE);
        operator.setDescription(description);
        operator.setEmail(email);
        operator.setMobile(mobile);
        SysRole role = roleService.getById(roleId);
        operator.setRole(role);
        operator.setGender(Gender.valueOf(genderStr));
        operatorService.update(operator);
        return new ModelAndView(new JsonView(true,"success"));
    }

    @RequestMapping(value = "/operator/current")
    public ModelAndView currentOperator(HttpServletRequest request){
        Operator operator = (Operator) request.getSession().getAttribute(SESSION_OPERATOR);
        return new ModelAndView(new JsonView("operator",operator));
    }

    @RequestMapping(value = "/operator/freeze/{id}")
    public ModelAndView freezeOperator(@PathVariable Integer id){
        Operator operator = operatorService.getBy(id);
        operatorService.freeze(operator);
        return new ModelAndView(new JsonView(true));
    }

    @RequestMapping(value = "/operator/delete/{id}")
    public ModelAndView deleteOperator(@PathVariable Integer id){
        Operator operator = operatorService.getBy(id);
        operatorService.delete(operator);
        return new ModelAndView(new JsonView(true));
    }
}
