<%
	request.setCharacterEncoding("UTF-8");
%>
<%@ page contentType="text/html; charset=utf-8" isErrorPage="true" language="java"%>
<%@ include file="application.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${contextPath }/style/page.css" />
<link rel="stylesheet" type="text/css" href="${contextPath }/style/table.css" />
<script src="js/jquery-1[1].3.2.min.js" type="text/javascript"></script>
<title>${systemName }</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
<form name="form1" method="post">
<input type="hidden" name="url" value="${url}"/>
<div class="container">
<jsp:include page="top.jsp" flush="false"></jsp:include>
  <div class="pagebody">
    <div class="mainbody">
      <div class="content">
         <div style="width:100%; float:left; background:#666666; height:30px;"><ul style="float:left; width:99%; padding-left:6px;"><li style="float:left; height:26px; margin-top:2px;line-height:26px; padding:0 6px; color:#FFFFFF;">错误信息</li></ul></div>
        <div class="maincon">
			对不起，出错了.... 	<a href="index.jsp"><font color="red">请点此处</font></a>转至首页 	
        </div>
      </div>
    </div>
  </div>
</div>
</form>
</body>
</html>