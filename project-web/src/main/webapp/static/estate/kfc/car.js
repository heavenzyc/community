/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
var dialog;
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/kfc/car/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'plateNumber',title:'车牌号',width: $(this).width() * 0.2, align: 'center'},
            {field:'deveiceId',title:'设备号',width: $(this).width() * 0.2, align: 'center'},
            {field:'driverName',title:'司机',width: $(this).width() * 0.15, align: 'center'},
            {field:'totalSeat',title:'可载人数',width: $(this).width() * 0.1, align: 'center'},
            {field:'emptySeat',title:'剩座状态',width: $(this).width() * 0.1, align: 'center'},
            {field:'kfcLine',title:'当前路线',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                return value ? value.lineNumber + "路线" : "";
            }},
            {field:'id',title:'操作',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="/se/estate/kfc/car/'+row.id+'/edit.htm" class="link-update">设置</a>';
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
