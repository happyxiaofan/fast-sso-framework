<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${sysName}</title>
<script type="text/javascript" src="${appctx}/scripts/jquery.js"></script>
<script type="text/javascript" src="${appctx}/scripts/cookie_util.js"></script>
<script type="text/javascript">
	
	var UNAME_COOKIE_NAME = "lastLoginUserName";

	$(function() {
		// 如果name没有value，将cookie中存储过的name值写入
		var eleName = $("input[name=name]");
		eleName.val(Cookie.get(UNAME_COOKIE_NAME));
		
		// 登录按钮被点击时记住当前name
		$("form").submit(function() {
			Cookie.set(UNAME_COOKIE_NAME, $.trim(eleName.val()), null, 7 * 24 * 60);
		});
	});
</script>
</head>
<body>


<c:if test="${empty loginUser}">
<c:if test="${not empty errorMsg}">
	<p style="color:red;font-weight:bold;">${errorMsg}</p>
</c:if>
<form action="/login" method="post">
	<p>账号：<input type="text" name="name" autocomplete="off"/></p>
	<p>密码：<input type="password" name="passwd" autocomplete="off"/></p>
	<p><input type="submit" value="登录" /></p>
</form>
</c:if>

<c:if test="${not empty loginUser}">
<p>欢迎：${loginUser}
	<button style="margin-left:20px;" onclick="location.href='http://www.ca.com:8080/logout'">退出</button>
</p>
<%--<ul>
	<c:forEach items="${sysList }" var="sys">
		<li><a href="${sys.homeUrl }" target="_blank">${sys.name}</a></li>
	</c:forEach>
</ul>--%>
</c:if>

<%--<c:forEach items="${sysList}" var="sys">
<script type="text/javascript" src="${sys.baseUrl}/cookie_set?vt=${vt}"></script>
</c:forEach>--%>
</body>
</html>