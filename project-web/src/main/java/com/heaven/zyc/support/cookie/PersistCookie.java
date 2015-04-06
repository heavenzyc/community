package com.heaven.zyc.support.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-23
 * Time: 下午10:35
 * To change this template use File | Settings | File Templates.
 */
public class PersistCookie extends MarkCookie{
    private HttpServletResponse response;
    private HttpServletRequest request;

    public PersistCookie(HttpServletResponse response, HttpServletRequest request) {
        this.response = response;
        this.request = request;
    }

    @Override
    public void writeCookie(CookieElement cookieElement) {
        Cookie cookie = new Cookie(cookieElement.getName(),cookieElement.getValue());
        cookie.setPath("/");
        cookie.setMaxAge(60*60*24*cookieElement.getDay());
        response.addCookie(cookie);
    }

    @Override
    public Cookie getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals(name)){
                    return cookie;
                }
            }
        }
        return null;
    }

    @Override
    public void removeCookie(String name) {
        Cookie cookie = getCookie(name);
        if (cookie != null){
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
