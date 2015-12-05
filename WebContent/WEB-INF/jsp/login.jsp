<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>乐培生数据展示平台</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="shortcut icon" href="img/favicon.png">
<!-- Le styles -->
<link href="css/zTreeStyle.css" rel="stylesheet">
<link href="css/my-util.css" rel="stylesheet">
<link href="css/my-plugin.css" rel="stylesheet">
<link href="css/login.css" rel="stylesheet">
<script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body style="zoom: 1;text-align: center;position: relative;">
	<h1>乐培生师生成长平台<sup id="year">2015</sup></h1>
	<div class="login" style="margin-top:50px;">
	    <div class="header">
	        <div class="switch" id="switch">
	            <a class="switch_btn_focus" id="switch_qlogin" href="javascript:void(0);" tabindex="7">普通登录</a>
	            <a class="switch_btn" id="switch_login" href="javascript:void(0);" tabindex="8">快速登录</a>
	            <div class="switch_bottom" id="switch_bottom" style="position: absolute; width: 64px; left: 0px;"></div>
	        </div>
	    </div>
	    <!--登录-->
	    <div class="web_qr_login" id="web_qr_login" style="display: block; height: 290px;">
	        <div class="web_login" id="web_login">
	            <div class="login-box">
	                <div class="login_form">
	                    <form id="loginform" accept-charset="utf-8" id="login_form" class="loginForm" method="post">
	                        <div class="uinArea" id="uintArea" style="z-index: 9999">
	                            <label class="input-tips" for="u">机构：</label>
	                            <div class="inputOuter" id="unitTab" style="position: relative;">
	                                <input type="text" id="unit" placeholder="机构" class="inputstyle">
	                                <ul class="ztree unit-tree hide" id="unitTree"></ul>
	                            </div>
	                        </div>
	                        <div class="uinArea" id="uinArea">
	                            <label class="input-tips" for="u">帐号：</label>
	                            <div class="inputOuter" id="uArea">
	                                <input type="text" id="name" placeholder="用户名" class="inputstyle">
	                            </div>
	                        </div>
	                        <div class="pwdArea" id="pwdArea">
	                            <label class="input-tips" for="p">密码：</label>
	                            <div class="inputOuter" id="pArea">
	                                <input type="password" id="password" placeholder="密码" class="inputstyle">
	                            </div>
	                        </div>
	                        <div style="padding-left:50px;margin-top:20px;">
	                            <input type="submit" value="登 录" style="width:150px;" class="button_blue">
	                        </div>
	                    </form>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
	<div class="jianyi">推荐使用IE浏览器8+版本或Chrome内核浏览器访问</div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/jquery.ztree.all-3.5.min.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/lib/myPlugin.js"></script>
	<script src="js/custom/login.js"></script>
	<script>
		document.getElementById('year').innerHTML = new Date().getFullYear();
    </script>
</body>
</html>