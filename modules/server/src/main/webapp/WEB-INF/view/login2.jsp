<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${sysName}</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/cookie_util.js"></script>
<script type="text/javascript">
	
	var UNAME_COOKIE_NAME = "lastLoginUserName";
	
	$(function() {
		// 如果name没有value，将cookie中存储过的name值写入
		var eleName = $("input[name=name]");
		eleName.val(Cookie.get(UNAME_COOKIE_NAME));

		// 登录按钮被点击时记住当前name
		$("form").submit(function() {
			alert($.md5($.md5($("input[name=passwd]").val()) + $("input[name=captcha]").val()));
			Cookie.set(UNAME_COOKIE_NAME, $.trim(eleName.val()), null, 7 * 24 * 60);
			// 将密码字段使用 MD5(MD5(密码) + 验证码）编码后发给服务端
			var elePasswd = $("input[name=passwd]");
			var passwd = elePasswd.val();
			elePasswd.val($.md5($.md5(passwd) + $("input[name=captcha]").val()));
		});
		
		// 加载验证码
		//drawCaptcha();
	});
	
	function drawCaptcha() {
		var im = $("#captchaImg");
		im.src = "${pageContext.request.contextPath}/preLogin?time=" + new Date().getTime();
		/*.done(function(data) {
		 console.log(data);
		 $("#captchaImg").attr("src", data.imgData);
		 }).fail(function() {
		 alert("验证码加载失败");
		 });*/
	}
</script>
</head>
<body>


<c:if test="${empty loginUser}">
<c:if test="${not empty errorMsg}">
	<p style="color:red;font-weight:bold;">${errorMsg}</p>
</c:if>
<form action="/login" method="post">
	<p>账号：<input type="text" name="name" autocomplete="off" /></p>
	<p>密码：<input type="password" name="passwd" autocomplete="off" /></p>
	<p>验证码：<input style="width:80px;" type="text" name="captcha" autocomplete="off" /><img src="preLogin" onclick="drawCaptcha()" id="captchaImg" style="cursor:pointer;"></p>
	<p><label><input type="checkbox" name="rememberMe" value="true"/>下次自动登录</label>
	<p><input type="submit" value="登录" /></p>
</form>
</c:if>

<c:if test="${not empty loginUser}">
<p>欢迎：${loginUser}
	<button style="margin-left:20px;" onclick="location.href='https://www.ca.com:8443/logout'">退出</button>
</p>
<ul>
	<c:forEach items="${sysList }" var="sys">
		<li><a href="${sys.homeUrl }" target="_blank">${sys.name}</a></li>
	</c:forEach>
</ul>
</c:if>

<c:forEach items="${sysList}" var="sys">
<script type="text/javascript" src="${sys.baseUrl}/cookie_set?vt=${vt}"></script>
</c:forEach>
</body>
</html>