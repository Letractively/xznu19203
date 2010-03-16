<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="application.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
<style>
	body{
		background:none repeat scroll 0 0 #FFFFFF;
		font-family:Tahoma;
		font-size:12px;
		margin:0;
		overflow:hidden;
		padding:0;
		}
	#head{
		position:absolute;
		margin:0;
		padding:0;
		background-color:#E1ECF9;
		width:100%;
		height:50px;
		color:#006;
		font-size:16px;
		}
	#line{
		position:absolute;
		top:50px;
		background-color:#4C94EA;
		width:100%;
		height:5px;
		}
	#line2{
		position:absolute;
		top:650px;
		background-color:#4C94EA;
		width:100%;
		height:5px;
		}
	#content{
		position:absolute;
		top:55px;
		width:100%;
		height:600px;
		margin:0px;
		padding:0px;
		}
	#left{
		width:80%;
		height:600px;
		float:left;
		margin:0px;
		padding:0px;
		}
	#right{
		width:20%;
		height:600px;
		float:right;
		margin:0px;
		padding:0px;
		}
</style>
</head>

<body>
	<div>
    	<div id="head">
        	<p>后台管理系统</p>
        </div>
        <div id="line"></div>
        <div id="content">
        	<div id="left">
            	<table width="100%" height="100%" border="1" bordercolor="#4c94ea" cellpadding="0" cellspacing="0" frame="rhs">
        			<tr>
            		<td style="border:none">&nbsp;</td>
            		</tr>
       			 </table>
            </div>
            <div id="right">&nbsp;</div>
        </div>
        <div id="line2"></div>
    </div>
</body>
</html>
