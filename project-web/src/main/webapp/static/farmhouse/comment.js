/**
 * 载入热评列表数据
 */
function loadComments(){
    $('#commentTable').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/se/comment/list.htm',
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        columns:[[
            {field:'merchant',title:'商家名称',width: $(this).width() * 0.2, align: 'center',
                formatter: function(value,row,index){
                    return value.name;
                }
            },
            {field:'createDate',title:'发表时间',width: $(this).width() * 0.2, align: 'center'},
            {field:'content',title:'热评详情',width: $(this).width() * 0.25, align: 'center'},
            {field:'enabled',title:'状态',width: $(this).width() * 0.1, align: 'center',
                formatter: function(value,row,index){
                    if (value || value == "true"){
                        return "正常";
                    }
                    else {
                        return "已屏蔽";
                    }
                }
            },
            {field:'reportCount',title:'举报次数',width: $(this).width() * 0.1, align: 'center'},
            {field:'id',title:'操作',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="#" onclick="viewComment(' + row.id + ')" class="link-update">查看</a>';
                if (row.enabled || row.enabled == "true"){
                    html += '<a href="#" class="link-del" onclick="disableComment(' + row.id + ')">屏蔽</a>';
                }
                else {
                    html += '<a href="#" class="link-info" onclick="enableComment('+row.id+')">取消屏蔽</a>';
                }
                return html;
            }}

        ]],

        loadFilter : loadFilter,

        onAfterSelectPage:onAfterSelectPage
    });
}

function onAfterSelectPage() {
    var pageOpt = $('#commentTable').datagrid('getPager').pagination("options");
    var queryParams = getParams();
    $('#commentTable').datagrid("options").queryParams={
//        "status" : $("#status :selected").val(),
//        "title" : $("#title").val(),
//        "startDate" :  $('#startDate').datebox('getValue'),
//        "endDate" :  $('#endDate').datebox('getValue'),
        "enabled" : queryParams.enabled,
        "report" : queryParams.report,
        "dateType" : queryParams.dateType,
        "dateValue" : queryParams.dateValue,
        "content" : queryParams.content,
        "ts" : new Date().getTime(), //时间戳，兼容IE，解决IE缓存问题
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}

/**\
 * 获取查询参数
 * @return {{}}
 */
function getParams(){
    var params = {};
    var enabled = $("#selEnabled").combobox("getValue");
    if(enabled != ""){
        params.enabled = enabled;
    }
    var report = $("#selReport").combobox("getValue");
    if(report != ""){
        params.report = report;
    }
    var date = $("#selDate").combobox("getValue");
    if(date != ""){
        var type = date.substring(date.length-1, date.length);
        var value = date.substring(0, date.length-1);
        switch(type){
            case "d":
                params.dateType = "day";
                params.dateValue = value;
                break;
            case "w":
                params.dateType = "week";
                params.dateValue = value;
                break;
            case "y":
                params.dateType = "month";
                params.dateValue = value;
                break;
        }
    }

    var content = $("#txtQuery").val();
    if(content != ""){
        params.content = content;
    }
    return params;
}


function loadFilter(json) {
    var data = {};
    if(!json.info){
        data.rows = [];
    } else {
        data.rows = json.info;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}

function loadComment(id,form){
    $.getJSON("/se/comment/" + id + "/", function(json){
        var domName = '';
        var domNames = new Array();
        //嵌套对象load，使用数组方式来读取JSON对象
        //只支持两层嵌套
        $(form).find("input").each(function(){
            domName = $(this).attr("name");
            if(domName){
                if(domName.indexOf(".") > 0){
                    index = '';
                    domNames = domName.split(".");
                    $(this).val(json[domNames[0]][domNames[1]]);
                }else{
                    if(domName == "enabled"){
                        if(json[domName] == true){

                            $(this).val('正常');
                            $("#operator").html("屏蔽");
                            $("#operator").bind("click",{id : json.id},disable);

                        }else if(json[domName] == false){
                            $(this).val("已屏蔽");
                            $("#operator").html("取消屏蔽");
                            $("#operator").bind("click",{id : json.id},enable);
                        }else{
                            $(this).val(json[domName]);
                        }
                    }else{
                        $(this).val(json[domName]);
                    }
                }
            }
        });
        $(form).find("img").attr("src",json.merchant.oldLogUrlPath);
    });
}

function disable(event){
    disableComment(event.data.id);
    $("#operator").html("取消屏蔽");
}

function enable(event){
    enableComment(event.data.id);
    $("#operator").html("屏蔽");
}



function viewComment(id){
    var dialogDom = $("<div id='commentinfo-dialog'></div>");

    dialogDom.load("/local/dialog/farmhouse/comment.html #commentinfo-dialog-div",function(){
        invodDialog(dialogDom,"查看");
        var form = $(dialogDom).find("form");
        loadComment(id,form);
        form.find("input,textarea").each(function(){
              $(this).attr("disabled","disabled");
        });
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
        height: 300,
        onClose:function() {
            $(this).dialog("destroy");
        }
    });
}

/**
 * 屏蔽评论
 */
function disableComment(id){
    $.ajax({
        url:"/se/comment/disable.htm",
        type:"GET",
        data:{"id":id, "_method": "put"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#commentTable").datagrid("reload");
            }
        }
    });
}

/**
 * 取消屏蔽
 */
function enableComment(id){
    $.ajax({
        url:"/se/comment/enable.htm",
        type:"GET",
        data:{"id":id, "_method": "put"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#commentTable").datagrid("reload");
            }
        }
    });
}


