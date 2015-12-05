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
<body data-student-id="${obj.studentId }" data-exam-code="${obj.examCode }" 
	data-exam-group-code="${obj.examGroupCode }" data-exam-batch="${obj.examBatch }">
	<!-- <div class="operator">
		<a href="javascript:void(0);" data-close-frame>×关闭</a>
	</div> -->
	<div class="goal-analysis ${(obj.examCode != null && obj.examCode != '') || 
		(obj.examGroupCode != '102' && obj.examGroupCode != '103' && obj.examGroupCode != '106') ? 'hide' : '' }">
		<!-- <input type="text" class="input-small search-query" placeholder="目标系数"/> -->
		<div class="btn-group">
			<button class="btn goal-rate">目标系数</button>
			<button class="btn dropdown-toggle" data-toggle="dropdown">
				<span class="caret"></span>
			</button>
			<ul class="dropdown-menu goal-rate-ul" style="min-width: 105px;"></ul>
		</div>
		<button class="btn goal-confirm">目标分析</button>
	</div>
	<div class="direction-check">
		<c:if test="${obj.backable == 1 }">
			<a href="javascript:history.back(-1);">&lt;&lt;返回</a>
		</c:if>
		<button class="btn">查看纵向数据</button>
	</div>
	<div class="summary"></div>
	<div id="main" style="height:540px;margin: 10px;width: 780px;position: fixed;top: 10px;right: 10px;"></div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/echarts.js"></script>
	<script src="js/lib/bootstrap.min.js"></script>
	<script src="js/mfs/util.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/mfs/chart-student.js"></script>
</body>
</html>