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
<title>乐培生教育</title><meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="shortcut icon" href="img/favicon.png">
<!-- Le styles -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
<script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
<style type="text/css">
  .like_a {
    color: #337ab7;
  }
  a:hover,a:visited,a:active,a:link {
    text-decoration: none;
  }
  .module {
    width: 24%;
  	float: left;
  	margin: 10px 0.5%;
  	min-width: 280px;
  }
  .row {
  	margin: 0!important;
  }
</style>
</head>
<body class="home-template" style="text-align: center;">
  <div class="container projects" style="text-align: center;">
    <div class="projects-header page-header">
      <h2>乐培生师生成长平台<sup id="year">2015</sup></h2>
    </div>
    <div class="row" style="text-align: center;">
      <c:forEach items="${loginUser.modules}" var="m">
	      <div class="module">
	        <a href="${m.url }/${loginUser.unitCode}" target="_blank">
	          <div class="thumbnail" style="height: 334px;">
	            <img class="lazy" src="${m.img }" width="300" height="150">
	            <div class="caption">
	              <h3>
	                <p class="like_a">${m.name }<br/></p>
	              </h3>
	              <p>${m.desc }</p>
	            </div>
	          </div>
	        </a>
	      </div>
      </c:forEach>
    </div>
  </div>
  <!-- static home page -->
  <a id="scrollUp" href="http://www.bootcss.com/#top" style="position: fixed; z-index: 2147483647; display: none;">
    <i class="fa fa-angle-up"></i>
  </a>
  <script>
	document.getElementById('year').innerHTML = new Date().getFullYear();
  </script>
</body>
</html>