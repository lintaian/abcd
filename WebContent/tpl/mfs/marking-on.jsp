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
<body data-type="${obj.type }" data-value="${obj.value }" 
	data-unit-code="${obj.unitCode }" data-unit-type-name="${obj.unitTypeName }"
	data-child-type-code="${obj.childTypeCode }" data-batch-code="${obj.batchCode }"
	data-exam-name="${obj.examName }">
	<div class="operator">
		<a href="javascript:history.back(-1);">&lt;&lt;返回</a>
		<button class="sort btn desc in-line">高→低排序</button>
		<button class="sort btn asc in-line">低→高排序</button>
	</div>
	<div id="main" style="height:500px;border:1px solid #ccc;padding:10px;width: 98%;"></div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/echarts.js"></script>
	<script src="js/lib/bootstrap.min.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/mfs/util.js"></script>
	<script src="js/mfs/marking-on.js"></script>
</body>
</html>