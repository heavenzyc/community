
var uploadRecommendPic ;
/**
 * 菜品分类列表
 */
function searchRecommends(){
    $("#recommend_table").datagrid({
        nowrap: true,
        striped: true,
        fitColumns:true,
        toolbar:"#recommend_toolbar",
        url: "/se/estate/recommend/list.htm",
        method:'get',
        pagination:true,
        singleSelect:true,
        idField:'id',
      //  rownumbers: true,
     //   scrollbarSize:0,
    //    rownumbers: true,
        columns:[[
            {field:'id',title:'编号',width:$(this).width() * 0.1, align: 'center',hidden:true},
            {field:'name',title:'楼盘名称',width:$(this).width() * 0.1, align: 'center'},
            {field:'canton',title:'区域',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(row.position.canton != null){
                    return row.position.canton.tagName;
                }
            }},
            {field:'type',title:'类型',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                return row.managerType.showvalue;
            }},
            {field:'price',title:'均价',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                if(value == null){
                    return "暂无报价";
                }else return value;

            }},
            {field:'maxUseMoney',title:'每套房可用购房币',width:$(this).width() * 0.1, align: 'center'},
            {field:'updateDate',title:'发布时间',width:$(this).width() * 0.1, align: 'center'},
            {field:'sort',title:'排序',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                var html = '';
                html += '<a href="###" class="link-update" onclick="move(event,this,true,'+row.id+')">↑</a>';
                html += '<a href="###" class="link-update" onclick="move(event,this,false,'+row.id+')">↓</a>';
                return html;
            }},
            {field:'realAdPic',title:'封面图',width:$(this).width() * 0.1, align: 'center',formatter:function(value,row,index){
                var html1 = '<a class="upload" href="javascript:void(0);" onclick="uploadPic('+row.id+')" title="上传封面图">上传封面图</a>';
                if(row.realAdPic == ""){
                    return html1;
                }
                var html2 = '<a style="display:none;" class="upload" href="javascript:;" title="上传封面图">上传封面图</a>'+
                    '<div class="img">'+
                    '<img src="'+row.realAdPic+'" width="44" height="60"/>'+
                    '<a class="del" href="javascript:void(0);" onclick="deletePic('+row.id+')">删除</a>'+
                    '</div>';
                return html2;
            }},
            {field:'id_n',title:'操作',width:$(this).width() * 0.1,formatter:function(value, row, index){
                var html = '';
                if(row.recommand != null){
                    html += '<a href="###" class="link-update" onclick="cancelRecommend(' + row.id + ', \'' + row.recommand.desc + '\',\''+row.realAdPic+'\')">'+row.recommand.desc+'</a>';
                }
                return html;
            }, align: 'center'}
        ]],
        loadFilter : function(json) {
            var data = {};
            if(!json.building){
                data.rows = [];
            } else {
                data.rows = json.building;
            }
            data.total = json.pagination.count;
            data.pagination = json.pagination;
            return data;
        },
        onAfterSelectPage:function() {
            var pageOpt = $('#recommend_table').datagrid('getPager').pagination("options");
            $(this).datagrid("options").queryParams={
                "ts":new Date().getTime(),
                "currentPage":pageOpt.pageNumber,
                "pageSize":pageOpt.pageSize
            };
        }
    });


    uploadRecommendPic = $('#uploadRecommendPic');
    uploadRecommendPic.show();
    uploadRecommendPic.dialog({
        title: '上传图片',
        width: 400,
        height: 400,
        closed: true,
        cache: false,
        modal: true,
        closed: true,
        buttons:[{
            text:'保存',
                 handler:uploadImg
        },{
            text:'取消',
            handler:function(){
                uploadRecommendPic.dialog('close');
            }
        }]
    });
}

function uploadImg(){
    var path = $(".path").val();
    var url = "/se/estate/recommend/uploadImg/"+recommendId+"/";
    $.ajax({
        url: url,
        async: false,
        type:'get',
        dataType:'json',
        data : {path:path},
        success:function(data) {
            if(data.success) {
                uploadRecommendPic.dialog('close');
            //    $.messager.alert('提示',"操作成功！");
                $('#recommend_table').datagrid("reload");
            }
            else {
                uploadRecommendPic.dialog('close');
                $.messager.alert('警告',data.msg);
            }
        }
    });
}
/**
 * 列表移动
 * @param e
 * @param target
 * @param isUp
 */
function move(e, target, isUp, id) {
    var $view = $(target).closest('div.datagrid-view');
    var index = $(target).closest('tr.datagrid-row').attr('datagrid-row-index');
    var $row = $view.find('tr[datagrid-row-index=' + index + ']');
    if (isUp) {
        $row.each(function() {
            var preColCount = $(this).prev().length; //当前行上面的行数
            $(this).prev().before($(this));
            var preid = $(this).next('tr').find('td:eq(0)').text();
            if(preColCount != 0){
                $.ajax({
                    type:"post",
                    url:"/se/estate/recommend/sort.htm",
                    data:{id: id, sort:id, preId:preid},
                    dataType:"json"
                });
            }
        });
    }
    if(!isUp) {
        $row.each(function() {
            var nextCount = $(this).before().next().length;
            $(this).before($(this).next());
            var nextid = $(this).prev('tr').find('td:eq(0)').text();
            if(nextCount != 0){
                $.ajax({
                    type:"post",
                    url:"/se/estate/recommend/sort.htm",
                    data:{id: id, sort:id, preId:nextid},
                    dataType:"json"
                });
            }
        });
    }
    $row.removeClass('datagrid-row-over');
    e.stopPropagation();
}

/**
 * 推荐/取消推荐
 */
function cancelRecommend(id,desc,pic) {
    if(desc == "推荐"){
        if(pic == null || pic == ""){
            $.messager.alert("提示","未上传封面图，请上传封面图后操作");
            return;
        }
    }

    var url = "/se/estate/recommend/cancel/"+id+"/";
    $.ajax({
        url: url,
        async: false,
        type:'get',
        dataType:'json',
        success:function(data) {
            if(data.success) {
                $.messager.alert('提示',"操作成功！");
                $('#recommend_table').datagrid("reload");
            }
            else {
                $.messager.alert('警告',data.msg);
            }
        }
    });
}


var recommendId;
function uploadPic(id) {
    recommendId = id;
    $("#brd_pic").attr("src","");
    uploadRecommendPic.dialog('open');
}

function deletePic(id){
    $.messager.confirm("删除","确认删除该图片吗？",function(ok){
        if(ok){
            var url = "/se/estate/recommend/delete_pic/"+id+"/";
            $.ajax({
                url: url,
                async: false,
                type:'get',
                dataType:'json',
                success:function(data) {
                    if(data.success) {
                        //    $('#recommend_table').datagrid("reload");
                        window.location.reload();
                    }
                    else {
                        $.messager.alert('警告',data.msg);
                    }
                }
            });
        }
    });

}

