var ajaxUpload=function(){
    var opts={
        ID:'#upfile',
        uploadUrl:'up.php',
        upExt:'jpg,jpeg,gif,png',
        uploadInputName:'filedata',
        data:{},
        autoSubmit:true,
        onSelect:null,
        onSubmit:null,
        onComplete:null,
        onProgress:null,
        onError:null,
        multiple:false //多文件，只支持Html5
    };
    opts=$.extend({},opts,arguments[0]||{});
    var _this=this;
    var checkFileExt=function(filename,limitExt){
        if(limitExt==='*'||filename.match(new RegExp('\.('+limitExt.replace(/,/g,'|')+')$','i'))){
            return true;
        }else{
            if(opts.onError!=null){
                opts.onError({
                    type:"ext",
                    message:"上传文件必需为: "+limitExt
                });
            }
            return false;
        }
    };
    this.upload;
    var uploadInit=function(){
        var $btn= typeof opts.ID == "object" ? opts.ID : $(opts.ID);
        var $file=$('<input type="file" style="position:absolute;top:-30px;right:-10px;padding:0;margin:0;height:auto;font-size:460px;cursor: pointer; opacity: 0;" tabindex="-1" '+(opts.multiple?"multiple":"")+'/>');
        $file.css({
            opacity:0
        });
        $btn.css({
            position:'relative',
            overflow:'hidden',
            display:'inline-block'
        }).append($file);
        
        
        if(opts.onSelect!=null){
            $file.on("change",function(){
                opts.onSelect($file[0]);
            });
        }
        
        //开始上传
        _this.upload=function(postdata){
            if(opts.onSubmit!=null&&!opts.onSubmit($file[0])){
                return false;
            }
            startUpload($file[0],postdata||{});
        };
        //自动上传
        if(opts.autoSubmit){
            $file.on("change",function(){
                _this.upload(opts.data);
            });
        }
    };
    var startUpload=function(fromFiles,postdata){
        var onUploadCallback=function(sText,bFinish){
            var data={},bOK=false;
            try{
                data=eval("("+sText+")");
            }catch(ex){};
//                if(data.status==1){
            if(data){
                bOK=true;
            }
            if(bFinish&&bOK){
                
                if(opts.onComplete!=null)opts.onComplete(data);
            }//全部上传完成
            return bOK;
        }
    
        var upload,fileList;
        if(fromFiles.nodeType&&!((fileList=fromFiles.files)&&fileList[0])){
            if(!checkFileExt(fromFiles.value,opts.upExt))return;
            upload=new html4Upload(fromFiles,opts.uploadUrl,postdata,onUploadCallback);
        }else{
            if(!fileList)fileList=fromFiles;//文件列表
            var i,len=fileList.length;
            for(i=0;i<len;i++)if(!checkFileExt(fileList[i].name,opts.upExt))return;
            upload=new html5Upload(fileList,opts.uploadUrl,postdata,onUploadCallback,function(ev){
                if(opts.onProgress!=null)opts.onProgress(ev);
            });
        }
        upload.start();
    };
    var html4Upload=function(fromfile,toUrl,postdata,callback){
        var uid = new Date().getTime(),idIO='jUploadFrame'+uid,_this=this;
        var jIO=$('<iframe name="'+idIO+'" style="display:none"/>').appendTo('body');
        var jForm=$('<form action="'+toUrl+'" target="'+idIO+'" method="post" enctype="multipart/form-data" style="display:none"></form>').appendTo('body');
        var jOldFile = $(fromfile).attr("name",opts.uploadInputName),jNewFile = jOldFile.clone().attr('disabled','true');
        jOldFile.before(jNewFile).appendTo(jForm);
        $.each(postdata,function(name,value){
            $('<input type="hidden" name='+name+' value='+value+' />').appendTo(jForm);
        })
        this.remove=function()
        {
            if(_this!==null)
            {
                jOldFile.removeAttr("name");
                jNewFile.before(jOldFile).remove();
                jIO.remove();
                jForm.remove();
                _this=null;
            }
        }
        this.onLoad=function(){
            var ifmDoc=jIO[0].contentWindow.document,result=$(ifmDoc.body).text();
            ifmDoc.write('');
            _this.remove();
            callback(result,true);
        }
        this.start=function(){
            jForm.submit();
            jIO.load(_this.onLoad);
        }
        return this;
    };
    var html5Upload=function(fromFiles,toUrl,postdata,callback,onProgress){
        var xhr,i=0,count=fromFiles.length,allLoaded=0,allSize=0,_this=this;
        for(var j=0;j<count;j++)allSize+=fromFiles[j].size;
        this.remove=function(){
            if(xhr){
                xhr.abort();
                xhr=null;
            }
        }
        this.uploadNext=function(sText){
            if(sText)//当前文件上传完成
            {
                allLoaded+=fromFiles[i-1].size;
                returnProgress(0);
            }
            if((!sText||(sText&&callback(sText,i===count)===true))&&i<count)postFile(fromFiles[i++],toUrl,_this.uploadNext,function(loaded){
                returnProgress(loaded);
            });
        }
        this.start=function(){
            _this.uploadNext();
        }
        function postFile(fromfile,toUrl,callback,onProgress){
            xhr = new XMLHttpRequest();
            var upload=xhr.upload;
            xhr.onreadystatechange=function(){
                if(xhr.readyState===4)callback(xhr.responseText);
            };
            if(upload)upload.onprogress=function(ev){
                onProgress(ev.loaded);
            };
            else onProgress(-1);//不支持进度
            xhr.open("POST", toUrl,true);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            $.each(postdata,function(name,value){
                fd.append(name,value);
            })
            fd.append(opts.uploadInputName,fromfile);
            xhr.send(fd);
        }
        function returnProgress(loaded){
            if(onProgress)onProgress({
                'loaded':allLoaded+loaded,
                'total':allSize
            });
        }
    };
    uploadInit();
}

