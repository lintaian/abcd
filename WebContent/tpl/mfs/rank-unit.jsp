<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
  <base href="<%=basePath%>">
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/bootstrap-responsive.min.css" rel="stylesheet">
  <link href="css/child.css" rel="stylesheet">
  <link href="css/my-util.css" rel="stylesheet">
  <script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body data-unit-code="${obj.unitCode }"	data-exam-code="${obj.examCode }" 
	data-group-code="${obj.groupCode }" data-exam-batch="${obj.examBatch }"
	data-name="${obj.name }" data-unit-type="0">
	<div class="operator">
		<a href="javascript:history.back(-1);">&lt;&lt;返回</a>
		<button class="sort btn desc in-line">高→低排序</button>
		<button class="sort btn asc in-line">低→高排序</button>
	</div>
	<div id="main" style="height:500px;border:1px solid #ccc;padding:10px;width: 98%;"></div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/echarts.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/mfs/util.js"></script>
	<script src="js/mfs/rank.js"></script>
</body>
</html>