<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/index.css" />
<link rel="stylesheet" type="text/css" href="css/login.css" />
<script type="text/javascript" src="jquery/jquery.js"></script>
<script type="text/javascript" src="js/login.js"></script>
<title>无标题文档</title>
</head>
<body>
	<div>
    	<div id="left"></div>
        <div id="right">
        <form name="form1" method="post">
        	<table width="100%" height="40%" border="0" cellpadding="0" cellspacing="0">
            	<tr>
                	<td colspan="2"></td>
                </tr>
                <tr>
                	<td><p class="font">账号:</p></td>
                    <td><input type="text" id="username" name="username" class="text"/></td>
                </tr>
                <tr>
                	<td><p class="font">密码:</p></td>
                    <td><input type="password" id="password" name="password" class="text"/></td>
                </tr>
                <tr>
                	<td colspan="2" align="center">
                    	<input type="submit" id="button" name="" value="登录" class="positive"/>
                        &nbsp;
                        <input type="reset" id="reset" name="" value="重置" class="positive"/>
                    </td>
                </tr>
                <tr>
                	<td colspan="2" align="right"><p><a href="register.jsp">会员注册</a> <a href="loginAction.antwork?method=visitor_login">游客浏览</a></p></td>
                </tr>
            </table>
        </form>
        </div>
    </div>
    <div class="backgroundDiv"></div>
	
	<div class="info">
	   <div id="close"></div>
	   <img src="myimage/ni.png"/>
	   <div>
	   	<div id="alt">提示消息</div>
	   	<div id="msg"></div>
	   </div>
	</div>
</body>
</html>