/*********************************/

var uploadDialogTools = {};

uploadDialogTools.initFlag = false;
uploadDialogTools.path = {imagePath: "", webPath: ""};
/**
* 上传工具的内置参数信息
*/
uploadDialogTools.options = {
    ID: ".naryou-upload",
    divBase: "_uploadImageDialog",
    divRoot: ".upload-pic-box",
    template: "#uploadTemplate",
    input: "input.path",
    canDelete: "deleteValue",
    dialog:{
        width: 500,
        height: 310
    },
    dialogHtml:[
        '<div class="lightbox uploadImageDialogBase" id="_uploadImageDialogBase">',
        '<div class="in">',
        '   <div class="boxtop clearfix"><strong class="Left">添加图片</strong><a class="Right close closeBtn" href="javascript:void();">X</a></div>',
        '   <div class="cotent">',
        '        <div class="files_nav clearfix">',
        '            <span class="file-box">',
        '                <form action="" method="post" enctype="multipart/form-data">',
        '                    <!--<input type="button" class="btn" value="选择图片"/>--><a class="btn inputUploadBtn">选择图片</a>',
        '                    <input type="file" name="fileField" class="file" id="fileField"/>',
        '                </form>',
        '            </span>',
        '            <span style="display:inline-block;">仅支持JPG、PNG、JPEG、GIF文件</span>',
        '        </div>',
        '        <div class="files_tips message"></div>',
        '        <div class="clearfix previewImgArea">',
        '            <div class="limgbox">',
        '                <div class="tips">预览窗口(图片大小：<span class="widthLabel">0</span>*<span class="heightLabel">0</span>)</div>',
        '                <div class="bigimg cropImgArea" style="height:228px;"><div class="cropImg"><img src="" class="img" /></div></div>',
        '            </div>',
        '            <div class="rimgbox">',
        '                <div class="tips">大尺寸图片效果<!--：(<span class="widthLabel">640</span>*<span class="heightLabel">320</span>)--></div>',
        '                <div style="position:relative;height:96px;padding-bottom:33px"><div class="milimg previewImg"><!--189*96--><img src=""/></div></div>',
        '                <div class="tips">小尺寸图片效果<!--：(<span class="widthLabel">640</span>*<span class="heightLabel">320</span>)--></div>',
        '                <div style="position:relative;height:58px"><div class="smalimg previewImg"><!--114*58--><img src=""/></div></div>',
        '            </div>',
        '        </div>',
        '        <div class="box_bottom">',
        '            <a href="javascript:void();" class="blueBut submitBtn">确认</a>',
        '            <a href="javascript:void();" class="greyBut closeBtn">取消</a>',
        '        </div>',
        '   </div>',
        '    </div>',
        '</div>'
    ].join("")
}

