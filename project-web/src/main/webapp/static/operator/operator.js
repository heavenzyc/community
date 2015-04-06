
/**
 * 退出登录
 */
function logOut(){
    $.messager.confirm("提示","确定退出登录吗？",function(ok){
       if(ok){
           $.ajax({
               url:'/se/logout',
               type:'get',
               success:function(data){
                   if(data){
                       window.location.href = "/";
                   }
               }
           });
       }
    });
}

function modifyPassword() {
	var dialogDom = $("<div id='modifyPasswordDivDialog'></div>");
	var loadUrl = "/local/dialog/operator.html #modifyPasswordDiv";
	dialogDom.load(loadUrl, function() {
		var form = $(this).find("form");
		$.parser.parse(form);
		var operator = currentOperator();
		$("#account").html(operator.account);
		$("#name").val(operator.name);
		var cancelBtn = $(this).find("#cancelBtn");
		cancelBtn.click(function() {
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#saveBtn");
		saveBtn.click(function() {
			form.attr("action", "/se/modifyPassword.htm");
			form.form("submit", {
				success : function(text) {
					var data = parserToJson(text);
					if (data.success) {
						dialogDom.dialog("destroy");
					} else {
						$("#errorMsg").html(data.msg);
					}
				}
			});
		});
	});
	dialogDom.dialog({
		title : '修改密码',
		width : 350,
		modal : true,
		shadow : false,
		closed : false,
		height : 350,
		onClose : function() {
			$(this).dialog("destroy");
		}
	});
}

function searchOperator(){
	$('#operatorTable').datagrid({
		nowrap: true,
		striped: true,
		fitColumns:true,
		toolbar:"#toolbar",
		url:'/se/operator.htm?'+new Date().getTime(),
		method:'get',
		pageSize:10,
		pageNumber:1,
		pagination:true,
		pageList:[10,20],
		singleSelect:false,
		idField:'id',
        rownumbers:true,
		/*frozenColumns:[[
	        {field:'id',checkbox:true}
       ]],*/
		columns:[[ 
			{field:'name',title:'姓名',width:$(this).width() * 0.1},
			{field:'account',title:'帐号',width:$(this).width() * 0.1},

			{field:'gender',title:'性别',width:$(this).width() * 0.05,formatter:function(gender){
                if(gender != null){
                    return gender.desc;
                }
            }},
			{field:'operatorStatus',title:'状态',width:$(this).width() * 0.05,formatter:function(operatorStatus){
				return operatorStatus.desc;
			}},
            {field:'email',title:'邮箱',width:$(this).width() * 0.1},
            {field:'mobile',title:'电话',width:$(this).width() * 0.1},
			{field:'role',title:'管理角色',width:$(this).width() * 0.1,formatter:function(role){
				return role.name;
			}},
			{field:'createTime',title:'创建日期',width:$(this).width() * 0.1,formatter:function(date){
				 
				return formatDate(date,'yyyy-MM-dd hh:mm');
			}},
            {field:'description',title:'描述',width:$(this).width() * 0.4},
            {field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
                var html = '';
             //   html += '<a href="###" class="link-update" onclick="createDialog(' + row.id + ',\'' + row.line.id + '\',\'' + row.peopleNumber + '\')">分配</a>';
                html += '<a href="###" class="link-update" onclick="editOperator('+row.id+')">编辑</a>';
                if(!row.role.superOperator){
                    if(row.operatorStatus._name=='FREEZE'){
                        html += '<a href="###" class="link-update" onclick="unFreezeOperator('+row.id+')">激活</a>';
                    }else{
                        html += '<a href="###" class="link-update" onclick="freezeOperator('+row.id+')">冻结</a>';
                    }
                    html += '<a href="###" class="link-update" onclick="deleteOperate('+row.id+')">删除</a>';
                }
                return html;
            }, align: 'center'}
            /*,
			{field:'lastLoginDate',title:'最后登录日期',width:$(this).width() * 0.1,formatter:function(date){
				return date?formatDate(date,'yyyy-MM-dd hh:mm'):"";
			}}	*/
		]],
		loadFilter:function(json) {
			var data = {};
			data.rows = json.operators;
			 
			data.total = json.pagination.count;
			data.pagination = json.pagination;
			return data;
		},
		onAfterSelectPage:function() {
			var pageOpt = $('#operatorTable').datagrid('getPager').pagination("options");
		 
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

var nameExist = false;
function checkAccountUnique(){
    var name = $("#add_account").val();
    $.ajax({
        url:'/se/operator/check_account/'+name,
        type:'get',
        async:'false',
        success:function(data){
            if(data){
                $("#accountUnique").html('').html('account is exist!');
                nameExist = false;
            }else{
                $("#accountUnique").html('');
                nameExist = true;
            }
        }
    });
}

/**
 * 添加管理员
 */
function addOperator() {
	 
	var dialogDom = $("<div id='addOperatorDiv'></div>");
	dialogDom.load("/local/dialog/operator.html #addOperatorDialog",function(){
		var form = $(this).find("form");
		$.parser.parse(form);
		
		$("#roles").combobox({
			url:'/se/data/json/roles.htm',
			method:'get',
		    valueField:'id',  
		    textField:'name',
		    multiple:false,
		    editable:false,
            loadFilter:function(roles) {
                roles.data[0].selected= true;
                return roles.data;
            }
		    
		});
		var cancelBtn = $(this).find("#cancelBtn");
		cancelBtn.click(function(){
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#saveBtn");
		saveBtn.click(function(){
			form.form("submit",{
                url:'/se/operator/add',
                onSubmit:function() {
                    var ok = $(this).form('validate');
                    ok = ok && nameExist
                    if(!ok) return false;
                },
			    success:function(text){  
			    	var data = parserToJson(text);
			    	if(data.success) {
			    		dialogDom.dialog("destroy");
			    		$('#operatorTable').datagrid("reload");
			    	}
			    	else {
			    		$("#errorMsg").html(data.msg);
			    	}
			    }
			});
		});
	});	
	dialogDom.dialog({
		title: '添加管理员',
		width: 450,
		modal: true,
		shadow: false,
		closed: false,
		draggable : false,
		height: 550,
		onClose:function() {
			$(this).dialog("destroy");
		}
	});
}

/**
 * 编辑管理员
 */
function editOperator(id) {
	/*var row = $('#operatorTable').datagrid('getSelected');
	if (!row){
		$.messager.alert('警告','请选择一个账户');
		return;
	}*/
 
	var dialogDom = $("<div id='editOperatorDialog'></div>");
	var loadUrl = "/local/dialog/operator.html #editOperatorDiv";
	dialogDom.load(loadUrl,function(){
		var form = $(this).find("form");
		$.parser.parse(form);
		var operator=getOperatorData(id);
		fillEditOperatorData(operator);
		var cancelBtn = $(this).find("#edit_cancelBtn");
		cancelBtn.click(function(){
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#edit_saveBtn");
		saveBtn.click(function(){
			form.form("submit",{
				url:"/se/operator/edit/"+id+"/",
                onSubmit:function() {
                    var ok = $(this).form('validate');
                    if(!ok) return false;
                },
			    success:function(data){
			    	if(data) {
			    		dialogDom.dialog("destroy");
			    		$('#operatorTable').datagrid("reload");
			    	}
			    	else {
			    		$("#errorMsg").html("保存失败！");
			    	}
			    }
			});
		});
	});	
	dialogDom.dialog({
		title: '修改账户',
		width: 350,
		modal: true,
		shadow: false,
		closed: false,
		draggable : false,
		height: 450,
		onClose:function() {
			$(this).dialog("destroy");
		}
	});
	
	function fillEditOperatorData(operator) {
		$("#edit_name").val(operator.name);
        $("#edit_mobile").val(operator.mobile);
        $("#edit_email").val(operator.email);
        $("#edit_description").val(operator.description);
        $("#edit_id").val(operator.id);
        if(operator.gender._name == 'MAN'){
            $("#man").attr("checked", "checked");
        }else{
            $("#woman").attr("checked", "checked");
        }
		$("#edit_roles").combobox({
			url:'/se/data/json/roles.htm',
			method:'get',
		    valueField:'id',  
		    textField:'name',
		    multiple:false,
		    editable:false,
		    loadFilter:function(roles) {
				for(var i =0;i<roles.data.length;i++) {
                    if(roles.data[i].id == operator.role.id){
                        roles.data[i].selected = true;
                        break;
                    }
				}
			  return roles.data;
			}
		});
	}
	
	/**
	 * 得到操作员的数据
	 * @param operatorId
	 */
	function getOperatorData(operatorId) {
		var url = "/se/operator/"+operatorId+"/";
		var data = $.ajax({
			  url: url,
			  async: false
			 }).responseText;
		 var operator = parserToJson(data);
		 
		return operator;
	}
}

/**
 * 冻结管理员
 */
function freezeOperator(id) {
	if(isSelf(id)) {
		$.messager.alert('警告','你不能冻结自己');
		return;
	}
	$.messager.confirm('冻结管理员','确定要冻结该账户?',function(ok){
	    if(ok) {
            freezeOperate(id);
	    }
	});

}

function unFreezeOperator(id) {
    freezeOperate(id);
}

function deleteOperate(id){
    if(isSelf(id)) {
        $.messager.alert('警告','你不能删除自己');
        return;
    }
    $.messager.confirm('删除管理员','确定要删除该账户?',function(ok){
        if(ok) {
            var url = "/se/operator/delete/"+id;
            $.ajax({
                url: url,
                async: false,
                type:'post',
                data:{'_method':'put'},
                dataType:'json',
                success:function(data) {
                    if(data) {
                        $('#operatorTable').datagrid("reload");
                    }
                    else {
                        $.messager.alert('警告',data.msg);
                    }

                }
            });
        }
    });

}


/**
 * 判断是否是自己
 */
function isSelf(id) {
    var operator = currentOperator();
    if(id == operator.id) {
        return true;
    }
    return false;
}

function currentOperator() {
    var url = "/se/operator/current?" + new Date().getTime();
    var data = $.ajax({
        url : url,
        async : false,
        type : 'get'
    }).responseText;
    var operator = jQuery.parseJSON(data);
    return operator;
}

/**
 * 重置密码
 */
function resetPassword() {
	var row = $('#operatorTable').datagrid('getSelected');
	if (!row){
		$.messager.alert('警告','请选择一个管理员');
		return;
	}
	$.messager.confirm('重置密码','你确定要重置 '+row.name+'密码 为:111111?',function(ok){  
	    if(ok) {
	    	var url = "/se/operator/" + row.id + "/resetPassword.htm";
	    	$.ajax({
	  		  url: url,
	  		  async: false,
	  		  type:'post',
	  		  data:{'_method':'put'},
	  		  dataType:'json',
	  		  success:function(data) {
	  			  if(data.success) {
	  				$.messager.alert('','重置密码成功!');
	  				$('#operatorTable').datagrid("reload");
	  			  }
	  			  else {
	  				$.messager.alert('警告',data.msg);
	  			  }
	  		  }
	  		 });
	    }
	});
}