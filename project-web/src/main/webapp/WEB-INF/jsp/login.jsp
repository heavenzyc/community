<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>后台登录</title>
		<c:import url="/WEB-INF/jsp/se/include/head_include.jsp"/>
		<tags:static value="com.heaven.zyc.config.LocalConfigUtils@STATIC_RESOURCE_DOMAIN" var="var"/>
		<link type="text/css" rel="stylesheet" href="/static/ui/css/login.css" />
		<script type="text/javascript">
		 
            function validForm() {
                  $("#loginForm").form("submit",{
                        url:"/se/login.htm",
                        method:'post',
                        onSubmit:function() {
                            return  $("#loginForm").form('validate');
                        },
                        success:function(txt){
                            var data = jQuery.parseJSON(txt);
                            if(data.success) {
                                window.location.href=data.msg;
                            }
                            else {
                                $("#loginForm").find("input[type='password']").attr("value","");
                                $("#errorMsg").html(data.msg);
                            }
                        }
                    });
                }
            function check(){
                var a_null = $("#account").validatebox("isValid");
                var p_null = $("#password").validatebox("isValid");
                if(a_null || p_null) return false;
            }
        </script>
	</head>
    <body class="login-page">
    <div class="logindiv"> <a href="#" class="logo"></a>
        <div class="myform">
            <form id="loginForm">
                <label class="username">
                    <input id="account" type="text" name="account" value="${param.account}" class="easyui-validatebox" data-options="required:true,validType:'length[5,10]'" delay="500"/>
                </label>
                <label class="password">
                    <input id="password" type="password" name="password" class=" easyui-validatebox" data-options="required:true,validType:'length[5,10]'" delay="500"/>
                </label>
                <p id="errorMsg" class="errorMsg">${errorMsg}</p>
                <button type="button" class="login-btn" onclick="validForm()"></button>
            </form>
        </div>
        <div class="foot">FOCION NETWORK ™ Copyright 2014 All Rights Reserved</div>
    </div>
    </body>
</html>