uploadDialogTools.bindOnSelected = function(event){
    uploadDialogTools.onSelected = event;
}

uploadDialogTools.bindOnBeforeShowDialog = function(event){
    uploadDialogTools.onBeforeShowDialog = event;
}

/**
* 上传的默认参数信息
*/
uploadDialogTools.uploadOptions = {
    ID: uploadDialogTools.options.divBase,
    autoSubmit:true,		    //为true时，选择后立即上传
    uploadUrl:"test.action",	//上传地址
    data: {},
    onComplete:function(data){
        if(data.result){
            uploadDialogTools.dialogDiv.find(".message").empty();

            uploadDialogTools.dialogDiv.find(".previewImg").removeClass("preview-img").find("img").removeClass("img");
            uploadDialogTools.path.imagePath = data.imagePath;
            uploadDialogTools.path.webPath = data.webPath;

            var previewImgAreaImg = uploadDialogTools.dialogDiv.find(".previewImgArea").find("img");


            if(uploadDialogTools.options.canCrop){
                previewImgAreaImg.attr("src", data.imagePath);
                if(uploadDialogTools.crop){
                    uploadDialogTools.crop.destroy();
                }
                var $previewImgArea = uploadDialogTools.dialogDiv.find(".previewImgArea");
                $previewImgArea.find("input[name='imgW']").val(data.width);
                $previewImgArea.find("input[name='imgH']").val(data.height);
                var $previewImg = uploadDialogTools.dialogDiv.find(".cropImg");
                var $cropImgArea = uploadDialogTools.dialogDiv.find(".cropImgArea");
                $previewImg.find("img").remove();
                var $img = $('<img src=""/>');

                var swidth = parseInt(data.width);
                var sheight = parseInt(data.height);

                var xw = swidth / $cropImgArea.width();
                var xh = sheight / $cropImgArea.height();
                var setWidth = xw > xh ? $cropImgArea.width() : (swidth / xh);
                var setHeight = xh > xw ? $cropImgArea.height() : (sheight / xw);

                $img.css({width: Math.round(setWidth) + "px", height: Math.round(setHeight) + "px"});
                $img.attr("src", data.imagePath);

                $previewImg.append($img);

                //var $img = uploadDialogTools.dialogDiv.find(".previewImgArea .bigimg").find("img");
                //if($img.size() != 1) return;
                var options = uploadDialogTools.options;
                var cropOptions = {};
                cropOptions.aspectRatio =options.cropWidth / options.cropHeight;
                var iwidth = $img.width();
                var iheight = $img.height();
                var cw = options.cropWidth / iwidth;
                var ch = options.cropHeight / iheight;

                //按比例压缩后的裁剪大小
                var width = cw > ch ? iwidth : (options.cropWidth / ch);
                var height = cw < ch ? iheight : (options.cropHeight / cw);

                var minWidth;
                var minHeight;
                //只按比例裁剪
                if(options.cropRatio){
                    minWidth = width;
                    minHeight = height
                    //cropOptions.select = [0, 0, width, height];
                }
                else{
                    //图片真实大小

                    minWidth = Math.round((iwidth * options.cropWidth) / swidth);
                    minHeight = Math.round((iheight * options.cropHeight) / sheight);
                    //cropOptions.select = [0, 0, minWidth, minHeight];
                    cropOptions.minSize =  [minWidth, minHeight];
                }

                $previewImgArea.find("input[name='cropX']").val(0);
                $previewImgArea.find("input[name='cropY']").val(0);
                $previewImgArea.find("input[name='cropW']").val(minWidth);
                $previewImgArea.find("input[name='cropH']").val(minHeight);

                //绑定裁剪动作事件
                cropOptions.onchange = uploadDialogTools.showPreview;
                cropOptions.onSelect = uploadDialogTools.showPreview;

                /*$img.ready(function(){
                    uploadDialogTools.crop = $.Jcrop($img, cropOptions);
                });*/
               // setTimeout('uploadDialogTools.crop = $.Jcrop($img, cropOptions)', 2000);
                uploadDialogTools.crop = $.Jcrop($img, cropOptions);
                uploadDialogTools.crop.animateTo([0, 0, minWidth, minHeight]);
                uploadDialogTools.initCrop = true;

                var previewImgs = uploadDialogTools.dialogDiv.find(".previewImg");
                previewImgs.each(function(){
                    var $previewImg = $(this);
                    var mx = $previewImg.width() / minWidth;
                    var my = $previewImg.height() / minHeight;
                    $previewImg.find("img").css({
                        width: Math.round(mx * iwidth) + "px",
                        height: Math.round(my * iheight) + "px"
                    });
                })
            }
            else{
                previewImgAreaImg.removeAttr("style").removeClass("img");
                if($.browser.msie) {
                    var $cropImg = uploadDialogTools.dialogDiv.find(".cropImg");
                    $cropImg.find("img").remove();
                    var img = new Image();
                    img.onload = uploadDialogTools.initImgWidthAndHeight;
                    $cropImg.prepend(img);
                    $(img).attr("src", data.imagePath);
                    previewImgAreaImg.attr("src", data.imagePath);
                }
                else{
                    previewImgAreaImg.attr("src", data.imagePath);
                    previewImgAreaImg.removeAttr("style").addClass("img");
                }

                uploadDialogTools.dialogDiv.find(".previewImg").removeAttr("style").find("img").addClass("img");
            }
        }
        else{
            uploadDialogTools.dialogDiv.find(".message").html(data.message);
            //uploadDialogTools.path.imagePath = "";
            //uploadDialogTools.path.webPath = "";
        }
    },
    upExt:'jpg,jpeg,gif,png',	//允许上传的文件
    uploadInputName:'filedata',
    onSelect:function(file){
        
    },
    onError:function(data){
        if(data.message){
            uploadDialogTools.dialogDiv.find(".message").html(data.message);
        }
        else{
            uploadDialogTools.dialogDiv.find(".message").html("上传图片错误！");
        }
    }
}

