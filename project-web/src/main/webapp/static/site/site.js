function initPage(){
    var form = $.parser.parse($("#form_site"));
    var operateType = $("#operatorType").val();
    var id = $("#id").val();
    initThemeList();
    if(!id || !operateType){
        return;
    }
    if("edit" == operateType){
        load(id,form);
    }
}

function initThemeList(){
    var themeSelect = $("#themeSelect");
    var radioDomHtml = '<input type="radio" name="theme" checked="checked">';
    var colorDomHtml = '<div class="colorSelect"></div>';

    $.getJSON("/json/getEnum.htm?className=com.naryou.platform.column.domain.PageThemeType", function(json){

        $.each(json,function(index,value){
            var radioDom = $(radioDomHtml);
            var colorDom = $(colorDomHtml);
            radioDom.val(value.value);
            colorDom.css("background-color", "#" + value.rgb);
            var themeItemDom = $("<li>");
            radioDom.appendTo(themeItemDom);
            colorDom.appendTo(themeItemDom);

            themeItemDom.appendTo(themeSelect);
        });

    });

}

function searchMerchant(){

    $('#merchantName').combogrid('showPanel');
}

/**
 * 载入商户列表数据
 */
function loadList(){
    $('#siteTable').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/se/site/site.htm',
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        columns:[[
            {field:'merchantName',align:'center',title:'商家名称'},
            {field:'merchantIndustry',align:'center',title:'所在行业'},
            {field:'template',align:'center',title:'模板编号',width:$(this).width() * 0.38},
            {field:'createDate',align:'center',title:'开通时间',width:$(this).width() * 0.4},
            {field:'status',align:'center',title:'状态',width:$(this).width() * 0.38},
            {field:'secondDomain',align:'center',title:'网站域名',width:$(this).width() * 0.38},
//            {field:'code',align:'center',title:'官方二维码',width:$(this).width() * 0.38,formatter:function(value,row,index){
//                 var html = "<a href='/create_qrcode?download=true&fillLog=true&content=" + row.home +"&downloadFileName=" + row.merchantName + "'>下载二维码</a>";
//                return html;
//            }},
            {field:'id',align:'center',title:'操作',width:$(this).width() * 0.38,formatter:function(value,row,index){
                 var html = "";
                html += "<a href='/se/site/save.htm?id=" + row.id +"'>编辑</a>";
                if(row.stautsValues == 1){
                    html += "&nbsp;<a onclick='offShelfSite(" + row.id + ")'>停用</a>";
                }else{
                    html += "&nbsp;<a onclick='removeSite(" + row.id + ")'>删除</a>";
                    html += "&nbsp;<a onclick='enableSite(" + row.id + ")'>启用</a>";
                }
                return html;
            }}
        ]],

        loadFilter : loadFilter,


        onAfterSelectPage:onAfterSelectPage
    });
}

var shortPattern = "";

var isRemoteLoad = true;

var jsonData = {
    merchants:[]
};

function filterLocalData(pattern){
    var newData = {merchants:[]};

    $.each( jsonData.merchants, function(index, item){
        if(item.name.toLowerCase().indexOf(pattern.toLowerCase()) != -1){
            newData.merchants.push(item);
        }
    });
    return newData;
}

function merchantLoadSuccess(data){
    if(isRemoteLoad){
        jsonData.merchants = data.rows;
    }

}

function beforeLoadfunction(param){
    var pattern = $('#merchantName').combogrid('getText');
    var _datagrid = $('#merchantName').combogrid('grid');
    if(!pattern){
        return false;
    }
    //如果新模式包含最短模式，则在本地查找
    if(shortPattern){
        if(pattern.indexOf(shortPattern) != -1){
            isRemoteLoad = false;
            _datagrid.datagrid('loadData',filterLocalData(pattern));
            return false;
        }else{
            shortPattern = pattern;
        }
    }else{
         shortPattern = pattern;

    }
    isRemoteLoad = true;
    return true;
}

function offShelfSite(id){
    $.getJSON("/se/site/offShelf.htm?id=" + id , function(data){
        if(data.success){
            $('#siteTable').datagrid('reload');
        }else{
            $.messager.alert("错误",data.msg,"error");
        }
    });
}

