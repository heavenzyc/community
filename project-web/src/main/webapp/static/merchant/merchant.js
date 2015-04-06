function dispatchOperat(){
    var operatType = $("#operatType").val();
    var id = $("#id").val();
    if(!id || !operatType){
        addMerchant();
        return;
    }


    if("edit" == operatType){
        editMerchant(id);
    }
    if("view" == operatType){
        viewMerchant(id);
    }
}


/**
 * 载入商户列表数据
 */
function loadList(){
    $('#merchantTable').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/se/merchant/merchant.htm',
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        nowrap:false,
        columns:[[
//            {field:'',title:'人员名称',align:'center',width:$(this).width() * 0.1},
            {field:'status',align:'center',title:'数据状态',width:$(this).width() * 0.1,styler:function(index,row){
               if(row.statusValue == 7 || row.statusValue == 9){
                        return 'color:#03B30C;';
               }
                if(row.statusValue == 3 || row.statusValue == 8){
                    return 'color:#276ED9;';
                }
                if(row.statusValue == 10 || row.statusValue == 2){
                    return 'color:#B5110B;';
                }
            }},

            {field:'name',align:'center',title:'商家名称',width:$(this).width() * 0.2},
            {field:'address',align:'center',title:'详细地址',width:$(this).width() * 0.2},
            {field:'industry',align:'center',title:'所属行业',width:$(this).width() * 0.1},
            {field:'updateDate',align:'center',title:'更新日期',width:$(this).width() * 0.2},
            {field:'operate',align:'center',title:'操作',width:$(this).width() * 0.1,formatter:function(value,row,index){
                var html = "<a href='/se/merchant/" + row.id + "/save.htm' >编辑附加信息</a>";
                return html;
            }},
            {field:'view',align:'center',title:'查看',width:$(this).width() * 0.1,formatter:function(value,row,index){
                 var html = "<a href='/se/merchant/" + row.id + "/view.htm' >查看</a>";
                return html;
            }}

        ]],

        loadFilter : loadFilter,

        onAfterSelectPage:onAfterSelectPage
    });
}

function onAfterSelectPage() {
    var pageOpt = $('#merchantTable').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "name" : $("#name").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}

function loadFilter(json) {
    var data = {};
    if(!json.merchants){
        data.rows = [];
    } else {
        for(var i in json.merchants){
            if(json.merchants[i].industryTypes && json.merchants[i].industryTypes.length > 0){
                var industry = "[("
                for(var index in json.merchants[i].industryTypes){
                    industry += "," + json.merchants[i].industryTypes[index].desc;
                }
                industry += ")]";
                json.merchants[i].industry = industry.replace(",","");
            }

            if(json.merchants[i].dataStatus){
                json.merchants[i].status = json.merchants[i].dataStatus.desc;
                json.merchants[i].statusValue = json.merchants[i].dataStatus.value;
            }
        }
        data.rows = json.merchants;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}

function appendTag(tagName){
    var style = randomcolor();
//    var index = $("#impressTag").find(".tag").length;
    var impresstagHtml = "<span class='tag' onclick='deleteTag(this)' style='font-size:15px;background:" + style  + ";' >" + tagName + "</span>";
    impresstagHtml += "<input type='hidden' name='tags' value='" + tagName + "'/>";
    $("#impressTag").append(impresstagHtml);
}
function deleteTag(obj){
    $(obj).next().remove();
    $(obj).remove();

}


function load(id,form){
    $.getJSON("/se/merchant/" + id + "/", function(json){
        if(json.merchantExtendInfo){
            $("#preConsume").val(json.merchantExtendInfo.preConsume);
            $("#receivesCapacity").val(json.merchantExtendInfo.receivesCapacity);
            $("#scope").val(json.merchantExtendInfo.scope);
            $("#unit").val(json.merchantExtendInfo.unit);
            $("#extend_id").val(json.merchantExtendInfo.id);
            $("#merchantName").val(json.name);
            $("#gpsX").val(json.gps_x);
            $("#gpsY").val(json.gps_y);
            $("#telephone").val(json.telephone);
            $("#address").val(json.address);

            initMap(json.gps_x,json.gps_y);

        }



        var tags = json.merchantImpressTags;
        if(tags && tags.length > 0){
             for(var i in tags){
                 appendTag(tags[i].tag.name);
             }
        }
    });
}

function initMap(x,y){
    $("#naryoumap").naryouMap({
        click: setLngAndLat,
        center:{
            lng: x,
            lat: y
        }
    });
}


function setLngAndLat(e){
    $("#gpsX").val(e.point.lng);
    $("#gpsY").val(e.point.lat);
}

function viewMerchant(id){
        var form = $("#form_detail");
        load(id,form);
        form.find("input,textarea").each(function(){
              $(this).attr("disabled","disabled");
        });
        $("#saveBtn").hide();
}

function invodDialog(obj,title){
    $(obj).dialog({
        title: title,
        width: 600,
        modal: true,
        height: 300,
        onClose:function() {
            $(this).dialog("destroy");
        }
    });
}


function editMerchant(id){
        var form = $("#form_detail");
        load(id,form);
        $.parser.parse(form);
        var saveBtn = $("#saveBtn");
        saveBtn.click(function(){
            submitData(form,"/se/merchant/save.htm?_method=PUT");
        });
}


function submitData(form,url){
    form.form("submit",{
        url:url,
        onSubmit:function() {
            var ok = $(this).form('validate');
            if(!ok) return false;
        },
        success:function(text){
            var data = parserToJson(text);
            if(data.success) {
                window.location.href = "/se/merchant/";
            }
            else {
                $("#errorMsg").html(data.msg);
            }
        }
    });
}

function addTag(){
    var dialogDom = $("<div id='addTagDialog'></div>");
    dialogDom.load("/local/dialog/merchant/add_tag.html",function(){

        $(this).dialog({
            title: "添加标签",
            width: 200,
            modal: true,
            shadow: true,
            closed: false,
            height: 400,
            onClose:function() {
                $(this).dialog("destroy");
            },
            buttons:[{
                text:'确定',
                handler:function(){
                    var rows = $(dialogDom).find("#tagTable").datagrid('getSelections');
                    for (var i in rows){
                        appendTag(rows[i].name);
                    }
                    $(dialogDom).dialog("destroy");
                }
            },{
                text:'取消',
                handler:function(){
                    $(dialogDom).dialog("destroy");
                }
            }]
        });

        $(this).find("#tagTable").datagrid({
            url:'/se/merchant/tags/' + $("#id").val() + "/",
            method:'GET',
            fitColumns : true,
            columns:[[
                {field:'id',checkbox:true},
                {field:'name',title:'标签名'}
            ]],
            loadFilter:function(json){
                var data = {};
                  if(!json.tags){
                      data.rows = [];
                  } else{
                      data.rows = json.tags;
                  }
                return data;
            }
        });
    });
}

function randomcolor() {
    var colorvalue = ["0", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"];
    var colorprefix = "#";
    var index;
    for (var i = 0; i < 6; i++) {
        index = Math.round(Math.random() * 14);
        colorprefix += colorvalue[index];
    }
    return colorprefix;
}