/**
 * 验证
 * @return {boolean}
 */
uploadDialogTools.validate = function(){
    var result = true;
    $(uploadDialogTools.options.ID).each(function(i, item){
        var $item = $(item);
        var required = $item.attr("required");
        if(required){
            var value = $item.find("input.path").val();
            if(value == ""){
                $item.parent().find(".validate-error").html("必填");
                result = result && false;
            }
        }
    });
    return result;
}

/**
 * 初始化上传控件
 */
uploadDialogTools.init = function(){
    var options = uploadDialogTools.options;
    var _dialog = $('<div id="' + options.divBase + '"></div>');
    _dialog.appendTo("body");
    $(options.ID).live("click", uploadDialogTools.addDialog);

    //添加删除按钮
    if(options.canDelete == "deleteValue" || options.canDelete == "deleteItem"){
        $(options.divRoot).each(function(i, item){
            var $item = $(item);
            var canDelete = $item.find(options.ID).attr("canDelete");
            if(canDelete == "deleteValue" || canDelete == "true" || canDelete == "deleteItem"){
                var value = $item.find("input.path").val();
                if(value && value != ""){
                    $item.find(".close").addClass("icon-clearpic").show();
                    //$item.css("margin-left", "30px");
                }
            }
        });
    }

    $(options.divRoot).find(".close").live("click", function(){
        var $parent = $(this).parents(options.divRoot);
        var canDelete = $parent.find(options.ID).attr("canDelete");
        if(canDelete == "deleteValue" || canDelete == "true"){
            $parent.find("input").val("");
            $parent.find("img").fadeOut("slow",function(){
                $(this).remove();
            });
            $parent.find(".close").fadeOut("slow").removeClass("icon-clearpic");
            //$parent.animate({"margin-left": '0px'}, "slow");
        }
        else if(canDelete == "deleteItem"){
            $parent.fadeOut("slow",function(){
                $(this).remove();
            });
            /*$parent.animate({"margin-left": '0px'}, "slow", function(){
                $(this).remove();
            });*/
        }
    });
}

