<%@ page language="java" import="java.util.*" pageEncoding="GBK"%> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
	 <title>欢迎回家</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--<link rel="stylesheet" type="text/css" href="styles.css">-->
  </head>
  <body>
  
    <table width="100%" border="0" cellspacing="2" height="100%" style="width: 785px; height: 26px;">
    	<tr><td>用户名：
    		<input type="text" name="account"/>
    		</td>
    		<td>密码：
    		<input type="password" name="password"/>
    		</td>
    		<td>
    		<input type="checkbox" name="autologin"/>
    		自动登录
    		</td>
    		<td>
    		<input type="button" value="登 录">
    		</td>
    		<td>
    		<input type="button" value="免费注册"/>
    		</td>
    		<td>--</td>
    		<td>游客浏览</td>
    	</tr>	
    </table width="100%" border="0" cellspacing="2" height="100%" style="width: 785px; height: 26px;">
    <table>
    <tr><td>
    		<img src="img/first_page_img.jpg" width="100%" height="100%"/>
    	</td>
    </tr>	
    </table>   			
    </body>
</html>
