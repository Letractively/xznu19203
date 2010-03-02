<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="application.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>无标题文档</title>
<style>
	body{
		font-family:Arial, Helvetica, sans-serif;
		font-size:12px;
		margin:0px auto;
		height:auto;
		width:770px;
		}
	#header{
		height:260px;
		width:770px;
		background-image:url(myimage/header.jpg);
		background-repeat:no-repeat;
		margin:0px 0px 3px 0px
		}
	#nav{
		height:25px;
		width:770px;
		font-size:14px;
		list-style-type:none
		}
	#nav li{
		float:left;
		}
	#nav li a{
		color:#000000;
		text-decoration:none;
		padding-top:4px;
		display:block;
		width:97px;
		height:22px;
		text-align:center;
	 	background-color:#009966;
		margin-left:2px
		}
	#nav li a:hover{
		background-color:#006633;
		color:#ffffff
		}
	#main{
		width:770;
		height:auto;
		background-color:#9FF;
		margin-top:10px;
		padding-left:40px;
		}
	#left{
		float:left;
		}
	#right{
		float:right;
		}
		
	#attention{
		width:770px;
		height:auto;
		border:1px solid #00F;
		}
	#attention td{
		border:1px solid #00f;
		width:256px;
		}
	#atten_nav{
		margin-top:5px;
		list-style-type:none
		}
	#atten_nav li{
		color:#00F;
		}
	.atten_new{
		color:#90F;
		text-align:center;
		font-family:Verdana, Geneva, sans-serif;
		background-color:#CFC;
		font-size:16px;
		font-style:normal;
		font-weight:bolder;
		margin:0px 0px 0px 0px;
		}
	#atten_nav li a:link{
		color:#FF0000;
		text-decoration:none;
		}
	#atten_nav li a:visited{
		color:#00FF00;
		text-decoration:none
		}
	#atten_nav li a:hover {
		color:#000000;
		text-decoration:none;
    }
    #atten_nav li a:active {
		color:#FFFFFF;
		text-decoration:none;
    }
    
    #first{
		border:1px solid #36F;
		}
	#head{
		font-family:Verdana, Geneva, sans-serif;
		font-weight:bold;
		font-size:20px;
		color:#EEE;
		width:769px;
		height:30px;
		background-color:#36F;
		}
	#content{
		font-family:Georgia, "Times New Roman", Times, serif;
		font-weight:lighter;
		font-size:12px;
		}
	#content td{
		padding-left:6px;
		height:28px;
		color:#006633;
	}
</style>
</head>

<body>
	<div id="header"></div>
    <ul id="nav">
    	<li><a href="#">首页</a></li>
        <li><a href="#">导航</a></li>
        <li><a href="#">爱墙</a></li>
        <li><a href="#">帮助</a></li>
    </ul>
    <div id="main">
		<div id="left">
	    	<input type="text" id="" name=""/>
	        <input type="text" id="" name=""/>
	        <input type="button" id="" name="" value="登录" />
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
        <table id="content" border="1px" bordercolor="#36F" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
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
    	<div id="head">&nbsp;我是菜鸟</div>
        <table id="content" border="1px" bordercolor="#36F" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
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
    	<div id="head">&nbsp;我是菜鸟</div>
        <table id="content" border="1px" bordercolor="#36F" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
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
    <br>
    <div id="first">
    	<div id="head">&nbsp;我是菜鸟</div>
        <table id="content" border="1px" bordercolor="#36F" cellpadding="0" cellspacing="0" width="100%" style="border-collapse:collapse;" frame="void">
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
</body>
</html>
