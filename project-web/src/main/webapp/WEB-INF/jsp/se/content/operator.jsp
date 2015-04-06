<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<script type="text/javascript" src="/static/user/user.js"></script>
<script type="text/javascript" src="/static/operator/operator.js"></script>
<script type="text/javascript">
$(function() {
    searchOperator();
});
</script>
<div class="contenter">
	 <table id="operatorTable" class="datagrid"></table>
	 <div id="toolbar" style="padding:5px;height:auto">
		<div style="margin-bottom:5px">
			<a href="#" onclick="addOperator()"  class="easyui-linkbutton" iconCls="icon-add">添加管理员</a>
			<%--<a href="#" onclick="editOperator()"  class="easyui-linkbutton" iconCls="icon-edit">修改管理员</a>--%>
			<%--<a href="#" onclick="deleteOperator()" class="easyui-linkbutton" iconCls="icon-no">删除管理员</a>--%>
		</div>
	</div>
</div>
