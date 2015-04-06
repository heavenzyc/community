/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-9-18
 * Time: 下午1:36
 */

$(function(){
    $("#questionBtn").bind("click", invokeEnable);
    $(".deleteAnswerItem").bind("click", deleteAnswerItem);
    $(".invokeAnswerItem").bind("click", invokeAnswerItem);
});

function invokeEnable(){
    var id = $("#questionId").val();
    var enabled = $(this).attr("data-options");
    if(enabled == "false"){
        enableQuestionItem(id);
    }
    else{
        disableQuestionItem(id);
    }
}

function invokeAnswerItem(){
    var $this = $(this);
    var id = $this.attr("data-options");
    var enabled = $this.attr("enabled");
    if(enabled == "false"){
        enableAnswerItem(id , $this);
    }
    else{
        disableAnswerItem(id, $this);
    }
}

function deleteQuestionItem(id){
    if(!confirm("确定删除吗？")){
        return;
    }
    $.ajax({
        url:"/se/estate/question/" + id + "/delete.htm",
        type:"POST",
        data:{"_method": "delete"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                window.location.href="/se/estate/question/";
            }
        }
    });
}

function deleteAnswerItem(){
    if(!confirm("确定删除吗？")){
        return;
    }
    var $this = $(this);
    var id = $this.attr("data-options");
    $.ajax({
        url:"/se/estate/question/answer/" + id + "/delete.htm",
        type:"POST",
        data:{"_method": "delete"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $this.parents(".item").remove();
            }
        }
    });
}

function enableQuestionItem(id){
    $.ajax({
        url:"/se/estate/question/" + id + "/enable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                var $btn =  $("#questionBtn");
                $btn.attr("data-options", "true");
                $btn.attr("title", "屏蔽");
                $btn.text("屏蔽");
            }
        }
    });
}

function disableQuestionItem(id){
    $.ajax({
        url:"/se/estate/question/" + id + "/disable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                var $btn =  $("#questionBtn");
                $btn.attr("data-options", "false");
                $btn.attr("title", "取消屏蔽");
                $btn.text("取消屏蔽");
            }
        }
    });
}

function enableAnswerItem(id, $this){
    $.ajax({
        url:"/se/estate/question/answer/" + id + "/enable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $this.attr("enabled", "true");
                $this.attr("title", "屏蔽");
                $this.text("屏蔽");
                /*$this.parents(".reply_list").find(".answerCount").each(function(){
                    var $text = $(this);
                    var val = $text.text();
                    val = parseInt(val);
                    val = val + 1 ;
                    $text.text(val);
                });*/
                enableInvoke($this, "add");
            }
        }
    });
}

function disableAnswerItem(id, $this){
    $.ajax({
        url:"/se/estate/question/answer/" + id + "/disable.htm",
        type:"POST",
        data:{"_method": "PUT"},
        contentType: "application/x-www-form-urlencoded; charset=utf-8",
        dataType:"json",
        cache:false,
        success:function(data){
            if(data.success){
                $this.attr("enabled", "false");
                $this.attr("title", "取消屏蔽");
                $this.text("取消屏蔽");

                /*$this.parents(".reply_list").find(".answerCount").each(function(){
                    var $text = $(this);
                    var val = $text.text();
                    val = parseInt(val);
                    val = val - 1 ;
                    $text.text(val);
                });*/

                enableInvoke($this, "sub");
            }
        }
    });
}

function enableInvoke($this, invokeType){
    var type = $this.attr("disableType");
    if(type == "item"){
        $this.parents(".reply_list").find(".answerCount").each(function(){
            var $text = $(this);
            var val = $text.text();
            val = parseInt(val);
            if(invokeType == "sub"){
                val = val - 1 ;
            }
            else if(invokeType == "add"){
                val = val + 1;
            }
            $text.text(val);
        });

    }
    else if(type = "root"){
        $this.parents(".rootList").find(".rootAnswerCount").each(function(){
            var $text = $(this);
            var val = $text.text();
            val = parseInt(val);
            if(invokeType == "sub"){
                val = val - 1 ;
            }
            else if(invokeType == "add"){
                val = val + 1;
            }
            $text.text(val);
        });
    }

}