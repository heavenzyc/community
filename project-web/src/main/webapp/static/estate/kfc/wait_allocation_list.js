/**
 * tabs标签切换数据
 * @param title
 * @param index
 */
function selectTab(title,index){
    if(index == 0){
        searchWaitAllocation();
    }else if(index == 1){
        searchAllocated();
    }
    else if(index == 2){
        searchRefused();
    } else {
        searchInvalid()
    }
}


var refuseDialog;
var allocationDialog;
/**
 * 待分配列表
 */
function searchWaitAllocation(){
    $("#wait_allocation_table").datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        url: "/se/estate/kfc/req/allocating",
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
      //  rownumbers: true,
     //   scrollbarSize:0,
    //    rownumbers: true,
        columns:[[
            {field:'id',title:'编号',width:$(this).width() * 0.1, align: 'center',hidden:true},
            {field:'loginName',title:'用户名',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,indes){
                return row.user.loginName;
            }},
            {field:'lineNumber',title:'路线',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.line.lineNumber != null){
                    return row.line.lineNumber;
                }
            }},
            {field:'receivePhone',title:'接送手机',width:$(this).width() * 0.1, align: 'center'},
            {field:'peopleNumber',title:'报名人数',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestTime',title:'请求时间',width:$(this).width() * 0.1, align: 'center', formatter:function(value,row,index){
                return formatDate(value, 'yyyy-MM-dd hh:mm');
            }},
            {field:'receiveAddress',title:'接送地点',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestTimeStr',title:'接送时间',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestType',title:'请求种类',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.requestType.desc;
            }},
            /*{field:'plateNumber',title:'车辆',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.car.plateNumber;
            }},
            {field:'rejectReason',title:'拒绝备注',width:$(this).width() * 0.1, align: 'center'},*/
            {field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
                var html = '';
                html += '<a href="###" class="link-update" onclick="createDialog(' + row.id + ',\'' + row.line.id + '\',\'' + row.peopleNumber + '\')">分配</a>';
                html += '<a href="###" class="link-update" onclick="sumbit_refuse(' + row.id + ')">拒绝</a>';
                return html;
            }, align: 'center'}
        ]],
        loadFilter : function(json) {
            var data = {};
            if(!json.reqList){
                data.rows = [];
            } else {
                data.rows = json.reqList;
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $('#wait_allocation_table').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "ts":new Date().getTime(),
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
    });


    refuseDialog = $('#refuse_dialog');
    refuseDialog.show();
    refuseDialog.dialog({
        title: '',
        width: 400,
        height: 200,
        closed: true,
        cache: false,
        modal: true,
        closed: true,
        buttons:[{
            text:'保存',
                 handler:refuse
        },{
            text:'取消',
            handler:function(){
                $("#reason").val("");
                refuseDialog.dialog('close');
            }
        }]
    });

    allocationDialog = $('#allocation_dialog');
    allocationDialog.show();
    allocationDialog.dialog({
        title: '',
        width: 350,
        height: 150,
        closed: true,
        cache: false,
        modal: true,
        closed: true,
        buttons:[{
            text:'保存',
            handler:allocation
        },{
            text:'取消',
            handler:function(){
                allocationDialog.dialog('close');
            }
        }]
    });
}

/**
 * 车辆分配弹出框
 * @param requestId
 * @param lineId
 */
var reqId;
function createDialog(requestId, lineId, peopleNum){
    reqId = requestId
    $.ajax({
        url:'/se/estate/kfc/req/allocate',
        async: false,
        data:{lineId:lineId,peopleNum:peopleNum},
        success:function(data){
            createCarsSelect(data);
        }
    });
}

function createCarsSelect(data){
    if(data.length == 0){
        $.messager.alert("提示","当前没有可分配的车辆！");
        return;
    }
    allocationDialog.dialog('open');
    for(var i=0; i<data.length; i++){
        $("#car").append("<option value="+data[i].id+">"+data[i].plateNumber+"</option>");
    }
}

/**
 * 分配车
 * @param id
 */
function allocation(){

    var carId = $("#car").find("option:selected").val();
    $.ajax({
        url:"/se/estate/kfc/req/allocate/"+reqId,
        async:false,
        type:'POST',
        data:{carId:carId},
        success:function(data){
            if(data.success) {
                allocationDialog.dialog('close');
                $('#wait_allocation_table').datagrid("reload");
            }
            else {
                allocationDialog.dialog('close');
                $.messager.alert('警告',data.msg);
            }
        }
    });
}

var refuse_id;
function sumbit_refuse(id){
    refuse_id = id;
    refuseDialog.dialog('open');
}

function refuse(){
    var url = "/se/estate/kfc/req/refuse/"+refuse_id;
    var reason = $("#reason").val();
    var notNull = $("#reason").validatebox("isValid");
    if(!notNull) return;
    $.ajax({
        url: url,
        async: false,
        type:'POST',
        dataType:'json',
        data : {reason:reason},
        success:function(data) {
            if(data.success) {
                refuseDialog.dialog('close');
                //    $.messager.alert('提示',"操作成功！");
                $("#reason").val("");
                $('#recommend_table').datagrid("reload");
            }
            else {
                $("#reason").val("");
                refuseDialog.dialog('close');
                $.messager.alert('警告',data.msg);
            }
        }
    });
}

/**
 * 已分配列表
 */
