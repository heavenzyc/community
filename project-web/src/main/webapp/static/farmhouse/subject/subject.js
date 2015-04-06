/**
 * User: zhouyajun
 * Date: 13-4-19
 * Time: 上午10:14
 */

$(function(){
    $("#datagrid").datagrid({
        url: "/se/subject/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        columns:[[
            {field:'title',title:'主题名称',width: $(this).width() * 0.2, align: 'center'},
            {field:'createDate',title:'发表时间',width: $(this).width() * 0.2, align: 'center'},
            {field:'content',title:'主题详情',width: $(this).width() * 0.25, align: 'center'},
            {field:'status',title:'状态',width: $(this).width() * 0.1, align: 'center',
                formatter: function(value, row, index){
                	alert(value);
                    if (value._name && value._name.toUpperCase() == "REVIEWED"){
                        return "审核通过";
                    } else {
                        return "已发布";
                    }
                }
            },
            {field:'id',title:'操作',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="/se/subject/list/' + row.id + '" class="link-info">查看</a>';
                html += '<a href="javascript:showInfoDialog('+index+')" class="link-update">编辑</a>';
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
});

/**
 * 重新获取数据
 */
function refreshData(){
    var params = {};
    var createDate = $("#txt_createDate").datebox("getValue");
    if(createDate != ""){
        params.createDate = createDate;
    }

    var title = $("#txtQuery").val();
    if(title != ""){
        params.title = title;
    }

    $("#datagrid").datagrid("load", params);
}

