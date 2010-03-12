<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/index.css" />
<script type="text/javascript" src="jquery/jquery.js"></script>
<title>无标题文档</title>
<script>
	$(document).ready(function(){
		$('#button').click(function(){
			$.ajax({
				url:"loginAction.antwork?method=loginValidate",
				data:"username="+$('#username').val()+"&password="+$('#password').val(),
				type:"POST",
				beforeSend:function(){},
				success:function(data){
					if(data==1){
						$('#msg').html("正在登陆...");
						lock();
						setTimeout("to()",3000);
						}
					if(data==0){
						$('#msg').html("用户名或密码错误！");
						lock();
						}
					}
				});
			return false;
			});
		$('#close').click(function(){
			unlock();
			});
		});
	function lock(){
		 $(".backgroundDiv").css({"opacity":"0.5"}).fadeIn('normal');		
		 var scrollWidth = document.documentElement.clientWidth;
		 var scrollHeight = document.documentElement.clientHeight;
		 var divWidth = $(".info").width();
		 var divHeight = $(".info").height();
		 var divLeft = scrollWidth/2-divWidth/2;
		 var divTop = scrollHeight/2-divHeight;
		 $(".info").css({"position":"absolute","top":divTop,"left":divLeft}).fadeIn('normal');
		}
	function unlock(){
		$(".info").fadeOut('normal');
		$(".backgroundDiv").fadeOut('normal');
		$('#username').val("");
		$('#password').val("");
		}
	function to(){
		document.location.href="loginAction.antwork?method=login";
		}
</script>

<style>
  .backgroundDiv{ 
  	width:100%; 
  	height:100%; 
  	display:none; 
  	z-index:10; 
  	background-color:#333333; 
  	position:absolute; 
  	top:0px; 
  	left:0px;
  }
  .info{ 
	display:none;
	z-index:10000;
	background-color:#333;
	width:375px;	
	min-height:150px;
	border:1px solid #666;
	padding:0px;
	/* CSS3 styling for latest browsers */
	-moz-box-shadow:0 0 90px 5px #000;
	-webkit-box-shadow: 0 0 90px #000;
	}
  #close{ 
	background-image:url(myimage/close.png);
	position:absolute;
	right:-15px;
	top:-15px;
	cursor:pointer;
	height:35px;
	width:35px;
	}
	
   #alt{
   	position:absolute;
	top:50px;
	right:15px;
	font-size:12px;
	color:#FFFFFF;
	width:150px;
   }
   #msg{
	position:absolute;
	top:80px;
	right:15px;
	font-size:15px;
	color:#FFFFFF;
	width:150px;
	}
</style>
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
                	<td colspan="2" align="right"><p><a href="#">会员注册</a> <a href="loginAction.antwork?method=visitor_login">游客浏览</a></p></td>
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
