/*
*common.js
*2013-09-09
*
*
*/
var _LOAD_SCRIPT_ = function (u, bA) {
        /**
        * 待页面onload之后异步载入JS
        * @param {string} u 脚本地址
        * @param { Boolean} bA   是否不用等待页面onload，立即加载
        */
        if(!u)return ;
        if (!bA) {
              _LOAD_SCRIPT_DELAY_(u);
               return;
       }
        var s = document.createElement('script' );
       s.type = 'text/javascript';
       s.async = true;
       s.src = u;
       setTimeout( function(){document.getElementsByTagName("head" )[0].appendChild(s)},0);
}
var _G_SCRIPT_lIST_ = [];
var _LOAD_SCRIPT_DELAY_ = function (u) {
       _G_SCRIPT_lIST_[_G_SCRIPT_lIST_.length] = u;
}
var _RUNNING_LOAD_SCRIPT_ = function () {
        if (_G_SCRIPT_lIST_ && _G_SCRIPT_lIST_.length > 0) {
               for (var i = 0; i < _G_SCRIPT_lIST_.length; i++) {
                     _LOAD_SCRIPT_( _G_SCRIPT_lIST_[i] , true );
              }
       }
}
function _LOAD_VIRTUAL_FUNCTION_(fnName, defaultRet) {
        if (!defaultRet) {
              window[fnName] = function () {};
       } else {
              window[fnName] = function () {
                      return defaultRet;
              };
       }
}
var T = {
        /**
        * 弹出lightbox            
        * @param  {string} html     弹出的html代码{=id} 为当前dom id
        * @param  {number} width    弹出框的宽度  默认450px;
        * @param  {Object} buttons 按钮对象{'确定':function(){},'关闭':'close'}
        * @param  {Boolean} noIn    不显示In层
        * @param  {number}   zIndex
        * @param  {String}   style 最外框样式
        * @param  {Boolean} showMask 是否显示遮罩层
        * @param  {String}   clasName 最外框class
        * @return {Dom Object}      自身Dom
        */
       lightBox : function (html, width, buttons , noIn , zIndex , style , showMask , clasName) {
              width=width ||450;
              buttons = buttons || {};
              zIndex = zIndex || 300;
               var Dom = document.createElement("div" );
               var d = new Date();
               var tmpId = d.getTime().toString(36);
              d = "JS_lightBox_" + tmpId;
              Dom.className = "lightBox " + ( clasName ? clasName : '' );
              Dom.id = d;
               if(style)Dom.style.cssText = style;
              Dom.style.zIndex = zIndex;
               var h = '<div class="htmls">' + (html+"").replace( /{=id}/g , tmpId ) + '</div>';
               var btn = '' ;
               for (var k in buttons) {
                     btn += '<button id="' + d + '_' + k + '" class="lightbox_btns_'+k+'">' + k + '</button>';
              }
               if (btn)
                     btn = '<div class="btns">' + btn + '</div>' ;
              h = noIn ?  h + btn : '<div class="in" style="width:'+width+'px">' + h + btn + '</div>' ;
              Dom.innerHTML = h;
              Dom._showMask = showMask;
               if( showMask )T.showMask( zIndex - 1 );
              document.body.appendChild(Dom);
               for (var k in buttons) {
                      if (buttons[k] == "close" ) {
                           document.getElementById(d + '_' + k).onclick = function () {
                                  T.closeLightBox(d);
                           }
                     } else if (typeof buttons[k] === "function") {
                           document.getElementById(d + '_' + k).onclick = buttons[k];
                     }
              }
              T.lightBoxId = d; //一个全局id
              T.lightBoxTempId = tmpId;
               return Dom;
       },
       closeLightBox: function(id,callback){
               /**
               * 关闭LightBox，如id为空，则关闭当前lightbox
               * @param { string} id lightbox的domId,可选
               */
               if(!id)id=T.lightBoxId;
              id = "JS_lightBox_" + ( (id+"" ).replace("JS_lightBox_", "") );
               var dom = document.getElementById(id);
               if(dom){
                      if( dom._showMask )T.hideMask();
                     dom.parentNode.removeChild(dom);
              }
               if(typeof callback == 'function')callback(id);
       },
       showMask : function(zIndex){
               /**
               * 显示遮罩层
               * @param {number} zIndex
               */
               if(T.maskDom)T.hideMask();
              zIndex = zIndex || 290;
               var mask = document.createElement("div" ),
              h =  (document.body.scrollHeight >= window.screen.availHeight)?document.body.scrollHeight:window.screen.availHeight;
              mask.id = "JS_mask";
              mask.className = "c-mask";
              mask.style.cssText = "height:"+h+"px;width:100%;position:absolute;background:#000;z-index:900;top:0;left:0;opacity:0.5;filter:alpha(opacity=50);z-index:" +zIndex;
              document.body.appendChild(mask);
              T.maskDom = mask;
       }
}
/**
 * 绑定事件
 * @param {object} obj     对象
 * @param {string} type    事件类型
 * @param {function} handler 执行函数
 * @param {object} scope   函数上下文
 */
T.addHandler=function(obj, type, handler, scope) {
        function fn(event) {
               var evt = event ? event : window.event;
              evt.target = event.target || event.srcElement;
               return handler.apply( scope || this , arguments);
       }
       obj.eventHash = obj.eventHash || {};
       
       (obj.eventHash[type] = obj.eventHash[type] || []).push({ "name": type, "handler": handler, "fn" : fn, "scope" : scope });
        if (obj.addEventListener) {
              obj.addEventListener(type, fn, false);
       } else if (obj.attachEvent) {
              obj.attachEvent( "on" + type, fn);
       } else {
              obj[ "on" + type] = fn;
       }
};
/**
 * 删除由T.addHandler绑定的事件
 * @param {object} obj     对象
 * @param {string} type    事件类型
 * @param {function} handler 执行函数
 * @param {object} scope   函数上下文
 */
T.removeHandler=function(obj, type, handler, scope) {
       obj.eventHash = obj.eventHash || {};
        var evtList = obj.eventHash [type] || [], len = evtList.length;
        if (len > 0) {
               for (; len--; ) {
                      var curEvtObj = evtList[len];
                      if (curEvtObj.name == type && curEvtObj.handler === handler && curEvtObj.scope === scope){
                            if (obj.removeEventListener) {
                                  obj.removeEventListener(type, curEvtObj.fn, false);
                           } else if (obj.detachEvent) {
                                  obj.detachEvent( "on" + type, curEvtObj.fn);
                           } else {
                                  obj[ "on" + type] = null;
                           }
                     evtList.splice(len, 1);
                      break;
                     }
              }
       }
};
T.addHandler(window , 'load' , _RUNNING_LOAD_SCRIPT_ );
