package com.heaven.zyc.support.filter;

import com.heaven.zyc.core.operator.domain.Operator;
import com.heaven.zyc.core.operator.service.OperatorService;
import com.heaven.zyc.support.cookie.CookieElement;
import com.heaven.zyc.support.cookie.MarkCookie;
import com.heaven.zyc.support.cookie.PersistCookie;
import com.heaven.zyc.support.utils.EncryptDecryptData;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: heaven.zyc
 * Date: 14-4-22
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
public class OperatorLoginFilter implements Filter {
    private FilterConfig config;
    private WebApplicationContext context;
    private OperatorService operatorService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.config = filterConfig;
        this.context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        operatorService = (OperatorService) context.getBean("operatorService");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUrl = request.getRequestURI();
        String loginStrings = config.getInitParameter("loginStr");        // 登录登陆页面
        String includes = config.getInitParameter("includeStr");
        String[] loginStr = loginStrings.split(",");
        String[] includeStr = includes.split(",");
        if (requestUrl.endsWith("/logout")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (isContains(requestUrl,loginStr) || requestUrl.equals("/")){ //filter login,login.htm
            if (null == request.getSession().getAttribute("operator")){//session is not exist,redirect to login page
                if (getOperatorId(request,response) != null){
                    response.sendRedirect("/se/auto.htm");
                    return;
                }
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }else {
                //session is exist,redirect to index page
                response.sendRedirect("/index");
                return;
            }
        }
        if (!isContains(requestUrl,includeStr)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (null == request.getSession().getAttribute("operator") && !requestUrl.endsWith("/auto.htm")){
            response.sendRedirect("/se/login.htm");
            return;
        }else {
            filterChain.doFilter(servletRequest,servletResponse);
        }

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

    public static boolean isContains(String container, String[] regx) {
        boolean result = false;

        for (int i = 0; i < regx.length; i++) {
            if (container.indexOf(regx[i]) != -1) {
                return true;
            }
        }
        return result;
    }

    @Override
    public void destroy() {
        config = null;
    }


}
