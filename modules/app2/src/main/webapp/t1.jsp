<%@page import="com.rhwayfun.sso.client.SSOUser"%>
<%@page import="com.rhwayfun.sso.client.UserHolder"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
t1.jsp of test2 project

<%
	SSOUser user = UserHolder.getUser();
	out.println("<br/>");
	out.println("<br/>");
	out.println("ID: " + user.getId());
	out.println("<br/>");
	for (String propertyName : user.propertyNames()) {
	    out.println(propertyName + ": " + user.getProperty(propertyName));
		out.println("<br/>");
	}
%>
</body>
</html>