
$(function(){
    invodDialog();
}) ;

//--------------------待发布楼盘列表-----------start--------------//
/**
 * 载入楼盘列表数据
 */
function loadUnreleaseList(){
    $('#unrelease_table').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#unrelease_toolbar",
        url:'/se/estate/building/building.htm',
        method:'post',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        nowrap:false,
        columns:[[
            {field:'name',title:'项目名称',align:'center',width:$(this).width() * 0.1},
            {field:'position',title:'项目地址',align:'center',width:$(this).width() * 0.1,formatter:function(value,row,index){
                return value.bulidAddress;
            }},
            {field:'updateDate',title:'更新时间',align:'center',width:$(this).width() * 0.1},
            {field:'id',title:'操作',align:'center',width:$(this).width() * 0.1,formatter:function(value,row,index){
                var html = "";
                if(row.contractType.key == 'CONTRACT'){
                    html += "<a href='/se/estate/building/" + value + "/'>编辑</a>&nbsp;|&nbsp;";
                    html += "<a onclick='release(\"unrelease_table\"," + value + ")'>发布</a>&nbsp;|&nbsp;";
                    html += "<a onclick='configAcount(" + value + ")'>账户设置</a>&nbsp;|&nbsp;";
                    html += "<a onclick=removeBuilding('unrelease_table',"+ value +")>删除</a>";
                }else{
                    html += "<a href='/se/estate/building/" + value + "/'>编辑</a>&nbsp;|&nbsp;";
                    html += "<a onclick='release(\"unrelease_table\"," + value + ")'>发布</a>&nbsp;|&nbsp;";
                    html += "<a onclick=removeBuilding('unrelease_table',"+ value +")>删除</a>&nbsp;|&nbsp;";
                }
                return html;
            }}
        ]],

        loadFilter : loadFilterUnrelease,

        onAfterSelectPage:onAfterSelectPageUnrelease
    });
}

function onAfterSelectPageUnrelease() {
    var pageOpt = $('#unrelease_table').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "releaseStatus" : 'PLAN',
        "contractType" : $("#UnreleaseContractType :selected").val(),
        "name" : $("#unrelease_name").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}



function loadFilterUnrelease(json) {
    var data = {};
    if(!json.buildings){
        data.rows = [];
    } else {
        data.rows = json.buildings;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}
//--------------------待发布楼盘列表-----------end--------------//


//--------------------已发布楼盘列表-----------start--------------//
function loadReleaseList(){
    $('#release_table').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#release_toolbar",
        url:'/se/estate/building/building.htm',
        method:'post',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        nowrap:false,
        columns:[[
            {field:'name',title:'项目名称',align:'center',width:$(this).width() * 0.1},
            {field:'position',title:'项目地址',align:'center',width:$(this).width() * 0.1,formatter:function(value,row,index){
                 return value.bulidAddress;
            }},
            {field:'updateDate',title:'更新时间',align:'center',width:$(this).width() * 0.1},
            {field:'id',title:'操作',align:'center',width:$(this).width() * 0.1,formatter:function(value,row,index){
                var html = "";
                if(row.contractType.key == 'CONTRACT'){
                    html += "<a href='/se/estate/building/" + value + "/?isView=true'>详情</a>&nbsp;|&nbsp;";
                    html += "<a onclick=removeContract('release_table',"+ value +")>停止合作</a>&nbsp;|&nbsp;";
                    html += "<a onclick='configAcount(" + value + ")'>账户设置</a>&nbsp;|&nbsp;";
                    html += "<a onclick=removeBuilding('release_table',"+ value +")>删除</a>&nbsp;|&nbsp;";
                    html += "<a href='/se/estate/building/quotedprice/edit/" + value + "/'>设置出价</a>&nbsp;|&nbsp;";
                }else{
                    html += "<a href='/se/estate/building/" + value + "/?isView=true'>详情</a>&nbsp;|&nbsp;";
                    html += "<a onclick=contract('release_table',"+ value +")>升级</a>&nbsp;|&nbsp;";
                    html += "<a onclick=removeBuilding('release_table',"+ value +")>删除</a>&nbsp;|&nbsp;";
                    html += "<a href='/se/estate/building/quotedprice/edit/" + value + "/'>设置出价</a>&nbsp;|&nbsp;";
                }
                return html;
            }}
        ]],

        loadFilter : loadFilterRelease,

        onAfterSelectPage:onAfterSelectPageRelease
    });
}

function onAfterSelectPageRelease() {
    var pageOpt = $('#release_table').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "releaseStatus" : 'RELEASE',
        "contractType" : $("#contractType :selected").val(),
        "canton" : $("#canton :selected").val(),
        "structure" : $("#structure :selected").val(),
        "sale" : $("#sale :selected").val(),
        "name" : $("#release_name").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}

function loadFilterRelease(json) {
    var data = {};
    if(!json.buildings){
        data.rows = [];
    } else {
        data.rows = json.buildings;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}

//--------------------已发布楼盘列表-----------end--------------//

/**
 * tabs标签切换数据
 * @param title
 * @param index
 */
function selectTab(title,index){
    if(index == 0){
        loadReleaseList();
    }else{
        loadUnreleaseList();
    }
}

//-------------------操作方法-------start-----------//
/**
 * 发布楼盘
 */
function release(obj,id){
    var url = "/se/estate/building/release/" + id +"/?_method=PUT";
    $.post(url, function(data){
        $("#" + obj).datagrid('reload');
        alert("Data Loaded: " + data.msg);
    });

}

/**
 * 删除楼盘
 * @param id
 */
function removeBuilding(obj,id){
    var url = "/se/estate/building/" + id +"/?_method=DELETE";
    $.messager.confirm('Confirm', '删除该楼盘记录后，系统将丢失这条信息！确定删除？', function(r){
        if (r){
            $.post(url, function(data){
                $("#" + obj).datagrid('reload');
            });
        }
    });
}

/**
 * 将普通楼盘升级为合作楼盘
 */
function contract(obj,id){

    var url = "/se/estate/building/contract/" + id +"/?_method=PUT";
    $.post(url, function(data){
        alert(data.msg);
        if(data.success){
            $("#" + obj).datagrid('reload');
        }
    });

}

/**
 * 解除合作
 */
function removeContract(obj,id){
    var url = "/se/estate/building/contract/" + id +"/?_method=DELETE";

    $.messager.confirm('Confirm', '该楼盘被停止合作后将被转为普通楼盘，确定停止？', function(r){
        if (r){
            $.post(url, function(data){
                alert(data.msg);
                if(data.success){
                    $("#" + obj).datagrid('reload');
                }
            });
        }
    });
}


/**
 * 开关购房币业务
 * @param status
 */
function switchRedTask(status,id){

    var status = $("input[name='redTask']:checked").val();
    var id = $("#buildingId").val();
    var url = "/se/estate/building/redTask/" + id +"/?status=" + status;
    $.post(url, function(data){
        alert(data.msg);
        $("#dd").dialog("close");
    });
}
//-------------------操作方法-------end-----------//

function configAcount(buildingId){
    $("#buildingId").val(buildingId);
    $("#dd").dialog('open');

    $.get("/se/estate/building/redTask/" + buildingId + "/", function(data){
        $("input[name='redTask'][value='" + data + "']").attr("checked","checked");
    });
}

function invodDialog(){

    $("#dd").dialog({
        title: "账户设置",
        modal: true,
        closed: true,
        width:200,
        height:150,
        buttons:[{
            text:'保存',
            handler:switchRedTask
        },{
            text:'取消',
            handler:function(){
                $("#dd").dialog("close");
            }
        }]
    });
}