function searchAllocated(){
    $("#already_allocation_table").datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        url: "/se/estate/kfc/req/allocated",
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        //  rownumbers: true,
        //   scrollbarSize:0,
        //    rownumbers: true,
        columns:[[
            {field:'id',title:'编号',width:$(this).width() * 0.1, align: 'center',hidden:true},
            {field:'loginName',title:'用户名',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,indes){
                return row.user.loginName;
            }},
            {field:'lineNumber',title:'路线',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.line.lineNumber != null){
                    return row.line.lineNumber;
                }
            }},
            {field:'receivePhone',title:'接送手机',width:$(this).width() * 0.1, align: 'center'},
            {field:'peopleNumber',title:'报名人数',width:$(this).width() * 0.1, align: 'center'},
            {field:'receiveAddress',title:'接送地点',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestTimeStr',title:'接送时间',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestType',title:'请求种类',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.requestType.desc;
            }},
            {field:'plateNumber',title:'车辆',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.car != null){
                    return row.car.plateNumber;
                }

             }}
             /*{field:'rejectReason',title:'拒绝备注',width:$(this).width() * 0.1, align: 'center'},*/
            /*{field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
                var html = '';
                html += '<a href="###" class="link-update" onclick="allocation(' + row.id + ')">分配</a>';
                html += '<a href="###" class="link-update" onclick="sumbit_refuse(' + row.id + ')">拒绝</a>';
                return html;
            }, align: 'center'}*/
        ]],
        loadFilter : function(json) {
            var data = {};
            if(!json.reqList){
                data.rows = [];
            } else {
                data.rows = json.reqList;
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $('#wait_allocation_table').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "ts":new Date().getTime(),
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
    });
}

/**
 * 已拒绝列表
 */
function searchRefused(){
    var carFlag = false;
    var check = $("#car_refuse").attr("checked");
    if(check == 'checked'){
        carFlag = true;
    }else{
        carFlag = false;
    }
    var carId = $("#carNum").find("option:selected").val();
    $("#refuse_table").datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#feuse_toolbar",
        url: "/se/estate/kfc/req/refused?carFlag="+carFlag+"&carId="+carId,
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        //  rownumbers: true,
        //   scrollbarSize:0,
        //    rownumbers: true,
        columns:[[
            {field:'id',title:'编号',width:$(this).width() * 0.1, align: 'center',hidden:true},
            {field:'loginName',title:'用户名',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,indes){
                return row.user.loginName;
            }},
            {field:'lineNumber',title:'路线',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.line.lineNumber != null){
                    return row.line.lineNumber;
                }
            }},
            {field:'receivePhone',title:'接送手机',width:$(this).width() * 0.1, align: 'center'},
            {field:'peopleNumber',title:'报名人数',width:$(this).width() * 0.1, align: 'center'},
            {field:'receiveAddress',title:'接送地点',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestTimeStr',title:'接送时间',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestType',title:'请求种类',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.requestType.desc;
            }},
            {field:'plateNumber',title:'车辆',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.car != null){
                    return row.car.plateNumber;
                }

            }},
            {field:'rejectReason',title:'拒绝备注',width:$(this).width() * 0.1, align: 'center'}
            /*{field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
             var html = '';
             html += '<a href="###" class="link-update" onclick="allocation(' + row.id + ')">分配</a>';
             html += '<a href="###" class="link-update" onclick="sumbit_refuse(' + row.id + ')">拒绝</a>';
             return html;
             }, align: 'center'}*/
        ]],
        loadFilter : function(json) {
            var data = {};
            if(!json.reqList){
                data.rows = [];
            } else {
                data.rows = json.reqList;
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $('#refuse_table').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "ts":new Date().getTime(),
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
    });
}


/**
 * 已作废
 */
function searchInvalid(){
    $("#invalid_table").datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        url: "/se/estate/kfc/req/invalid",
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        //  rownumbers: true,
        //   scrollbarSize:0,
        //    rownumbers: true,
        columns:[[
            {field:'id',title:'编号',width:$(this).width() * 0.1, align: 'center',hidden:true},
            {field:'loginName',title:'用户名',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,indes){
                return row.user.loginName;
            }},
            {field:'lineNumber',title:'路线',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.line.lineNumber != null){
                    return row.line.lineNumber;
                }
            }},
            {field:'receivePhone',title:'接送手机',width:$(this).width() * 0.1, align: 'center'},
            {field:'peopleNumber',title:'报名人数',width:$(this).width() * 0.1, align: 'center'},
            {field:'receiveAddress',title:'接送地点',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestTimeStr',title:'接送时间',width:$(this).width() * 0.1, align: 'center'},
            {field:'requestType',title:'请求种类',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.requestType.desc;
            }},
            {field:'plateNumber',title:'车辆',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.car != null){
                    return row.car.plateNumber;
                }
            }}
            /*{field:'rejectReason',title:'拒绝备注',width:$(this).width() * 0.1, align: 'center'},*/
            /*{field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
             var html = '';
             html += '<a href="###" class="link-update" onclick="allocation(' + row.id + ')">分配</a>';
             html += '<a href="###" class="link-update" onclick="sumbit_refuse(' + row.id + ')">拒绝</a>';
             return html;
             }, align: 'center'}*/
        ]],
        loadFilter : function(json) {
            var data = {};
            if(!json.reqList){
                data.rows = [];
            } else {
                data.rows = json.reqList;
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $('#invalid_table').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "ts":new Date().getTime(),
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
    });
}




