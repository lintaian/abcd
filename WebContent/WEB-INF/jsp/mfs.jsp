<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<link href="css/zTreeStyle.css" rel="stylesheet">
<link href="css/my-util.css" rel="stylesheet">
<link href="css/mfs.css" rel="stylesheet">

<script src="js/lib/jquery.js"></script>
<script src="js/lib/jquery.ztree.all-3.5.min.js"></script>
<script src="js/lib/bootstrap.min.js"></script>
<script src="js/lib/my-util.js"></script>
<script src="js/mfs/util.js"></script>
<script src="js/mfs/mfs.js"></script>
<script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body data-unit-code="${obj.unitCode }" data-top>
	<div class="title">
		<div class="title-bd">
			<div class="title-text"><img class="logo" src="img/logo.png">乐培生教学质量监测反馈系统</div>
			<div class="logout">
				<a href="logout">退出</a>
				<!-- <div class="dropdown">
				  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
				    	设置
				    <span class="caret" style="margin-left: 5px"></span>
				  </button>
				  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1" style="min-width: 100px">
				    <li><a href="javascript:void(0)" class="update-pwd">修改密码</a></li>
				    <li><a href="logout">退出登陆</a></li>
				  </ul>
				</div> -->
			</div>
			<div class="exam-choose">
				<div class="btn-group">
					<button class="btn exam-batch">请选择</button>
					<button class="btn dropdown-toggle" data-toggle="dropdown">
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu exam-batch-ul"></ul>
				</div>
				<div class="btn-group">
					<button class="btn unit">请选择</button>
					<button class="btn dropdown-toggle unit-caret" >
						<span class="caret"></span>
					</button>
					<ul class="ztree unit-tree" id="unitTree"></ul>
				</div>
				<div class="btn-group">
					<button class="btn unit-type">请选择</button>
					<button class="btn dropdown-toggle" data-toggle="dropdown">
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu unit-type-ul"></ul>
				</div>
				<div class="btn-group">
					<button class="btn exam">请选择</button>
					<button class="btn dropdown-toggle" data-toggle="dropdown">
						<span class="caret"></span>
					</button>
					<ul class="dropdown-menu exam-ul">
						<li class="divider"></li>
					</ul>
				</div>
				<button class="btn query">确 定</button>
			</div>
		</div>
	</div>
	<div class="main">
		<iframe src="tpl/mfs/welcome.jsp" id="frame" width="100%" height="600" scrolling="false" frameborder="0"></iframe>
	</div>
	<div id="help"><a href="helper.html" target="_blank">操作帮助</a></div>
   	<div id="search" class="hide">
       <form id="searchForm">
           <input type="search" class="examNo" placeholder="输入考号后回车" title="输入考号后回车"/>
       </form>
   	</div>
</body>
</html>