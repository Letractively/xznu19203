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
				beforeSend:function(){
					new screenClass().lock();
				},
				success:function(data){
					if(data!=null && data!=""){
						alert(data);
						new screenClass().unlock();
						}
					}
				});
			return false;
			});
		});
    var screenClass = function()
    {
        /// 解锁
        this.unlock = function()
        {
            var divLock = document.getElementById("divLock");
            if(divLock == null) return;
            document.body.removeChild(divLock);
        };
        
        /// 锁屏
        this.lock = function()
        {
            var sWidth,sHeight;
            var imgPath = "myimage/bgcolor.gif";
            sWidth  = screen.width - 20;
            sHeight = screen.height- 170;
            
            var bgObj=document.createElement("div");
            bgObj.setAttribute("id","divLock");
            bgObj.style.position="absolute";
            bgObj.style.top="0";
            bgObj.style.background="#cccccc";
            bgObj.style.filter="progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75";
            bgObj.style.opacity="0.6";
            bgObj.style.left="0";
            bgObj.style.width=sWidth + "px";
            bgObj.style.height=sHeight + "px";
            bgObj.style.zIndex = "100";
            document.body.appendChild(bgObj);
            var html = "<table border=\"0\" width=\""+sWidth+"\" height=\""+sHeight+"\"><tr><td valign=\"middle\" align=\"center\"><image src=\""+imgPath+"\"></td></tr></table>";
            bgObj.innerHTML = html;
            // 解锁
            bgObj.onclick = function()
            {
                 //new screenClass().unlock();
            }
        };
    }
</script>
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
                	<td colspan="2" align="right"><p><a href="#">会员注册</a> <a href="main.jsp">游客浏览</a></p></td>
                </tr>
            </table>
        </form>
        </div>
    </div>
</body>
</html>
