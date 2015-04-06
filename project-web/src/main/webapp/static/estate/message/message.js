/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
var dialog;
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/message/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'title',title:'标题',width: $(this).width() * 0.2, align: 'center'},
            {field:'contents',title:'推送内容',width: $(this).width() * 0.3, align: 'center'},
            {field:'beginTime',title:'推送时间',width: $(this).width() * 0.15, align: 'center'},
            {field:'operator',title:'发布者',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                return value ? value.name : "";
            }}/*,
            {field:'id',title:'操作',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a class="red" href="javascript:deleteItem('+row.id+');" class="link-update">查看详情</a>';
                return html;
            }}*/

        ]],
        loadFilter: function(json){
            var data = {};
            data.rows = json.data;

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

    dialog = $('#addMessageDialog');
    dialog.append($("#addMessageDialogTemplate").html());
    dialog.dialog({
        title: "创建新信息",
        width: 600,
        height: 480,
        cache: false,
        modal: true,
        buttons:[{
            text:'创建',
            handler:function(){
                    $('#addMessage').form('submit', {
                        url:"/se/estate/message/add.htm",
                        onSubmit: function(){
                            var isValid = $(this).form('validate');
                            return isValid;
                        },
                    success:function(data){
                        eval("var jsonData = " + data);
                        if(jsonData && jsonData.success){
                            $("#datagrid").datagrid("reload");
                            dialog.dialog("close");
                        }
                        else if(jsonData && !jsonData.success){
                            $("#errorMsg").text(jsonData.msg).show();
                        }
                    }
                });
            }
        },{
            text:'取消',
            handler:function(){
                dialog.dialog("close");
            }
        }]
    });
    dialog.dialog("close");
    $.parser.parse('#addMessageDialog');
});

function showAddMessageDialog(){
    $('#addMessage').form("clear");
    dialog.dialog("open");
    dialog.dialog("center");
}

/**
 * 重新获取数据
 */
function refreshData(){
    var params = {};

    var content = $("#txtContent").val();
    if(content != ""){
        params.content = content;
    }

    $("#datagrid").datagrid("load", params);
}

function deleteItem(id){
    if(!confirm("确定删除？")){return;}
    $.ajax({
        url:"/se/estate/comment/" + id + "/delete.htm",
        type:"POST",
        data:{"_method": "delete"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#datagrid").datagrid("reload");
            }
        }
    });
}

function enableItem(id){
    $.ajax({
        url:"/se/estate/comment/" + id + "/enable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#datagrid").datagrid("reload");
            }
        }
    });
}

function disableItem(id){
    $.ajax({
        url:"/se/estate/comment/" + id + "/disable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $("#datagrid").datagrid("reload");
            }
        }
    });
}
