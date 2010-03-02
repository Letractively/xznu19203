<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
</head>
<style>
	body{
		font-family:Arial, Helvetica, sans-serif;
		font-size:12px;
		margin:0px auto;
		height:auto;
		width:750px;
		border:1px solid #000000;
		margin-top:200px;
		}
	#left{
		width:550px;
		height:300px;
		background-image:url(myimage/welcome.jpg);
		background-repeat:no-repeat;
		margin:0px 0px 3px 0px;
		float:left;
		}
	#right{
		width:200px;
		height:300px;
		background-image:url(myimage/bgcolor.jpg);
		background-repeat:no-repeat;
		margin:0px 0px 0px 0px;
		float:right;
		}
	.text{
		width:150px;
		height:15px;
		background-color:#CFF;
		}
	.positive{
		background-color:#E6EFC2;
		border:1px solid #C6D880;
    	color:#96F;
	}
	.font{
		color:#96F;
		}
	a:link{
		color:#FF0000;
		text-decoration:underline;
		}
	a:visited{
		color:#00FF00;
		text-decoration:none
		}
	a:hover {
		color:#000000;
		text-decoration:none;
    }
    a:active {
		color:#FFFFFF;
		text-decoration:none;
    }

</style>
<body>
	<div>
    	<div id="left"></div>
        <div id="right">
        	<table width="100%" height="40%" border="0" cellpadding="0" cellspacing="0">
            	<tr>
                	<td colspan="2"></td>
                </tr>
                <tr>
                	<td><p class="font">账号:</p></td>
                    <td><input type="text" id="" name="" class="text"/></td>
                </tr>
                <tr>
                	<td><p class="font">密码:</p></td>
                    <td><input type="password" id="" name="" class="text"/></td>
                </tr>
                <tr>
                	<td colspan="2" align="center">
                    	<input type="button" id="" name="" value="登录" class="positive"/>
                        &nbsp;
                        <input type="button" id="" name="" value="重置" class="positive"/>
                    </td>
                </tr>
                <tr>
                	<td colspan="2" align="right"><p><a href="#">会员注册</a> <a href="#">游客浏览</a></p></td>
                </tr>
            </table>
        </div>
    </div>
</body>
</html>
