package com.heaven.zyc.controller.login;

import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.core.operator.service.OperatorService;
import com.heaven.zyc.core.resource.domain.ResourceGroup;
import com.heaven.zyc.core.resource.domain.SysResource;
import com.heaven.zyc.core.resource.service.ResourceGroupService;
import com.heaven.zyc.core.resource.service.ResourceService;
import com.heaven.zyc.core.role.domain.RoleResource;
import com.heaven.zyc.core.role.domain.RoleStatus;
import com.heaven.zyc.core.role.domain.SysRole;
import com.heaven.zyc.core.role.service.RoleResourceService;
import com.heaven.zyc.exception.BusinessException;
import com.heaven.zyc.support.cookie.CookieElement;
import com.heaven.zyc.support.cookie.MarkCookie;
import com.heaven.zyc.support.cookie.PersistCookie;
import com.heaven.zyc.support.spring.interceptor.ObjectConvertAnno;
import com.heaven.zyc.support.spring.view.JsonView;
import com.heaven.zyc.support.utils.EncryptDecryptData;
import com.heaven.zyc.web.HttpParameterParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-20
 * Time: 下午10:41
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class LoginController {

    private final String SESSION_OPERATOR = "operator";

    @Resource
    private OperatorService operatorService;
    @Resource
    private ResourceGroupService resourceGroupService;
    @Resource
    private ResourceService resourceService;

    @RequestMapping(value = "/")
    public String toLoginPage(){
        return "/login";
    }

    @RequestMapping(value = "/se/login.htm",method = RequestMethod.POST)
    public ModelAndView login(Model model,HttpServletRequest request,
                        HttpServletResponse response,
                        @RequestParam String account,
                        @RequestParam String password){
        Operator operator = null;
        try {
            operator = operatorService.login(account,password);
        } catch (BusinessException e) {
            model.addAttribute("errorMsg",e.getMessage());
            return new ModelAndView(new JsonView(false,e.getMessage()));
        }
        createMenu(operator);
        model.addAttribute("operator",operator);
        request.getSession().setAttribute(SESSION_OPERATOR,operator);
        saveOperatorCookie(response,request,operator);
        return new ModelAndView(new JsonView(true,"/index"));
    }

    @RequestMapping(value = "/se/auto.htm",method = RequestMethod.GET)
    public String autoLogin(Model model,HttpServletRequest request,HttpServletResponse response){
        Integer id = getOperatorId(request,response);
        Operator operator = null;
        if (id != null){
            operator = operatorService.getBy(id);
        }
        createMenu(operator);
        model.addAttribute(SESSION_OPERATOR, operator);
        request.getSession().setAttribute(SESSION_OPERATOR, operator);
        return "redirect:/";
    }

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        return "/se/index";
    }

    @RequestMapping(value = "/se/logout",method = RequestMethod.GET)
    public ModelAndView logOut(HttpServletRequest request,HttpServletResponse response){
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_OPERATOR) != null){
            session.removeAttribute(SESSION_OPERATOR);
        }
        MarkCookie markCookie = new PersistCookie(response,request);
        markCookie.removeCookie(CookieElement.OPERATOR_ID);
        return new ModelAndView(new JsonView("success",true));
    }

    private void saveOperatorCookie(HttpServletResponse response,HttpServletRequest request,Operator operator){
        String operatorId = EncryptDecryptData.encrypt(operator.getId()+"",EncryptDecryptData.KEY);
        MarkCookie persistCookie = new PersistCookie(response,request);
        CookieElement cookieElement = new CookieElement(CookieElement.OPERATOR_ID,operatorId,7);
        persistCookie.writeCookie(cookieElement);
    }

    private Integer getOperatorId(HttpServletRequest request,HttpServletResponse response){
        MarkCookie persistCookie = new PersistCookie(response,request);
        Cookie cookie = persistCookie.getCookie(CookieElement.OPERATOR_ID);
        if (cookie != null){
            String cookieValue = cookie.getValue();
            String operatorIdStr = EncryptDecryptData.decrypt(cookieValue,EncryptDecryptData.KEY);
            if (!StringUtils.isBlank(operatorIdStr)){
                return Integer.parseInt(operatorIdStr);
            }
        }
        return null;
    }

    private void createMenu(Operator operator){
        SysRole role = operator.getRole();
        Map<ResourceGroup,List<SysResource>> resourceGroupListMap = operator.getResourceGroupListMap();
        if (role.isSuperOperator()){ //超级管理员权限
            List<ResourceGroup> groups = resourceGroupService.getAll();
            for (ResourceGroup group : groups){
                List<SysResource> resources = resourceService.getByResourceGroup(group);
                resourceGroupListMap.put(group,resources);
            }
        }else { //普通管理员权限
            //用户能操作的所有资源
        //    List<RoleResource> roleResources = roleResourceService.getListByRole(role);
            if (role.getRoleStatus().equals(RoleStatus.FREEZE) || role.getRoleStatus().equals(RoleStatus.DELETE)) return;
            Set<RoleResource> roleResourceSet = role.getRoleResources();

            for (RoleResource roleResource : roleResourceSet){
                SysResource resource = roleResource.getResource();
                ResourceGroup group = resource.getResourceGroup();
                if (!resourceGroupListMap.containsKey(group)){
                    List<SysResource> resources = new ArrayList<SysResource>();
                    resources.add(resource);
                    resourceGroupListMap.put(group,resources);
                }else {
                    Set<Map.Entry<ResourceGroup,List<SysResource>>> entrySet = resourceGroupListMap.entrySet();
                    Iterator<Map.Entry<ResourceGroup,List<SysResource>>> entryIterator = entrySet.iterator();
                    while (entryIterator.hasNext()){
                        Map.Entry<ResourceGroup, List<SysResource>> entry = entryIterator.next();
                        if (entry.getKey().equals(group)){
                            entry.getValue().add(resource);
                        }
                    }
                }
            }
        }

    }

}
