<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/index.css" />
<title>无标题文档</title>
<script>
	function manager_login(){
			document.form1.action="managerAction.antwork?method=managerLogin";
			document.form1.submit();
		}
</script>
</head>
<body>
	<form name="form1" method="post">
		username:<input type="text" id="" name="username"/>
		password:<input type="password" id="" name="password"/>
		<input type="button" id="" name="" value="登录" onclick="manager_login();"/>
	</form>
</body>
</html>
