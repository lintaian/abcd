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
  <link href="css/child-dialog.css" rel="stylesheet">
  <link href="css/my-util.css" rel="stylesheet">
  <script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body data-unit-code="${obj.unitCode }" data-exam-code="${obj.examCode }" 
	data-exam-group-code="${obj.examGroupCode }" data-exam-batch="${obj.examBatch }"
	data-is-student="true">
	<a href="javascript:history.back(-1);" class="back">&lt;&lt;返回</a>
	<!-- <div class="summary"></div> -->
	<div id="main" style="height:540px;margin: 10px;width: 980px;position: fixed;top: 10px;right: 10px;"></div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/echarts.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/mfs/util.js"></script>
	<script src="js/mfs/direction.js"></script>
</body>
</html>