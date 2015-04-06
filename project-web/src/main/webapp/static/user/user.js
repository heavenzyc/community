/**
 * 查询所有角色
 */
function loadUser(){
	$('#userTable').datagrid({
		nowrap: true,
		striped: true,
		fitColumns:true,
		toolbar:"#toolbar",
		url:'/user',
		method:'get',
		pagination:true,
		singleSelect:true,
		idField:'id',
		frozenColumns:[[
	        {field:'id',checkbox:true}
       ]],
		columns:[[ 
			{field:'name',title:'名称',width:$(this).width() * 0.1},
			{field:'age',title:'年龄',width:$(this).width() * 0.4}
		]],
        loadFilter:function(json) {
            var data = {};
            data.rows = json.users;

            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        }
        ,
        onAfterSelectPage:function() {
            var pageOpt = $('#userTable').datagrid('getPager').pagination("options");

            var name = $("#opName").attr("value");
            var account = $("#opAccount").attr("value");
            $(this).datagrid("options").queryParams={
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize,
                "name":name,
                "account":account
            };
        }
	});		
}