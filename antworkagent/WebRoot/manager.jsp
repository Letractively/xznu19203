<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="application.jsp"%>
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
	#content{
		position:absolute;
		top:55px;
		width:100%;
		height:600px;
		margin:0px;
		padding:0px;
		}
	#main{
		background-color:#9CF;
		width:50%;
		height:50%;
		text-align:center;
		margin:0 auto;
		margin-top:150px;
		}
	#line2{
		position:absolute;
		top:650px;
		background-color:#4C94EA;
		width:100%;
		height:5px;
		}
</style>
</head>
<body>
	<div>
		<div id="head">
		</div>
		<div id="line"></div>
		<div id="content">
            <div id="main">
                 <form name="form1" method="post">
                 	<table width="100%" height="100%" cellpadding="0" cellspacing="0">
                    	<tr>
                        	<td height="30%">&nbsp;</td>
                        </tr>
                        <tr>
	                        <td align="center">
                             	username:<input type="text" id="" name="username"/>
                                <br/>
                                password:<input type="password" id="" name="password"/>
                                <br/>
                                <input type="button" id="" name="" value="登录" onclick="manager_login();"/>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
		</div>
        <div id="line2"></div>
	</div>
</body>
</html>
