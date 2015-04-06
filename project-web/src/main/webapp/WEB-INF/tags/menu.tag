<%@tag import="com.naryou.security.business.operator.domain.Operator"%>
<%@tag import="com.naryou.security.business.operator.service.impl.OperatorIdentityValidator"%>
<%@ tag import="com.heaven.zyc.core.operator.domain.Operator" %>
<%@ tag pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 

<%--
<%
Operator operator = (Operator)request.getAttribute(OperatorIdentityValidator.PRINCIPALCOOKIE_NAME);
jspContext.setAttribute("operator", operator);
%>
--%>

 
<div  id="meunList">
	<c:forEach items="${operator.resourceGroups}" var="groupMap" varStatus="st">
		
		<div class="nuemGroup" title="${groupMap.key.name}" >
			 <ul class="menu-left-lists">
            	<c:forEach items="${groupMap.value}" var="resource" varStatus="st1">
				<li><a class="nuemItem" groupName="${groupMap.key.name}" href="${resource.entryIndex}">${resource.name}</a></li>
			    </c:forEach>
       	</ul>
		</div>
	</c:forEach>
</div>
 
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
 