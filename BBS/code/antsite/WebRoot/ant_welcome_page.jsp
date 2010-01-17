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
  	<!-- 使table显示效果圆角 -->
<style type="text/css">
div.Liehuo_net_RoundedCorner...{background: #FFD1FA}
b.rtop, b.rbottom...{display:block;background: #FFF}
b.rtop b, b.rbottom b...{display:block;height: 1px;overflow:
hidden; background: #ffD1FA}
b.r1...{margin: 0 5px}
b.r2...{margin: 0 3px}
b.r3...{margin: 0 2px}
b.rtop b.r4, b.rbottom b.r4...{margin: 0 1px;height: 2px}
</style>
    <table width="100%" border="0" cellspacing="2" height="100%" style="width: 785px; height: 26px;">
    
    	<tr>
    		<input type="text" name="account"/>
    		<input type="password" name="password"/>
    		<input type="checkbox" name="autologin"/>
    		<td>自动登录</td>
    		<input type="button" value="登 录">
    		<input type="button" value="免费注册"/>
    		<text>-</text>
    		<text>游客浏览</text>
    	</tr>	
    </table width="100%" border="0" cellspacing="2" height="100%" style="width: 785px; height: 26px;">
    <table>
    <tr><td>
    		<img src="img/first_page_img.jpg" width="100%" height="100%">
    	</td>
    </tr>	
    </table>   			
    </body>
</html>
