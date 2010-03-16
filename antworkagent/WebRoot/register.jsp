<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="application.jsp"%>
<%@ page import="com.TzTwork.awa.antworkagent.po.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="jquery/jquery.js"></script>
<title>无标题文档</title>
</head>
<style>
	body{
		width:770px;
		margin:auto;
		}
	#head{
		color:#FFF;
		text-align:left;
		font-family:Arial, Helvetica, sans-serif;
		font-size:25px;
		width:771px;
		height:28px;
		background-color:#0F3E5B;
		margin-top:10px;
		}
	#content{
		background-color:#FFFFF7;
		width:769px;
		border:1px solid #FFCC00;
		}
	table{
		margin:0px;
		}
	textarea{
		width:400px;
		height:100px;
		}
</style>
<body>
	<div id="head"><p>注册会员</p></div>
    <br />
<div id="content">
    	<form>
        	<table cellpadding="0" cellspacing="15" width="100%">
            	<tr>
                	<td width="30%">用户名：</td>
                    <td><input type="text" id="username" name="username" /></td>
                </tr>
                <tr>
                	<td>密码：</td>
                    <td><input type="password" id="password" name="password" /></td>
                </tr>
                <tr>
                	<td>真实姓名：</td>
                    <td><input type="text" id="password" name="password" /></td>
                </tr>
                <tr>
                	<td>现状：</td>
                    <td>
                        <select name="active">
                            <option>上班</option>
	                        <option>读研</option>
                            <option>公务员</option>
                            <option>其他</option>
                        </select>
                    </td>
                </tr>
                <tr>
                	<td>婚姻：</td>
                    <td>
                    	<input type="radio" name="marrige" value="" />已婚&nbsp;
                        <input type="radio" name="marrige" value="" />未婚
                    </td>
                </tr>
                <tr>
                	<td>目前所在地：</td>
                    <td><input type="text" name="addriess" id="addriess" /></td>
                </tr>
                <tr>
                	<td valign="top">最怀念的同学:</td>
                    <td>
                    	<textarea id="" name="">
                        </textarea>
                    </td>
                </tr>
                <tr>
                	<td valign="top">最喜欢的老师：</td>
                    <td>
                    	<textarea id="" name="">
                        </textarea>
                    </td>
                </tr>
                <tr>
                	<td valign="top">大学里最难忘的一件事:</td>
                    <td>
                    	<textarea id="" name="">
                        </textarea>
                    </td>
                </tr>
                <tr>
                	<td valign="top">最想对同学们说的话：</td>
                    <td>
                    	<textarea id="" name="">
                        </textarea>
                    </td>
                </tr>
                <tr>
                	<td align="center" colspan="2">
                    	<input type="button" id="" name="" value="提交"/>
                    	<input type="button" id="" name="" value="取消" />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</body>
</html>