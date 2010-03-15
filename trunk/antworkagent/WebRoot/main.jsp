<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="application.jsp"%>
<%@ page import="com.TzTwork.awa.antworkagent.po.*" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/main.css"/>
<link rel="stylesheet" type="text/css" href="css/login.css"/>
<script type="text/javascript" src="jquery/jquery.js"></script>
<script type="text/javascript" src="js/login.js"></script>
<title>无标题文档</title>
</head>

<body>
	<div id="header"></div>
	<div>
	    <ul id="nav">
	    	<li><a href="succ.jsp">首页</a></li>
	        <li><a href="succ.jsp">导航</a></li>
	        <li><a href="succ.jsp">爱墙</a></li>
	        <li><a href="succ.jsp">帮助</a></li>
	    </ul>
    </div>
    <div id="main">
		<div id="left">
		<c:choose>
			<c:when test="${sessionScope.user == null}">
			<form action="post" name="form1">
				<input type="text" id="username" name="username" class="tp"/>
		        <input type="password" id="password" name="password" class="tp"/>
		        <input type="submit" id="button" name="" value="登录" />
		        <input type="button" id="reset" name="" value="注册" />
		    </form>
	    	</c:when>
	    	<c:otherwise>
	    		<p>欢迎${user.username}</p>
	    	</c:otherwise>
	    </c:choose>
	    </div>
	    <div id="right">
	    	<p>会员：100&nbsp;在线：900</p>
	        <p>今日新帖：200&nbsp;今日注册：300</p>
	    </div>
	</div>
	<table id="attention">
    	<tr>
        	<td>
            	<div id="atten_left">
                	<p class="atten_new">最新贴</p>
                    <ul id="atten_nav">
                    	<li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                    </ul>
                </div>
            </td>
            <td>
            	<div id="atten_center">
                	<p class="atten_new">最热帖</p>
                    <ul id="atten_nav">
                    	<li><a href="#">京哈空间的划分爱的世界哈利交</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                    </ul>
                </div>
            </td>
            <td>
            	<div id="atten_right">
                	<p class="atten_new">最多回复</p>
                    <ul id="atten_nav">
                    	<li><a href="#">大厦了解到发技术的飞机啊利交</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                        <li><a href="#">京哈肯定就上了飞机安抚绝对是</a></li>
                    </ul>
                </div>
            </td>
        </tr>
    </table>
    <br/>
    <div id="first">
    	<div id="head">&nbsp;我是菜鸟</div>
        <table id="content" border="1px" bordercolor="#B2D3F5" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
        	<tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
        </table>
    </div>
    <br/>
    <div id="first">
    	<div id="head">&nbsp;他是菜鸟</div>
        <table id="content" border="1px" bordercolor="#046DC9" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
        	<tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
        </table>
    </div>
    <br/>
    <div id="first">
    	<div id="head">&nbsp;欢迎菜鸟</div>
        <table id="content" border="1px" bordercolor="#046DC9" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
        	<tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
        </table>
    </div>
    <br/>
    <div id="first">
    	<div id="head">&nbsp;你是菜鸟</div>
        <table id="content" border="1px" bordercolor="#046DC9" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
        	<tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
            <tr>	
            	<td>结婚登记咖啡黑客基地哈空间符合的离开旧爱</td>
            </tr>
        </table>
    </div>
    <br/>
    <div style="height:37px; color:#fff;text-align:center; line-height:37px;width:770;background-color:#0365F1;bottom:0px;_bottom:-1px;">
    	Copyright @ 2009	ANTSITE, Inc. All rights reserved.
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
