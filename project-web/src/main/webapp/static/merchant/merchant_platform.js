/**
 * 载入商户列表数据
 */
function loadList(){
	$('#dataTable').datagrid({
		nowrap: true,
		striped: true,
		fitColumns:true,
		toolbar:"#toolbar",
		url:'/se/merchant_platform/list?'+new Date().getTime(),
		method:'get',
		pageSize:10,
		pageNumber:1,
		pagination:true,
		pageList:[10,20],
		singleSelect:true,
		idField:'id',
		frozenColumns:[[
	        {field:'id',checkbox:true}
       ]],
       columns:[[
		{field:'name', title:'商家名称',width:$(this).width() * 0.1},
		{field:'address',title:'详细地址',width:$(this).width() * 0.4},
		{field:'telephone',title:'联系电话',width:$(this).width() * 0.1},
		{field:'firstOperatorLoginName',title:'登录帐号',width:$(this).width() * 0.1},
		{field:'firstOperatorName',title:'账户名称',width:$(this).width() * 0.1},
		{field:'op',title:'操作',width:$(this).width() * 0.2,
			formatter:function(value,row,index){
				var html = "<a style='color:#276ED9;'  href='javascript:void(0);' onclick='bindBusinessConfig("+index+")' >修改商户业务配置</a>&nbsp;&nbsp;&nbsp;&nbsp;";
			if(row.openManagerPlatform) {
				html += "<a style='color:#03B30C;' href='javascript:void(0);' onclick='resetMerchantOperatorPassword("+index+")' >重置密码</a>&nbsp;";
			} else {
				html += "<a style='color:#276ED9;' href='javascript:void(0);' onclick='openMerchantOperator("+index+")' >开通账户</a>&nbsp;";
			}
            return html;
        }}
		]],
		loadFilter:function(json) {
			var data = {};
			data.rows = json.merchants;
			data.total = json.pagination.count;
			data.pagination = json.pagination;
			return data;
		},
		onAfterSelectPage:function() {
			var pageOpt = $('#dataTable').datagrid('getPager').pagination("options");
		 
			$(this).datagrid("options").queryParams={
		        "name" : $("#name").val(),
		        "ts" : new Date().getTime(),
		        "currentPage":pageOpt.pageNumber,
		        "pageSize":pageOpt.pageSize
		    };
		}
	});
}
/**
 * 修改商户业务配置
 * @param index
 */
function bindBusinessConfig(index) {
	var row = ($('#dataTable').datagrid('getRows'))[index];
	 
	var dialogDom = $("<div id='openMerchantOperatorDiv'></div>");
	var loadUrl = "/local/dialog/merchant/create_merchant_operator.html #modify_config";
	dialogDom.load(loadUrl, function() {
		dialogDom.dialog({
			title : '修改业务配置',
			width : 350,
			modal : true,
			shadow : false,
			closed : false,
			height : 200,
			onClose : function() {
				$(this).dialog("destroy");
				
			}
		});
		var form = $(this).find("form");
		$.parser.parse(form);
		$("#businessConfigids").combobox({
			url:'/se/merchant_platform/loadBusinessConfig',
			method:'get',
		    valueField:'id',  
		    textField:'name',
		    multiple:true,
		    required:true,
		    editable:false,
		    loadFilter:function(data) {
		    	for(var i =0;i<data.length;i++) {
					for(var j = 0;j<row.merchantBusinessConfigs.length;j++) {
						if(data[i].id == row.merchantBusinessConfigs[j].businessConfig.id) {
							data[i].selected=true;
							break;
						}
					}
				}
			  return data; 
			}
		});	
		var cancelBtn = $(this).find("#cancelBtn");
		cancelBtn.click(function() {
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#saveBtn");
		saveBtn.click(function() {
			form.attr("action", "/se/merchant_platform/"+row.id+"/bindMerchantBusinessConfig");
			form.form("submit", {
				success : function(text) {
					var data = parserToJson(text);
					if (data.success) {
						dialogDom.dialog("destroy");
						loadList();
					} else {
						$("#errorMsg").html(data.msg);
					}
				}
			});
		}); 
	});
}
/**
 * 开通账户
 * @param index
 */
function openMerchantOperator(index) {
	var row = ($('#dataTable').datagrid('getRows'))[index];
	var dialogDom = $("<div id='openMerchantOperatorDiv'></div>");
	var loadUrl = "/local/dialog/merchant/create_merchant_operator.html #addOperatorDialog";
	dialogDom.load(loadUrl, function() {

		dialogDom.dialog({
			title : '开通账户',
			width : 460,
			modal : true,
			shadow : false,
			closed : false,
			height : 420,
			onClose : function() {
				$(this).dialog("destroy");
				
			}
		});
        $(".label-fl").width(100);
		var form = $(this).find("form");
		$.parser.parse(form);
		$("#businessConfigids").combobox({
			url:'/se/merchant_platform/loadBusinessConfig',
			method:'get',
		    valueField:'id',  
		    textField:'name',
		    multiple:true,
		    required:true,
		    editable:false,
		    loadFilter:function(data) {
		    	for(var i =0;i<data.length;i++) {
					for(var j = 0;j<row.merchantBusinessConfigs.length;j++) {
						if(data[i].id == row.merchantBusinessConfigs[j].businessConfig.id) {
							data[i].selected=true;
							break;
						}
					}
				}
			  return data; 
			}
		});	
			
		var cancelBtn = $(this).find("#cancelBtn");
		cancelBtn.click(function() {
			dialogDom.dialog("destroy");
		});
		var saveBtn = $(this).find("#saveBtn");
		saveBtn.click(function() {
			form.attr("action", "/se/merchant_platform/"+row.id+"/openFirstOperator");
			form.form("submit", {
				success : function(text) {
					var data = parserToJson(text);
					if (data.success) {
						dialogDom.dialog("destroy");
						loadList();
					} else {
						$("#errorMsg").html(data.msg);
					}
				}
			});
		}); 
	});
	
}

/**
 * 重置密码
 */
function resetMerchantOperatorPassword(index) {
	var row = ($('#dataTable').datagrid('getRows'))[index];
	if (!row){
		$.messager.alert('警告','请选择一个管理员');
		return;
	}
	$.messager.confirm('重置密码','你确定要重置 '+row.name +' 商户的帐号: '+row.firstOperatorLoginName+'密码 为:111111?',function(ok){  
	    if(ok) {
	    	var url = "/se/merchant_platform/"+row.id+"/resetMerOpPassword";
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
/**
 * 开启微信帐号
 */
function openWeixinOperator(index) {
	var row = ($('#dataTable').datagrid('getRows'))[index];
	if (!row){
		$.messager.alert('警告','请选择一个商家');
		return;
	}
	$.messager.confirm('开通微信','你确定要开通 '+row.name +' 商户的微信帐号 ', function(ok){
		if(ok) {
			$.post("/se/merchant_platform/weixin/"+row.id+"/",
					 function(data){
				$.messager.show({
					title:'提示',
					msg: data.msg,
					timeout:5000,
					showType:'slide'
				});
			   }, "json");
		}
	});
}