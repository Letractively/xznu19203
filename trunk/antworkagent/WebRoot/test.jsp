<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/index.css" />
<script type="text/javascript" type="jquery/jquery.js"></script>
<title>无标题文档</title>
<script>
	function getXMLHTTPRequest(){  
	
	     if (XMLHttpRequest)    {  
	         return new XMLHttpRequest();  
	     } else {  
	         try{  
	             return new ActiveXObject('Msxml2.XMLHTTP');  
	         }catch(e){  
	             return new ActiveXObject('Microsoft.XMLHTTP');  
	         }  
	     }  
	 }  
	 var req = getXMLHTTPRequest();  
	 req.open('DELETE','http://localhost/test.jsp',false);  
	 req.send(null);  
	 document.write(req.responseText); 
</script>
</head>
<body>
	<form method="delete">
		<input type="text" id="" name="" />
		<input type="button" id="but" name="" value="post"/>
	</form>
</body>
</html>
