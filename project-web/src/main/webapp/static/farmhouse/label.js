/**
 * 载入限时特价列表数据
 */
function loadLabels(){
    $('#labelTable').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/merse/label/labels.htm',
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        columns:[[
            {field:'id',title:'图片',align:'center',formatter:function(value,row,index){
                  var html = "<img style='width:50px;height:50px;' src='" + row.path + "'/>" ;
                return html;
            }},
            {field:'name',title:'名称',align:'center',width:$(this).width() * 0.2},
            {field:'description',title:'描述',align:'center',width:$(this).width() * 0.4},
            {field:'operate',align:'center',title:'操作',width:$(this).width() * 0.2,formatter:function(value,row,index){
                var html = "";
                html += "<a href='#' class='link-update' onclick='view(" + row.id + ")'>查看</a>";
                html += "<a href='#' class='link-update' onclick='edit(" + row.id + ")'>编辑</a>";
                html += "<a href='#' class='link-update' onclick='destroy(" + row.id + ")'>删除</a>";
                return html;
            }},
            {field:'ts',align:'center',title:'发布时间',width:$(this).width() * 0.2}
        ]],

        loadFilter : loadFilter,

        onAfterSelectPage:onAfterSelectPage
    });
}

function onAfterSelectPage() {
    var pageOpt = $('#labelTable').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "name" : $("#name").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}
function loadFilter(json) {
    var data = {};
    if(!json.labels){
        data.rows = [];
    } else {
        for (var i in json.labels){
            if(json.labels[i].labelPicture){
                json.labels[i].path = json.labels[i].labelPicture.path;
            }
        }

        data.rows = json.labels;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}

function load(id,form){
    $.getJSON("/merse/label/" + id + "/", function(json){
        form.form('load',json);
        var $pathInput = $(form).find("input[name='labelPicture.path']");
        $pathInput.val(json.labelPicture.path);
        $(form).find("img").attr("src",$pathInput.val());
    });
}

function view(id){
    var dialogDom = $("<div id='addDialog'></div>");
    invodDialog(dialogDom,"查看");
    dialogDom.load("/local/dialog/farmhouse/label.html",function(){
        var form = $(this).find("form");
        load(id,form);
        form.find("input,textarea").each(function(){
              $(this).attr("disabled","disabled");
        });
        $("#saveBtn").hide();
    });


}

function invodDialog(obj,title){
    $(obj).dialog({
        title: title,
        width: 600,
        modal: true,
        shadow: true,
        closed: false,
        draggable : false,
        height: 400,
        onClose:function() {
            $(this).dialog("destroy");
        }
    });
}


function edit(id){
    var dialogDom = $("<div id='addDialog'></div>");
    invodDialog(dialogDom,"编辑特色项目");
    dialogDom.load("/local/dialog/farmhouse/label.html #label-dialog",function(){
        var form = $(this).find("form");
        load(id,form);
        $.parser.parse(form);
        var saveBtn = $(this).find("#saveBtn");
        saveBtn.click(function(){
            submitHandler(form,"/merse/label/save.htm?_method=POST",dialogDom);
        });
    });


}

function destroy(id){
    $.post("/merse/label/" + id + "/", {_method:"DELETE"},function(json){
        if(json.success){
            $('#labelTable').datagrid("reload");
        }
    },"json");
}

function submitHandler(form,url,dialogDom){
    form.form("submit",{
        url:url,
        onSubmit:function() {
            var ok = $(this).form('validate');
            if(!ok) return false;
        },
        success:function(text){
            var data = parserToJson(text);
            if(data.success) {
                dialogDom.dialog("destroy");
                $('#labelTable').datagrid("reload");
            }
            else {
                $("#errorMsg").html(data.msg);
            }
        }
    });
}

function add(){
    var dialogDom = $("<div id='addDialog'></div>");
    invodDialog(dialogDom,"添加特色项目");
    dialogDom.load("/local/dialog/farmhouse/label.html #label-dialog",function(){
        var form = $(this).find("form");
        $.parser.parse(form);
        var saveBtn = $(this).find("#saveBtn");
        saveBtn.click(function(){
            submitHandler(form,"/merse/label/save.htm?_method=PUT",dialogDom);
        });
    });
}






$(function(){
    var options = {
        uploadUrl : "/upload/DISHBIGPIC/pic/600/300",  //上传图片的路径
        uploadInputName : 'filedata' //后台接收文件的名称
    };
    uploadDialogTools.initUploadOptions(options);  //初始化上传参数
    uploadDialogTools.init(); //初始化上传图片按钮
    uploadDialogTools.bindOnBeforeShowDialog(beforeSelected); //绑定显示上传弹出层之前的回调事件
    uploadDialogTools.bindOnSelected(selected); //绑定上传对话框点确认按钮之后的回调事件

});

/**
 *   确认上传成功之后设置图片描述信息的值
 *   uploadArea为点击的上传的Dom元素（jQuery对象）
 */
function selected(uploadArea){
}

/**
 *   在显示上传对话框之前初始化图片描述信息
 *   uploadArea为点击的上传的Dom元素（jQuery对象）
 */
function beforeSelected(uploadArea){
}


