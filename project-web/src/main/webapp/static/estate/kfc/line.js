/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
var dialog;
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/kfc/line/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'lineNumber',title:'线路名称',width: $(this).width() * 0.2, align: 'center',formatter:function(value, row, index){
                return value ? value + "号线" : "";
            }},
            {field:'buildings',title:'线路楼盘',width: $(this).width() * 0.7, align: 'center',formatter:function(value, row, index){
                if(value && value.length > 0){
                    var text = "";
                    for(var i = 0; i < value.length; i++){
                        if(value[i].building && value[i].building.name){
                            text += value[i].building.name + " -> ";
                        }
                    }
                    if(value.length > 0){
                        text = text.substring(0, text.length - 4);
                    }
                    return text;
                }
                else{
                    return ""
                }
            }},
            {field:'id',title:'操作',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="/se/estate/kfc/line/'+row.id+'/edit.htm" class="link-update">设置</a>';
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