uploadDialogTools.initUploadOptions = function(options){
    uploadDialogTools.uploadOptions = $.extend(uploadDialogTools.uploadOptions, options);
}

uploadDialogTools.addDialog = function(){
    uploadDialogTools._viewSeletor = this;
    var options = uploadDialogTools.options;
    
    if(!uploadDialogTools.dialogDiv){
        uploadDialogTools.dialogDiv = $("#" + options.divBase);
    }
    
    var webPath = $(this).find(options.input).val();
    var imagePath = $(this).find("img").attr("src");
    
    if(webPath && webPath != ""){
        uploadDialogTools.path.webPath = webPath;
        if(imagePath){
            uploadDialogTools.path.imagePath = imagePath;
        }
    }

    //初始化属性
    options.cropRatio = undefined;
    options.canCrop = undefined;
    options.width = undefined;
    options.height = undefined;
    options.cropWidth = undefined;
    options.cropHeight = undefined;

    var currentOptions = $(uploadDialogTools._viewSeletor).attr("data-options");
    options.uploadUrl = uploadDialogTools.uploadOptions.uploadUrl;
    if(currentOptions){
        try{
            currentOptions = eval("currentOptions = " + currentOptions);

            options = $.extend(options, currentOptions);
            if(currentOptions.uploadUrl){
                uploadDialogTools.uploadOptions.uploadUrl = currentOptions.uploadUrl;
            }

        }
        catch(e){ }
    }

    //清空提示信息
    uploadDialogTools.dialogDiv.find(".message").html("");
    
    if(uploadDialogTools.onBeforeShowDialog){
        uploadDialogTools.onBeforeShowDialog.call(uploadDialogTools.dialogDiv, $(this));
    }
    
    if(!uploadDialogTools.initFlag){
        
        //uploadDialogTools.dialogDiv.append($(uploadDialogTools.options.template).html());
        uploadDialogTools.dialogDiv.append(options.dialogHtml);

        uploadDialogTools.uploadOptions.ID = uploadDialogTools.dialogDiv.find(".inputUploadBtn");
        //绑定关闭弹出框事件
        uploadDialogTools.dialogDiv.find(".closeBtn").click(function(){
            uploadDialogTools.uploadOptions.uploadUrl = uploadDialogTools.options.uploadUrl;
            uploadDialogTools.dialogDiv.hide();
            if(uploadDialogTools.options.canCrop && uploadDialogTools.crop){
                uploadDialogTools.crop.destroy();
            }
        });

        uploadDialogTools.dialogDiv.find(".submitBtn").bind("click", function(){
            if(uploadDialogTools.options.canCrop){
                uploadDialogTools.submitCropOk();
            }
            else{
                uploadDialogTools.submitOk();
            }
        });


        var $previewImgArea = uploadDialogTools.dialogDiv.find(".previewImgArea .cropImg");
        var inputHiddens = [
            '<input type="hidden" name="cropX" value="" /> ',
            '<input type="hidden" name="cropY" value="" /> ',
            '<input type="hidden" name="cropW" value="" /> ',
            '<input type="hidden" name="cropH" value="" /> ',
            '<input type="hidden" name="imgW" value="" /> ',
            '<input type="hidden" name="imgH" value="" /> '
        ].join("");
        $previewImgArea.append(inputHiddens);
        uploadDialogTools.initFlag = true;
    }


    var data;// = $(uploadDialogTools._viewSeletor).attr("data-options");
    if(currentOptions){
        var data = uploadDialogTools.uploadOptions.data;
        if(currentOptions.width && currentOptions.height){
            data.width = currentOptions.width;
            data.height = currentOptions.height;
        }
        if(currentOptions.canCrop){
            data.canCrop = currentOptions.canCrop;
        }
        else{
            data.canCrop = false;
        }
        if(currentOptions.cropWidth && currentOptions.cropHeight){
            data.cropWidth = currentOptions.cropWidth;
            data.cropHeight = currentOptions.cropHeight;
        }
        if(currentOptions.size){
            data.size = currentOptions.size;
        }
    }

    //如果可以裁剪
    if(options.canCrop){
        uploadDialogTools.uploadOptions.canCrop = true;
        uploadDialogTools.uploadOptions.cropRatio = options.cropRatio ? options.cropRatio : false;
        uploadDialogTools.uploadOptions.cropWidth = options.cropWidth;
        uploadDialogTools.uploadOptions.cropHeight = options.cropHeight;

        //设置是否初始化裁剪标志值
        uploadDialogTools.initCrop = false;
    }
    ajaxUpload(uploadDialogTools.uploadOptions);
    

    var $item = uploadDialogTools.dialogDiv.find(".previewImgArea");
    $item.find(".previewImg").each(function(){
        var $this = $(this);
        $this.removeAttr("style");
        var height = $this.height();
        if(options.canCrop){
            if(currentOptions && currentOptions.cropWidth && currentOptions.cropHeight){
                var width = Math.round(height * currentOptions.cropWidth / currentOptions.cropHeight);
                if(width > 190){
                    width = 190;
                }
                height = Math.round(width * currentOptions.cropHeight / currentOptions.cropWidth);
                $this.css({width: width + "px", height: height + "px"});
                $item.find(".widthLabel").text(currentOptions.cropWidth);
                $item.find(".heightLabel").text(currentOptions.cropHeight);
            }
        }
        else{
            if(currentOptions && currentOptions.width && currentOptions.height){
                var width = Math.round(height * currentOptions.width / currentOptions.height);
                $this.css({width: width + "px", height: height + "px"});
                $item.find(".widthLabel").text(currentOptions.width);
                $item.find(".heightLabel").text(currentOptions.height);
            }
        }
        $this.addClass("preview-img");
    });
    if(imagePath){
        $item.find("img").removeAttr("style").attr("src", imagePath);
        if($.browser.msie){
            var $cropImg = $item.find(".cropImg");
            $cropImg.find("img").remove();
            var img = new Image();
            img.onload = uploadDialogTools.initImgWidthAndHeight;
            $cropImg.append(img);
            $(img).attr("src", imagePath);
        }
        else{
            $item.find(".cropImg img").css({maxWidth:"100%", maxHeight:"100%"});
        }
    }
    else{
        $item.find("img").attr("src", "");
    }
    uploadDialogTools.dialogDiv.show();
}

