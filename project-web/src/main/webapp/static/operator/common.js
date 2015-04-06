/**
 * 打开IFRAME方法
 */
function openFrame(url,centerTitile){
	var centerPanel = $('body').layout('panel','center');
	centerPanel.panel("setTitle",centerTitile);
	$('#centerFrame').attr('src',url);
}
/**
 * 退出登录
 */
function logOut(){
	window.location.href="/se/logout.htm";
}

/**
 * 得到当前管理员
 */
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
 * js 登录验证
 */
function loginCheck() {
	var operator = currentOperator();
	if(!operator) {
		if(window.parent){
			window.parent.location.href="/se/";
		}
		else{
			window.location.href="/se/";
		}
	}
}
function fillCommonCombox(dom,_class,selectedValue) {
	dom.combobox({
		url : '/json/getEnum.htm?className='+_class,
		method : 'get',
		valueField : 'value',
		textField : 'desc',
		multiple : false,
		editable : false,
		panelHeight : true
	});
	if(selectedValue) {
		dom.combobox("select",selectedValue);
	}
	
}

/**
 * 修改密码
 */
function modifyPassword() {
	var dialogDom = $("<div id='modifyPasswordDivDialog'></div>");
	var loadUrl = "./operator/dialog.html?t=" + Math.random() + " #modifyPasswordDiv";
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
			form.attr("action", "/se/operator/modifyPassword.htm");
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
		height : 280,
		onClose : function() {
			$(this).dialog("destroy");
		}
	});
}
