
/**
 * 载入商户列表数据
 */
function loadList(){
    $('#listTable').treegrid({
        nowrap: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/se/template/template.htm',
        method:'post',
        pagination:true,
        singleSelect:true,
        idField:'id',
        treeField: 'name',
        rownumbers: true,
        columns:[[
            {field:'name',align:'left',title:'模板名称'},
            {field:'templatePath',align:'center',title:'模板路径'},
            {field:'crateDate',align:'center',title:'创建日期',width:$(this).width() * 0.38},
            {field:'updateDate',align:'center',title:'更新日期',width:$(this).width() * 0.4},
            {field:'enable',align:'center',title:'是否可用',width:$(this).width() * 0.38,formatter:function(value,row,index){
                if(row.isParente){
                    return  row.enable ? "可用" : "已停用";
                }
            }},
            {field:'id',align:'center',title:'操作',width:$(this).width() * 0.38,formatter:function(value,row,index){
                var html = "";
                if(row.isParente){
                    if(row.enable){
                        html += "&nbsp;<a onclick='unableTemplate(" + row.id + ")'>停用</a>";
                    }else{
                        html += "&nbsp;<a onclick='enableTemplate(" + row.id + ")'>启用</a>";
                    }
                }

                return html;
            }}
        ]],

        loadFilter : loadFilter,
        onClickCell : function(){},


        onAfterSelectPage:onAfterSelectPage
    });

}

function unableTemplate(id){
    changeStatus(id,false);

}

function changeStatus(id,enable){
    $.post('/se/template/' + id + '/?_method=PUT&enable=' + enable,function(json){
        if(json.success){
            $("#listTable").treegrid('reload');

        }else{
            $.messager.alert('错误',json.msg,'error');
        }
    },"json");
}

function enableTemplate(id){
    changeStatus(id,true);
}



function onAfterSelectPage() {
    var pageOpt = $('#listTable').treegrid('getPager').pagination("options");
    $(this).treegrid("options").queryParams={
        "name" : $("#name").val(),
        "enable" : $("#status :selected").val(),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}

function loadFilter(json) {
    var data = {};
    if(!json.templates){
        data.rows = [];
    } else {
        var identity = 0;
        for(var index in json.templates){
            if(json.templates[index].columns){
                for(var i in json.templates[index].columns){
                    if(!json.templates[index].children){
                        json.templates[index].children = [];
                    }
                    json.templates[index].children[i] = {};
                    json.templates[index].children[i].name = json.templates[index].columns[i].column.name;
                    json.templates[index].children[i].identity = identity++;
                }

            }
            json.templates[index].isParente=true;
        }


        data.rows = json.templates;

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

            $("#secondDomain").val(json.secondDomain);
            $("#cc1").combobox('setValue', json.template.value);


            $("input[name='theme']").each(function(){
                if($(this).val() == json.theme.value){
                    $(this).attr("checked","checked");
                }
            });
        }
    });
}