uploadDialogTools.submitOk = function(){
    var src = uploadDialogTools.dialogDiv.find(".cropImg").find("img:first").attr("src");
    if(src && src != ""){
        var img = document.createElement("img");
        var $item = $(uploadDialogTools._viewSeletor);
        img.src = uploadDialogTools.path.imagePath;
        $item.find("img").remove().end().append(img);

        $item.find(uploadDialogTools.options.input).val(uploadDialogTools.path.webPath);
        var $parent = $item.parent();
        $parent.find(".validate-error").empty();

        if(uploadDialogTools.onSelected){
            uploadDialogTools.onSelected.call(uploadDialogTools.dialogDiv, $item);
        }

        if(uploadDialogTools.options.canDelete == "deleteValue" || uploadDialogTools.options.canDelete == "deleteItem" ){
            var canDelete = $item.attr("canDelete");
            if(canDelete != "false"){
                $parent.find(".close").hide().addClass("icon-clearpic").fadeIn("slow");
                //$parent.animate({"margin-left": '+30px'}, "slow");
            }
        }
        uploadDialogTools.uploadOptions.uploadUrl = uploadDialogTools.options.uploadUrl;
    }

    uploadDialogTools.dialogDiv.hide();
}

uploadDialogTools.submitCropOk = function(){
    if(!uploadDialogTools.initCrop){
        uploadDialogTools.submitOk();
        return;
    }
    var $previewImgArea = uploadDialogTools.dialogDiv.find(".previewImgArea");
    var imgW = parseInt($previewImgArea.find("input[name='imgW']").val());
    var imgH = parseInt($previewImgArea.find("input[name='imgH']").val());
    var cropX = parseInt($previewImgArea.find("input[name='cropX']").val());
    var cropY = parseInt($previewImgArea.find("input[name='cropY']").val());
    var cropW = parseInt($previewImgArea.find("input[name='cropW']").val());
    var cropH = parseInt($previewImgArea.find("input[name='cropH']").val());

    var $img = $previewImgArea.find(".cropImgArea img");
    var width = $img.width();
    var height =$img.height();

    var ratio = imgW / width;
    var data = {};
    data.width =  uploadDialogTools.options.cropWidth;
    data.height =  uploadDialogTools.options.cropHeight;
    data.cropX = Math.round(cropX * ratio);
    data.cropY = Math.round(cropY * ratio);
    data.cropW = Math.round(cropW * ratio);
    data.cropH = Math.round(cropH * ratio);
    data.imgPath = uploadDialogTools.path.webPath;
    data.crop = true;

    $.ajax({
        url: uploadDialogTools.uploadOptions.uploadUrl,
        type : "POST",
        dataType: "json",
        data: data,
        success: function(jsonData){
            if(jsonData && jsonData.success == "success"){
                var timestamp=new Date().getTime();
                uploadDialogTools.path.imagePath = uploadDialogTools.path.imagePath + "?timestamp=" + timestamp;
                uploadDialogTools.submitOk();
                uploadDialogTools.crop.destroy();
                uploadDialogTools.dialogDiv.hide();
            }
        }
    });
}

