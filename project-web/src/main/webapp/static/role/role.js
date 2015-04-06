/**
 * 查询所有角色
 */
function loadAllRole(){
	$('#roleTable').datagrid({
		nowrap: true,
		striped: true,
		fitColumns:true,
		toolbar:"#toolbar",
		url:'/se/role.htm',
		method:'get',
		pagination:true,
		singleSelect:true,
		idField:'id',
        rownumbers:true,
		/*frozenColumns:[[
	        {field:'id',checkbox:true}
       ]],*/
		columns:[[ 
			{field:'name',title:'角色名称',width:$(this).width() * 0.1},
			{field:'superOperator',hidden:true},
			{field:'roleResources',title:'权限列表',width:$(this).width() * 0.4,formatter:function(roleResources){
				var value='';
				if(!roleResources) return value;
				 
				for(var i = 0;i<roleResources.length;i++) {
					value+=roleResources[i].resource.name;
					if(i<roleResources.length-1)
						value+=',';	
				}
				return value;
			}},
			{field:'roleStatus',title:'状态',width:$(this).width() * 0.1,formatter:function(value){
                return value.desc;
            }},
			{field:'description',title:'角色描述',width:$(this).width() * 0.4},
            {field:'id',title:'操作',align:'center',width:$(this).width() * 0.2,formatter:function(value,row,index){
                var html = "";
                html += "<a class='link-update' href='javascript:editRole("+row.id+")'>编辑</a>";
                if(row.roleStatus._name == 'FREEZE'){
                    html += "<a class='link-update' href='javascript:cancelFreezeRole()'>激活</a>";
                }else{
                    html += "<a class='link-update' href='javascript:freezeRole()'>冻结</a>";
                }
                html += "<a class='link-update' href='javascript:deleteRole()'>删除</a>";

                return html;
            }}
		]],
        loadFilter:function(json) {
            var data = {};
            data.rows = json.data;

            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        }
        ,
        onAfterSelectPage:function() {
            var pageOpt = $('#roleTable').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
	});		
}

var nameExist = false;
function checkNameUnique(){
    var name = $("#add_name").val();
    $.ajax({
        url:'/se/role/check_name',
        type:'get',
        data:{name:name},
        async:'false',
        success:function(data){
            if(data){
                $("#uniqueTips").html('').html('name is exist!');
                nameExist = false;
            }else{
                $("#uniqueTips").html('');
                nameExist = true;
            }
        }
    });
}


/**
 * 添加角色
 */
function addRole() {
	var dialogDom = $("<div id='addRoleDialog'></div>");
	dialogDom.load("/local/dialog/role.html #addRoleDiv",function(){
		var form = $(this).find("form");
		$.parser.parse(form);
		$('#resources').combotree({  
		    url: '/se/data/json/resources.htm',
		    method:'get',
		    required: true,
		    idField : 'id',
		    fitColumns : true,
		    multiple:true,
		    checkbox:true,
		    editable:false,
		    loadFilter:function(json) {
				 for(var i = 0;i<json.length;i++){
					 json[i].text = json[i].name;
					 json[i].attributes={};
					 json[i].attributes.isGroup = true;
					 json[i].children=parserChildren(json[i].resources);
				 }
				 return json;
			}
			 
		});
		function parserChildren(resources) {
			if (!resources || resources.length == 0)
				return [];
			for(var i = 0;i<resources.length;i++){
				resources[i].text = resources[i].name;
				resources[i].attributes={};
				resources[i].attributes.isGroup = false;
			}
			return resources;
		}
		var cancelBtn = $(this).find("#cancelBtn");
		cancelBtn.click(function(){
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#saveBtn");
		saveBtn.click(function(){
			form.form("submit",{
				url:"/se/role/create.htm",
				onSubmit:function() {
					var ok = $(this).form('validate');
                    ok = ok && nameExist
					if(!ok) return false;
					var values = $('#resources').combotree('getValues');
					var tree = $('#resources').combotree('tree');
					var checkedNodes = tree.tree('getChecked');
					var checkedValues = [];
					for(var i = 0,j = 0;i<checkedNodes.length;i++) {
						if(!checkedNodes[i].attributes.isGroup) {
							checkedValues[j++] = checkedNodes[i].id;
						}
					}
				//	$('#resources').combotree('setValues',checkedValues);
			    },
			    success:function(text){  
			    	var data = parserToJson(text);
			    	if(data) {
			    		dialogDom.dialog("destroy");
			    		$('#roleTable').datagrid("reload");
			    	}
			    	else {
			    		$("#errorMsg").html(data.msg);
			    	}
			    }
			});
		});
	});	
	dialogDom.dialog({
		title: '添加角色',
		width: 350,
		modal: true,
		shadow: false,
		closed: false,
		draggable : false,
		height: 250,
		onClose:function() {
			$(this).dialog("destroy");
		}
	});
}


var editNameExist = true;
function checkEditNameUnique(){
    var row = $('#roleTable').datagrid('getSelected');
    var name = $("#name").val();
    $.ajax({
        url:'/se/role/check_edit_name/'+row.id+'/'+name,
        type:'get',
        async:'false',
        success:function(data){
            if(data){
                $("#uniqueEditTips").html('').html('name is exist!');
                editNameExist = false;
            }else{
                $("#uniqueEditTips").html('');
                editNameExist = true;
            }
        }
    });
}


/**
 * 编辑角色
 */
function editRole(id) {
 
	var row = $('#roleTable').datagrid('getSelected');
	if (!row){
		$.messager.alert('警告','请选择一个角色');
		return;
	}
	if(row.superOperator) {
		$.messager.alert('警告','该管角色是系统角色不能修改');
		return;
	}
	var dialogDom = $("<div id='editRoleDialog'></div>");
	var loadUrl = "/local/dialog/role.html #editRoleDiv";
	dialogDom.load(loadUrl,function(){
		var form = $(this).find("form");
		$.parser.parse(form);
		var operator=getRoleData(id);
		fillEditRoleData(operator);
		var cancelBtn = $(this).find("#edit_cancelBtn");
		cancelBtn.click(function(){
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#edit_saveBtn");
		saveBtn.click(function(){
			form.form("submit",{
				url:"/se/role/edit/"+id+"/",
				onSubmit:function() {
					var ok = $(this).form('validate');
                    ok = ok && editNameExist;
					if(!ok) return false;
					var values = $('#edit_resources').combotree('getValues');
					var tree = $('#edit_resources').combotree('tree');
					var checkedNodes = tree.tree('getChecked');
					var checkedValues = [];
					for(var i = 0,j = 0;i<checkedNodes.length;i++) {
						/*if(!checkedNodes[i].attributes.isGroup) {
							checkedValues[j++] = checkedNodes[i].id;
						}*/
                        checkedValues[j++] = checkedNodes[i].id;
					}
					$('#edit_resources').combotree('setValues',checkedValues);
			    },
			    success:function(text){  
			    	var data = parserToJson(text);
			    	if(data.success) {
			    		dialogDom.dialog("destroy");
			    		$('#roleTable').datagrid("reload");
			    	}
			    	else {
			    		$("#errorMsg").html(data.msg);
			    	}
			    }
			});
		});
		 
	});	
	dialogDom.dialog({
		title: '修改角色',
		width: 400,
		modal: true,
		shadow: false,
		closed: false,
		draggable : false,
		height: 300,
		onClose:function() {
			$(this).dialog("destroy");
		}
	});
	
	function getRoleData(roleId) {
		var url = "/se/role/"+roleId+"/";
		var data = $.ajax({
			  url: url,
			  async: false
			 }).responseText;
		return parserToJson(data);
	}
	 
	function fillEditRoleData(role) {
		$("#name").val(role.name);
		$("#description").val(role.description);
		$("#edit_resources").combotree({
			url:'/se/data/json/resources.htm',
			method:'get',
		    valueField:'id',  
		    required:true,
		    multiple:true,
		    editable:false,
		    loadFilter:function(resources) {
		    	var roleResources = role.roleResources;
		    	if(!roleResources||roleResources.length==0)
		    		return resources;
				for(var i =0;i<resources.length;i++) {
                    resources[i].text = resources[i].name;
					for(var j = 0;j<roleResources.length;j++) {
						if(resources[i].id == roleResources[j].resource.id) {
							resources[i].checked=true;
							break;
						}
					}
				}
			  return resources;
			}
		});
	}
}

 


/**
 * 删除角色
 */
function deleteRole() {
 
	var row = $('#roleTable').datagrid('getSelected');
	if (!row){
		$.messager.alert('警告','请选择一个角色');
		return;
	}
	if(row.superOperator) {
		$.messager.alert('警告','系统角色不能删除');
		return;
	}
	$.messager.confirm('删除角色','你确定删除要角色 '+row.name+' ?',function(ok){
	    if(ok) {
	    	var url = "/se/role/delete/"+row.id+"/";
	    	$.ajax({
	    		url: url,
	  		  	async: false,
	  		  	type:'get',
	  		  	dataType:'json',
	  		  	success:function(data) {
	  		  		if(data.success) {
	  		  			$('#roleTable').datagrid("reload");
	  		  		}
	  		  		else {
	  		  			$.messager.alert('警告',data.msg);
	  		  		}
	  		  	}
	    	});
	    }
	});
}


function freezeRole() {

    var row = $('#roleTable').datagrid('getSelected');
    if(row.superOperator) {
        $.messager.alert('警告','系统角色不能冻结');
        return;
    }
    $.messager.confirm('冻结角色','你确定要冻结角色 '+row.name+' ?',function(ok){
        if(ok) {
            freezeOperate(row);
        }
    });
}

function cancelFreezeRole() {

    var row = $('#roleTable').datagrid('getSelected');
    freezeOperate(row);
}

function freezeOperate(row){
    var url = "/se/role/freeze/"+row.id+"/";
    $.ajax({
        url: url,
        async: false,
        type:'get',
        dataType:'json',
        success:function(data) {
            if(data.success) {
                $('#roleTable').datagrid("reload");
            }
            else {
                $.messager.alert('警告',data.msg);
            }
        }
    });
}