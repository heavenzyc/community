$(function(){

    var options = {
        uploadUrl : "/se/upload/SUBJECT/pic/140/140",  //上传图片的路径
        uploadInputName : 'filedata' //后台接收文件的名称
    };
    uploadDialogTools.initUploadOptions(options);  //初始化上传参数
    uploadDialogTools.init(); //初始化上传图片按钮

    $("#addMerchants").find(".merchantTable .del").live("click", function(){
        $(this).css("display","none");
        $(this).parents(".merchantTable").fadeOut("fast",function(){
            $(this).remove();
        });

        /* $("#addMerchants").find(".merchantTable").each(function(index, item){
         $(this).find(".index").html(index + 2);
         });*/
        var   _merchantTable= $("#addMerchants").find(".merchantTable");
        var _merchantTablelen=_merchantTable.length;
        for(var i=1;i<_merchantTablelen;i++){
            _merchantTable.eq(i).find('.index').html(i+1);
        }
        merchantCount--;
    });

    $('#form').form({
        url:"/se/subject/add.htm",
        onSubmit: function(){
            return $('#form').form("validate");
        },
        success:function(data){
            alert(data);
        }
    });

    initSelectMerchant();

    resetContentHeight();
    
    $('#txt_subjectCreateDate').siblings('span').find('.validatebox-text').removeClass("validatebox-invalid");
});

var merchantCount = 1;

function addMerchant(){
    merchantCount++;
    var itemHtml = $("#merchantTemplate").html();
    var item = $(itemHtml);
    //$("#addMerchants").append(item);
    item.find(".index").html(merchantCount);
    item.appendTo("#addMerchants");
    $.parser.parse("#addMerchants");
  //  item.find(":text:first").focus();
    item.hover(function(){
        $(this).addClass("merchantTable-active");
    },function(){
        $(this).removeClass("merchantTable-active");
    });

    resetContentHeight();
}

/**
 * 重新设置内容模块的高度
 */
function resetContentHeight(){
    var height = $(".contenter").height();

    resetLayoutHeight(height + 120);
}

function resetLayoutHeight(height){
    var layoutDom = $("#easyuiLayout");
    layoutDom.height(height);
    layoutDom.layout("resize");
}

/**
 * 初始化选择商家的对话框
 */
function initSelectMerchant(){
    var dialog = $('#merchantDialog');
    dialog.append($("#selectMerchantDialog").html());
    dialog.dialog({
        title: "选择商家",
        width: 600,
        height: 480,
        cache: false,
        modal: true,
        buttons:[{
            text:'确定',
            handler:function(){
                var dialog = $("#merchantDialog");
                if(currentItem){
                    var name = dialog.find("#selectedMerchantName").text();
                    var value = dialog.find("#selectedMerchantId").val();

                    currentItem.find(".merchantName").val(name).removeClass("validatebox-invalid");
                    currentItem.find("input[name='merchant']").val(value);
                   /* console.info(currentItem.find("input[name='merchant']").val());
                    console.info(currentItem.find("input[name='merchant']"));*/
                }
                dialog.dialog("close");
            }
        },{
            text:'取消',
            handler:function(){
                $("#merchantDialog").dialog("close");
            }
        }]
    });
    dialog.dialog("close");

    $("#merchantDataGrid").datagrid({
        url: "/se/merchant/merchant/merchantList/",
        idField: "id",
        nowrap: false,
        fitColumns:true,
        method: "GET",
        singleSelect: true,
        columns:[[
            {field:'id',title:'商家名称', align: 'center', checkbox: true},
            {field:'name',title:'商家名称',width: $(this).width() * 0.35, align: 'center'},
            {field:'address',title:'商家地址',width: $(this).width() * 0.65, align: 'center'}
        ]],
        loadFilter: function(json){
            var data = {};
            if(json.info){
            	data.rows = json.info;
            }else{
            	data.rows = {};
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $("#merchantDataGrid").datagrid('getPager').pagination("options");
            var pageParams = {
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };

            $(this).datagrid("options").queryParams= $.extend($(this).datagrid("options").queryParams, pageParams);
        },
        onCheck: function(rowIndex, rowData){
            $("#selectedMerchantName").html(rowData.name);
            $("#selectedMerchantId").val(rowData.id);
        },
        pagination:true,
        toolbar: "#toolbar"
    });
}

/**
 * 选择商家
 * @param item
 */
//用于保存点击选择商家的源元素信息
var currentItem;
function selectMerchant(item){
    var $root =  $(item).parents("td");
    currentItem = $root;
    var name = $root.find(".merchantName").val();
    var value = $root.find("input[name='merchant']").val();

    var dialog = $("#merchantDialog");

    dialog.find("#selectedMerchantName").html(name);
    dialog.find("#selectedMerchantId").val(value);

    dialog.dialog("open");
    dialog.dialog("center");
    $("#merchantDataGrid").datagrid("uncheckAll");
}