uploadDialogTools.showPreview = function(coords){
    if(parseInt(coords.w) > 0){
        var $previewImgArea = uploadDialogTools.dialogDiv.find(".previewImgArea");
        $previewImgArea.find("input[name='cropX']").val(coords.x);
        $previewImgArea.find("input[name='cropY']").val(coords.y);
        $previewImgArea.find("input[name='cropW']").val(coords.w);
        $previewImgArea.find("input[name='cropH']").val(coords.h);
        var previewImgs = uploadDialogTools.dialogDiv.find(".previewImg");
        //var $img = uploadDialogTools.dialogDiv.find(".previewImgArea .bigimg").find("img");
        var bound = uploadDialogTools.crop.getBounds();
        var imgWidth =  bound[0];
        var imgHeight =  bound[1];
        previewImgs.each(function(){
            var $previewImg = $(this);
            var mx = $previewImg.width() / coords.w;
            var my = $previewImg.height() / coords.h;
            $previewImg.find("img").css({
                width: Math.round(mx * imgWidth) + "px",
                height: Math.round(my * imgHeight) + "px",
                marginLeft: "-" + Math.round(mx * coords.x) + "px",
                marginTop: "-" + Math.round(my * coords.y) + "px"
            });
        })
    }
}

uploadDialogTools.initImgWidthAndHeight = function(){
    var swidth = parseInt(this.width);
    var sheight = parseInt(this.height);

    var $this = $(this);

    var $cropImgArea = $this.parents(".cropImgArea");
    var xw = swidth / $cropImgArea.width();
    var xh = sheight / $cropImgArea.height();
    var setWidth = xw > xh ? $cropImgArea.width() : (swidth / xh);
    var setHeight = xh > xw ? $cropImgArea.height() : (sheight / xw);
    $this.removeClass("img");

    $this.css({width: Math.round(setWidth) + "px", height: Math.round(setHeight) + "px"});
}

