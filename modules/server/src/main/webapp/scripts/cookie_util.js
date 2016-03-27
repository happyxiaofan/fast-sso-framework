/**
 * Cookie操作工具类
 */
var Cookie = {

	/**
	 *  按名称获取cookie值
	 * @param {String} name
	 */
	get: function(name) {
		var cookies = document.cookie;
		cookies = cookies.split("; ")
		for (var i = 0, len = cookies.length; i < len; ++i) {

			var cookieItem = cookies[i];
			var pos = cookieItem.indexOf("=");
			var cname = cookieItem.substring(0, pos);
			var cval = unescape(cookieItem.substring(pos + 1));

			if (name == cname) {
				return cval;
			}
		}
	},

	/**
	 * 设置cookie值
	 * @param {Object} name
	 * @param {Object} val
	 * @param {Object} path
	 * @param {Object} expMinute 过期时间，以分钟为单位
	 * @param {Object} domain
	 * @param {Object} secure
	 */
	set: function(name, val, path, expMinute, domain, secure) {
		var cookieItem = name + "=" + escape(val);
		
		if (path) {
			cookieItem += ";path=" + path;
		}
		
		if (expMinute) {
			cookieItem += ";expires=" + new Date(new Date().getTime() + expMinute * 60 * 1000).toGMTString();
		}
		
		if (domain) {
			cookieItem += ";domain=" + domain;
		}
		
		if (secure) {
			cookieItem += ";secure";
		}
		
		document.cookie = cookieItem;
		
	},

	/**
	 * 删除cookie 
	 * @param {Object} name
	 */
	del: function(name) {
		document.cookie = name + "=anyVal;expires=" + new Date(0).toGMTString();
	}
}