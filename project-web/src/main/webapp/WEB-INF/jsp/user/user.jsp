<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<script type="text/javascript" src="/static/user/user.js"></script>
<script type="text/javascript">
$(function() {
	loadUser();
});
</script>
<div class="contenter">
	 <table id="userTable" class="datagrid"></table>
	 <div id="toolbar" style="padding:5px;height:auto">
		<div style="margin-bottom:5px">
			<a href="#" onclick="addRole()"  class="easyui-linkbutton" iconCls="icon-add">添加角色</a>
			<a href="#" onclick="editRole()"  class="easyui-linkbutton" iconCls="icon-edit">修改角色</a>
			<a href="#" onclick="deleteRole()" class="easyui-linkbutton" iconCls="icon-no">删除角色</a>
		</div>
	</div>
</div>
