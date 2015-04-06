Function.prototype.Apply = function(thisObj)
{
    var _method = this;
    return function(data)
    {
        return _method.apply(thisObj,[data]);
    };
}

/**
 * 构造评论列表数据
 */
$(function(){
    $("#datagrid").datagrid({
        url: "/merse/comment/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
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
                html += '<a href="javascript:showInfoDialog('+index+')" class="link-update">查看</a>';
                if (row.enabled || row.enabled == "true"){
                    html += '<a href="#" class="link-del" onclick="disableComment('+index+')">屏蔽</a>';
                }
                else {
                    html += '<a href="#" class="link-info" onclick="enableComment('+index+')">取消屏蔽</a>';
                }
                return html;
            }}

        ]],
        loadFilter: function(json){
            var data = {};
            data.rows = json.info;

            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $("#datagrid").datagrid('getPager').pagination("options");
            var pageParams = {
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };

            $(this).datagrid("options").queryParams= $.extend($(this).datagrid("options").queryParams, pageParams);
        },
        pagination:true,
        toolbar: "#banner_comment"
    });

    $("#selEnabled").combobox({
        onSelect: refreshData
    });
    $("#selReport").combobox({
        onSelect: refreshData
    });
    $("#selDate").combobox({
        onSelect: refreshData
    });

    $("#btnQuery").click(refreshData);
});

/**
 * 屏蔽评论
 */
function disableComment(index){
    var rows = $("#datagrid").datagrid("getRows");
    $.ajax({
        url:"/merse/comment/disable.htm",
        type:"GET",
        data:{"id":rows[index].id, "_method": "put"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#datagrid").datagrid("updateRow", {
                    index: index,
                    row:{
                        enabled: false
                    }
                });
                $("#datagrid").datagrid("refreshRow", index);
            }
        }
    });
}

/**
 * 取消屏蔽
 */
function enableComment(index){
    var rows = $("#datagrid").datagrid("getRows");
    $.ajax({
        url:"/merse/comment/enable.htm",
        type:"GET",
        data:{"id":rows[index].id, "_method": "put"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#datagrid").datagrid("updateRow", {
                    index: index,
                    row:{
                        enabled: true
                    }
                });
                $("#datagrid").datagrid("refreshRow", index);
            }
        }
    });
}

/**
 * 重新获取数据
 */
function refreshData(){
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

    $("#datagrid").datagrid("load", params);
}

var dialogDom;

function showInfoDialog(index){
    var rows = $("#datagrid").datagrid("getRows");
    var id = rows[index].id;
    if(!dialogDom){
        dialogDom = $('#dialogArea').empty();
        dialogDom.load("/local/dialog/farmhouse/comment.html #commentinfo-dialog",function(){
            dialogDom.dialog({
                title: "用户点评详情",
                width: 680,
                modal: true,
                height: 500,
                modal: true,
                closed: true,
                buttons:[{
                    text:'确定',
                    handler:function(){
                        dialogDom.dialog('close');
                    }
                }]
            });

            dialogDom.find("#btnDisable").click(function(){
                var enabled = $("#hdn_commentEnabled").val();
                var commentId = $("#hdn_commentId").val();
                if(enabled == "true"){
                    disableCommentById(commentId);
                }
                else{
                    enableCommentById(commentId);
                }
            });

            refreshDialogData(id);
            dialogDom.dialog("open");
        });
    }
    else{
        refreshDialogData(id);
        dialogDom.dialog("open");
    }
}

function refreshDialogData(id){
    $.ajax({
        url:"/merse/comment/info/" + id,
        type:"GET",
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.id){
                bindLabelValue(data)
            }
        }
    });
}

/**
 * 绑定评论信息详情
 * @param data
 */
function bindLabelValue(data){
    $("#txt_name").html(data.merchant.name);
    $("#txt_createDate").html(data.createDate);
    $("#txt_user").html(data.user.nickname);
    $("#txt_content").html(data.content);
    $("#txt_agreeCount").html(data.agreeCount);
    $("#txt_reportCount").html(data.reportCount);
    var enabled = data.enabled ? "正常" : "已屏蔽";
    var btnText = data.enabled ? "屏蔽" : "取消屏蔽"
    $("#hdn_commentEnabled").val(data.enabled);
    $("#btnDisable").text(btnText);
    if(data.enabled){
        $("#btnDisable").addClass("link-del");
    }
    else{
        $("#btnDisable").removeClass("link-del");
    }
    $("#txt_enabled").html(enabled);
    $("#txt_impressTag").html(data.impressTag.name);
    if(data.commentPictures[0]){
        $("#img_comment").attr("src", data.commentPictures[0].picUrlPath);
    }
    else{
        $("#img_comment").attr("src", "");
    }

    $("#hdn_commentId").val(data.id);
}

function disableCommentById(id){
    $.ajax({
        url:"/merse/comment/disable.htm",
        type:"GET",
        data:{"id":id, "_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#txt_enabled").html("已屏蔽");
                $("#btnDisable").text("取消屏蔽").removeClass("link-del");
                $("#hdn_commentEnabled").val(false);
            }
        }
    });
}

function enableCommentById(id){
    $.ajax({
        url:"/merse/comment/enable.htm",
        type:"GET",
        data:{"id":id, "_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#txt_enabled").html("正常");
                $("#btnDisable").text("屏蔽").addClass("link-del");
                $("#hdn_commentEnabled").val(true);
            }
        }
    });
}

