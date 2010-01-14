<%@ page language="java" pageEncoding="GBK"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>ÒÏ×åÀ´°É</title>
    <script>
    	function on(data){
			if(data=="0"){
				document.getElementById("menu").style.display="none";
				document.getElementById("main").style.width="100%";
				document.getElementById("left").style.display="none";
				document.getElementById("right").style.display="";
				}
			if(data=="1"){
				document.getElementById("menu").style.display="";
				document.getElementById("main").style.width="85%";
				document.getElementById("left").style.display="";
				document.getElementById("right").style.display="none";
				}
        	}
    </script>
  </head>
  <body>
  	<form name="form1" id="form1">
	  	<table width="100%" height="100%" border="0">
	  		<tr>
	  			<td id="menu" style="width: 15%;display: ">
	  				<iframe frameborder="0" src="tree.jsp" width="100%" height="100%" scrolling="auto" style="border: 0"></iframe>
	  			</td>
	  			<td id="left"><img src="images/left.gif" onclick="on(0)"></img></td>
	  			<td id="right" style="display: none"><img src="images/right.gif" onclick="on(1)"></img></td>
	  			<td id="main" style="width: 85%;display: ">
	  				<iframe frameborder="0" src="main.jsp" width="100%" height="100%" scrolling="auto" style="border: 0"></iframe>
	  			</td>
	  		</tr>
	  	</table>
  	</form>
  </body>
</html>
