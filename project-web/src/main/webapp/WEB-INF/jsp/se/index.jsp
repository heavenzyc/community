<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tags" tagdir="/WEB-INF/tags" %>
 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<c:import url="/WEB-INF/jsp/se/include/head_include.jsp"/>
	<script type="text/javascript" src="/static/operator/operator.js"></script>
	<script type="text/javascript">
	$(function() {
		$("#modifyPassDiv").click(function(){
			modifyPassword();
		});
		/*
		$("#logOutDiv").click(function(){ 
			logOut();
		});*/
		
		var menuItems = $(".nuemItem");
		menuItems.each(function(index,item){
			if(window.location.pathname ==$(item).attr("href")) {
				var centerPanel = $('#easyuiLayout').layout('panel','center');
				centerPanel.panel("setTitle", $(item).html());
				$("#navbar").attr("href", $(item).attr("href"));
				$("#navbar").html($(item).html());
				return;
			}
		});
		 
		
	});
	</script>

    <script type="text/javascript">
        $(function() {
            var accordion =  $('#meunList').accordion({
                fit : false,
                border:false,
                animate:false
            });
            var menuItems = $(".nuemItem");
            menuItems.each(function(index,item){
                if(window.location.pathname ==$(item).attr("href")) {
                    accordion.accordion("select",$(item).attr("groupName"));

                    return;
                }
            });
        });

    </script>
</head>
<body>
    <div class="top">
        <div class="topwapper">
            <div class="header"> <img src="" /><em></em> </div>
            <span class="logo"></span>
            <span class="name">约泡生气</span>
            <div class="right">
                <div class="login">
                    <p>${requestScope.op_principal.name}</p>
                    <p><a href="javascript:void(0);" id="modifyPassDiv">修改密码</a>丨 <a href="javascript:logOut()" id="logOutDiv">退出</a> </p>
                </div>
            </div>
        </div>
    </div>
	
	<div style="text-align: center;">
		<div style="margin: 0 auto; width: 90%;text-align: left;">
			<div id="easyuiLayout" class="easyui-layout" style="height: 750px;">
			 	<!-- 导航条 -->
			    <div data-options="region:'north',split:false" style="height:35px;">
			    	<div class="banner">
		                 <p class="url-path">
		                     <span class="fl-left">
		                         <span>
		                       
		                         <a href="/se/">系统首页</a>
		                     
		                         </span>
		                         <span>&gt;</span>
		                         <a id="navbar" href="food_comment.html"></a>
		                     </span>
		                      
		                 </p>
		             </div>
			    </div>
			    
			    <!--菜单-->
			    <div data-options="region:'west',title:'菜单列表',split:false" style="width:150px;">
		 			<%--<tags:menu/>--%>
                    <div  id="meunList">
                        <c:forEach items="${operator.resourceGroupListMap}" var="groupMap" varStatus="st">

                            <div class="nuemGroup" title="${groupMap.key.name}" >
                                <ul class="menu-left-lists">
                                    <c:forEach items="${groupMap.value}" var="resource" varStatus="st1">
                                        <li><a class="nuemItem" groupName="${groupMap.key.name}" href="${resource.path}">${resource.name}</a></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </c:forEach>
                    </div>
				 </div>  
				
				<!-- 具体内容 -->
			    <div data-options="region:'center',title:' '">
			     	<sitemesh:write property="body"/>
                </div>
			</div>
		</div>
	</div>
	<div>
       <div id="footer-inner">
           <p>HEAVEN.ZYC ™ Copyright 2014 All Rights Reserved</p>
           <p>关于我们  |  条款和隐私权  |  帮助中心  </p>
           <p class="tel"></p>
       </div>
   </div>
</body>
 
</html>
