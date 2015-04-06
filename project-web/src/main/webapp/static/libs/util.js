String.prototype.trim=function()
{
     return this.replace(/(^\s*)(\s*$)/g, '');
};

Date.prototype.format = function(format) {
	/*
	 * eg:format="yyyy-MM-dd hh:mm:ss";
	 */
	if (!format) {
		format = "yyyy-MM-dd hh:mm:ss";
	}
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	// millisecond
	};
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
};
/**
 * 日期格式话 dataStr:被格式话的日期字符串 format:日期格式
 */
formatDate = function(dataStr, format) {
	if(dataStr==undefined||dataStr==''||dataStr==null)
		return '';
	return new Date(dataStr.replace(/-/ig, '/')).format(format);
};
 
/**
 * 字符转换成json队形
 * @param strData
 * @returns date
 */
function parserToJson(strData) {
	return (new Function("return " + strData))();
}