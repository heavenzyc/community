/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/question/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'questionUser',title:'提问人',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                return value ? value.nickname : "";
            }},
            {field:'content',title:'提问内容',width: $(this).width() * 0.2, align: 'center'},
            {field:'building',title:'楼盘',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                return value ? value.name : "";
            }},
            {field:'pv',title:'查看次数',width: $(this).width() * 0.1, align: 'center'},
            {field:'answerNum',title:'回复次数',width: $(this).width() * 0.1, align: 'center'},
            {field:'laudNum',title:'赞/次',width: $(this).width() * 0.1, align: 'center'},
            {field:'updateDate',title:'提问时间',width: $(this).width() * 0.15, align: 'center'},
            {field:'id',title:'操作',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="/se/estate/question/' + row.id + '/" class="link-info">查看详情</a>';
                html += '<a href="javascript:deleteItem('+row.id+');" class="link-update">删除</a>';
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
        params.content = content;
    }

    $("#datagrid").datagrid("load", params);
}

function deleteItem(id){
    if(!confirm("确定删除？")){return;}
    $.ajax({
        url:"/se/estate/question/" + id + "/delete.htm",
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
        url:"/se/estate/question/" + id + "/enable.htm",
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
        url:"/se/estate/question/" + id + "/disable.htm",
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
