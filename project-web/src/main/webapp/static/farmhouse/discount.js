
/**
 * 载入限时特价列表数据
 */
function loadDiscounts(){
    $('#discountTable').datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#toolbar",
        url:'/se/discount/discounts.htm',
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
        rownumbers: true,
        columns:[[
            {field:'name',title:'名称',align:'center',width:$(this).width() * 0.4,formatter:function(value,row,index){
                var style = "font-family: 微软雅黑;font-size: 14px;font-weight: normal;font-style: normal;text-decoration: none;color: #666666;"
                var html = "<div style='margin:5px 5px 5px 5px;'>";
                html += "<p style='text-align:left;'><b>" + row.title + "</b></p>"
//                html += "<p style='text-align:left;'><span style='" + style + "'>" + row.content + "</span></p>"
                html += "</div>"
                return html;
            }},
            {field:'startDate',align:'center',title:'开始时间',width:$(this).width() * 0.2},
            {field:'endDate',align:'center',title:'结束时间',width:$(this).width() * 0.2},
            {field:'bookCount',align:'center',title:'可订次数',width:$(this).width() * 0.2},
            {field:'status',align:'center',title:'状态',width:$(this).width() * 0.2,formatter:function(value,row,index){
                return row.status.desc;
            }},
            {field:'operate',align:'center',title:'操作',width:$(this).width() * 0.2,formatter:function(value,row,index){
                var html = "";
                html += "<a class='link-update' href='/se/discount/" + row.id + "/view.htm'>查看</a>";
                if(row.status._name == 'ACTIVE'){
                    html += "<a href='#' class='link-update' onclick='offShelf(" + row.id + ")'>下架</a>";
                }
                return html;
            }}
        ]],

        loadFilter : loadFilter,

        onAfterSelectPage:onAfterSelectPage
    });
}

function onAfterSelectPage() {
    var pageOpt = $('#discountTable').datagrid('getPager').pagination("options");
    $(this).datagrid("options").queryParams={
        "title" : $("#title").val(),
        "status" : $("#status :selected").val(),
        "endDate" : $("#endDate").datebox('getValue'),
        "startDate" : $("#startDate").datebox('getValue'),
        "ts" : new Date().getTime(),
        "currentPage":pageOpt.pageNumber,
        "pageSize":pageOpt.pageSize
    };
}


function loadFilter(json) {
    var data = {};
    if(!json.discounts){
        data.rows = [];
    } else {
        data.rows = json.discounts;
    }
    data.total = json.pagination.count;
    data.pagination = json.pagination;
    return data;
}

function offShelf(id){
    $.post("/se/discount/offShelf/" + id +"/", function(json){
        if(json.success){
            $('#discountTable').datagrid('reload');
        }else{
            $.messager.alert('Warning',json.msg);
        }
    });
}

function loadDiscount(id,form){
    $.getJSON("/se/discount/" + id + "/", function(json){
        form.form('load',json);
        $(form).find("input[name*='path']").each(function(i,obj){
            if(json.discountPictures[i]){
                $(obj).val(json.discountPictures[i].path);
                $(obj).prev("img").attr("src",json.discountPictures[i].picUrlPath);
            }
        });
    });
}

function viewDiscount(id){
    var form = $("#form_discount");
    loadDiscount(id,form);
    form.find("input,textarea").each(function(){
        $(this).attr("disabled","disabled");
    });
}


function submitDiscount(form,url){
    form.form("submit",{
        url:url,
        onSubmit:function() {
            var ok = $(this).form('validate');
            $(form).find("input[name*='pics']").each(function(index, domEle){
                if($(domEle).val() == undefined || $(domEle).val() == ""){
                    $(domEle).attr("disabled","disabled");
                }
            });
            if(!ok) return false;
        },
        success:function(text){
            var data = parserToJson(text);
            if(data.success) {
                window.location.href = "/se/discount/";
            }
            else {
                $("#errorMsg").html(data.msg);
            }
        }
    });
}

