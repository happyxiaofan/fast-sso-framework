<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>config</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery.js"></script>
<script type="text/javascript">

	function refreshConfig() {
		$.ajax({
			type:"post",
			url:"${pageContext.request.contextPath}/config/refresh",
			//contentType:"application/json;charset=utf-8",
			data: {code:$("#code").val()},
			success:function (data){
				alert(data ? "更新成功" : "更新失败");
			},
			error:function(){
				alert("系统错误");
			}
		});
	}
</script>
</head>
<body>
<input type="text" id="code" >
<button onclick="refreshConfig();">更新配置</button> 
</body>
</html>