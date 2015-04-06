/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/comment/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'nickName',title:'用户昵称',width: $(this).width() * 0.1, align: 'center'},
            {field:'content',title:'点评内容',width: $(this).width() * 0.2, align: 'center',formatter:function(value, row, index){
                if(value){
                    return value;
                }
                /*else{
                    if(row.commentType && row.commentType._name == "AGREE"){
                        return "赞！"
                    }
                }*/
            }},
            {field:'scoreValue',title:'评分',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                if(value){
                    return value;
                }
                /*else{
                    if(row.commentType && row.commentType._name == "AGREE"){
                        return "-"
                    }
                }*/
            }},
            {field:'building',title:'关联楼盘',width: $(this).width() * 0.1, align: 'center'},
            {field:'commonCommentType',title:'点评对象',width: $(this).width() * 0.1, align: 'center'},
            {field:'createDate',title:'点评时间',width: $(this).width() * 0.15, align: 'center'},
            {field:'id',title:'操作',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a class="red" href="javascript:deleteItem('+row.id+');" class="link-update">删除</a>';
                if(row.enabled){
                    html += '<a href="javascript:disableItem('+row.id+');" class="link-update">屏蔽</a>';
                }
                else{
                    html += '<a href="javascript:enableItem('+row.id+');" class="link-update">取消屏蔽</a>';
                }
                return html;
            }}

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
});

/**
 * 重新获取数据
 */
function refreshData(){
    var params = {};

    var content = $("#txtContent").val();
    if(content != ""){
        params.key = content;
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
