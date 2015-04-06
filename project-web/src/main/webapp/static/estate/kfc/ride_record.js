/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-17
 * Time: 上午10:19
 */
var dialog;
$(function(){
    $("#datagrid").datagrid({
        url: "/se/estate/kfc/ride_record/list.htm",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        scrollbarSize:0,
        rownumbers:true,
        columns:[[
            {field:'request',title:'用户名',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                return value ? (value.user ? value.user.loginName : "") : "";
            }},
            {field:'lineNumber',title:'路线',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                return row.car ? (row.car.kfcLine ? row.car.kfcLine.lineNumber + "号线" : "") : "";
            }},
            {field:'car',title:'车辆',width: $(this).width() * 0.15, align: 'center',formatter:function(value, row, index){
                //return value ? (value.kfcLine ? value.kfcLine : "") : "";
                return value ? value.plateNumber : "";
            }},
            {field:'onboardPeopleNumber',title:'乘车人数',width: $(this).width() * 0.1, align: 'center'},
            {field:'onboardAddress',title:'接送地点',width: $(this).width() * 0.2, align: 'center'},
            {field:'onboardTime',title:'上车时间',width: $(this).width() * 0.1, align: 'center'},
            {field:'requestType',title:'请求种类',width: $(this).width() * 0.1, align: 'center', formatter:function(value, row, index){
                return row.request ? (row.request.requestType ? row.request.requestType.desc : "") : "";
            }},
            {field:'id',title:'操作',width: $(this).width() * 0.1, align: 'center',formatter:function(value, row, index){
                var html = '';
                html += '<a href="/se/estate/kfc/ride_record/'+row.id+'/" class="link-update">查看详情</a>';
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

    $("#selLine").combobox({
        onSelect: function(param){
            var lineId = $("#selLine").combobox("getValue");
            var url = "/se/estate/kfc/ride_record/" + lineId + "/cars";
            $.getJSON(url,function(data){
                $('#selCar').combobox('clear');
                $('#selCar').combobox('loadData', data);
                $('#selCar').combobox('select', "");
            });
        }
    });
});

/**
 * 重新获取数据
 */
function refreshData(){
    var params = {};

    var key = $("input[name='key']").val();
    if($.trim(key) != ""){
        params.key = key;
    }
    var startDate = $("#txtStartDate").datetimebox("getValue");
    if($.trim(startDate) != ""){
        params.startDate = startDate;
    }
    var endDate = $("#txtEndDate").datetimebox("getValue");
    if($.trim(endDate) != ""){
        params.endDate = endDate;
    }

    var lineId = $("#selLine").combobox("getValue");
    if($.trim(lineId) != ""){
        params.lineId = lineId;
    }

    var carId = $("#selCar").combobox("getValue");
    if($.trim(carId) != ""){
        params.carId = carId;
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

