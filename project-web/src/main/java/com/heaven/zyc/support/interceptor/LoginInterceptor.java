package com.heaven.zyc.support.interceptor;

import com.heaven.zyc.core.operator.domain.Operator;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-21
 * Time: 下午10:10
 * To change this template use File | Settings | File Templates.
 */
public class LoginInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object o) throws Exception {
        if (request.getServletPath().endsWith("/login.htm")){
            return true;
        }
        if (request.getServletPath().endsWith("/login")){
            return true;
        }
        Operator operator = (Operator) request.getSession().getAttribute("operator");
        if (operator != null) {
            return true;
        }else {
            response.sendRedirect("/login.htm");
        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
