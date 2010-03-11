<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="application.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/index.css" />
<title>无标题文档</title>
<script>
	function sub(){
		document.form1.action="managerAction.antwork?method=saveUser";
		document.form1.submit();
		}
</script>
<style>
</style>
</head>
<body>
	<form name="form1" method="post">
		<div id="list">
			<c:forEach var="userinfo" items="${userinfo_list}">
				<p>${userinfo.userid}&nbsp;${userinfo.username}</p>
			</c:forEach>
		</div>
		username:<input type="text" id="" name="username"/>
		password:<input type="password" id="" name="password"/>
		<input type="submit" id="" name="" value="提交" onclick="sub();"/>
	</form>
</body>
</html>
