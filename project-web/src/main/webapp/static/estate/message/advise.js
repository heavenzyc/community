/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/advise/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'createAt',title:'提交时间',width: $(this).width() * 0.25, align: 'center'},
            {field:'content',title:'反馈内容',width: $(this).width() * 0.4, align: 'center'},
            {field:'user',title:'用户昵称',width: $(this).width() * 0.2, align: 'center',formatter:function(value, row, index){
                return value ? value.nickname : "";
            }},
            {field:'phone',title:'手机号',width: $(this).width() * 0.15, align: 'center'}
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

    var startDate = $("#txtStartDate").datebox("getValue");
    var endDate = $("#txtEndDate").datebox("getValue");

    if(startDate != "" ){
        params.startDate = startDate;
    }

    if(endDate!= ""){
        params.endDate = endDate;
    }

    $("#datagrid").datagrid("load", params);
}

