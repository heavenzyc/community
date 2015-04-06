package com.heaven.zyc.support.cookie;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: heavenzyc
 * Date: 14-4-23
 * Time: 下午10:34
 * To change this template use File | Settings | File Templates.
 */
public abstract class MarkCookie {

    public abstract void writeCookie(CookieElement cookieElement);

    public abstract Cookie getCookie(String name);

    public abstract void removeCookie(String name);
}