function enableSite(id){
    $.getJSON("/se/site/enable.htm?id=" + id , function(data){
        if(data.success){
            $('#siteTable').datagrid('reload');
        }else{
            $.messager.alert("错误",data.msg,"error");
        }
    });
}

function removeSite(id){
    $.post("/se/site/" + id + "/?_method=DELETE" , function(data){
        if(data.success){
            $('#siteTable').datagrid('reload');
        }else{
            $.messager.alert("错误",data.msg,"error");
        }
    },"json");
}

/**
 * 智能提示商户列表
 */
function merchantTips(){
    $('#merchantName').combogrid({
        required: true,
        width:500,
        hasDownArrow:false,
        pagination:false,
        idField:'id',
        textField:'name',
        method:'get',
        delay:300,//用户最后一次输入后，进行请求的时延
        mode : 'remote',
        fitColumns:true,
        url:'/se/merchant/merchant.htm',

        columns:[[
            {field:'name',align:'center',title:'商家名称',width:66},
            {field:'id',hidden:true,title:'商家名称',width:66},
            {field:'address',align:'center',title:'详细地址',width:66},
            {field:'industry',align:'center',title:'所属行业',width:66}
        ]],
        onBeforeLoad : beforeLoadfunction,
        onLoadSuccess : merchantLoadSuccess,
        loadFilter: merchantLoadFilter,
        onHidePanel : setDomain
    });

}

function tipLoad(param){
    var text = $('#merchantName').combogrid('getText');
    var value = $('#merchantName').combogrid('getValue');

    if(text || value ||text == "" || value == ""){
        return false;
    }

    return true;

}


function setDomain(){
    var merchantName = $("#merchantName").combogrid('getText');
    $.getJSON("/se/site/domain.htm?merchantName=" + merchantName, function(json){
        if(json.success){
            $("#secondDomain").val(json.msg);
        }
    });
}

function bindSaveBtn(){
    var form = $("#form_site");
    $.parser.parse(form);
    var saveBtn = $("#btn_submit");
    saveBtn.click(function(){
        submitData(form,"/se/site/save.htm?_method=PUT");
    });
}

function merchantLoadFilter(json) {
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

        }
        data.rows = json.merchants;
    }
    return data;
}


function onAfterSelectPage() {
    var pageOpt = $('#siteTable').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "status" : $("#status :selected").val(),
        "merchantName" : $("#name").val(),
        "template" : $("#template :selected").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}

function loadFilter(json) {
    var data = {};
    if(!json.sites){
        data.rows = [];
    } else {
        for(var i in json.sites){
            if(json.sites[i].merchant.industryType){
                json.sites[i].merchantIndustry = json.sites[i].merchant.industryType.desc;
            }

            json.sites[i].merchantName = json.sites[i].merchant.name;
            json.sites[i].template = json.sites[i].template.name;
            json.sites[i].stautsValues = json.sites[i].status.value;
            json.sites[i].status = json.sites[i].status.desc;


        }
        data.rows = json.sites;
    }
    if(json.pagination){
        data.total = json.pagination.count;
        data.pagination = json.pagination;
    }

    return data;
}




function load(id,form){
    $.getJSON("/se/site/" + id + "/", function(json){
        if(json){
            /**
             * 这三局顺序很重要，要是吧editable：false放在setValue后面，
             * 会导致该combogrid没有值
             */
            $("#merchantName").combogrid({editable:false});
            $("#merchantName").combogrid('setValue', json.merchant.id);
            $("#merchantName").combogrid('setText', json.merchant.name);


            $("#secondDomain").val(json.secondDomain);
            setTimeout(function(){
                $("#cc1").combobox('setValue', json.template.id);
            },100);


            $("input[name='theme']").each(function(){
                if($(this).val() == json.theme.value){
                    $(this).attr("checked","checked");
                }
            });
        }
    });
}



function submitData(form,url){
    form.form("submit",{
        url:url,
//        onSubmit:function() {
//            var ok = $(this).form('validate');
//            if(!ok) return false;
//        },
        success:function(text){
            var data = parserToJson(text);
            if(data.success) {
                window.location.href = "/se/site/";
            }
            else {
                $.messager.alert("错误",data.msg,"error");
            }
        }
    });
}



