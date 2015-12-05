<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>" />
<meta charset="utf-8">
<title>乐培生教学质量监测反馈系统</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">  
<meta name="apple-mobile-web-app-capable" content="yes">  
<meta name="apple-mobile-web-app-status-bar-style" content="black"> 
<meta name="description" content="">
<meta name="author" content="">
<meta http-equiv="pragma" content="no-cache"  />
<meta http-equiv="content-type" content="no-cache, must-revalidate" />
<meta http-equiv="expires" content="0"/>
<link rel="shortcut icon" href="img/favicon.png">
<!-- Le styles -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
<link href="css/my-util.css" rel="stylesheet">
<link href="css/update-pwd.css" rel="stylesheet">

<script src="js/lib/jquery.js"></script>
<script src="js/lib/jquery.ztree.all-3.5.min.js"></script>
<script src="js/lib/bootstrap.min.js"></script>
<script src="js/lib/my-util.js"></script>
<script src="js/custom/update-pwd.js"></script>
<script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body>
	<form class="form-signin">
		<div class="up-title">修改密码</div>
		<input type="password" class="input-block-level" value="" id="oldPwd" placeholder="旧密码"/>
		<input type="password" class="input-block-level" value="" id="newPwd" placeholder="新密码"/>
		<input type="password" class="input-block-level" value="" id="newPwd2" placeholder="确认密码"/>
		<div id="msg"></div>
		<button class="btn btn-large btn-primary" name="ok" type="submit">确 认 修 改</button>
	</form>
</body>
</html>