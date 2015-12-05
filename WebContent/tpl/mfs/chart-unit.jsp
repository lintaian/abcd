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
<body data-unit-code="${obj.unitCode }" data-unit-type="${obj.unitType }" 
	data-exam-code="${obj.examCode }" data-exam-group-code="${obj.examGroupCode }"
	data-show-student="${obj.showStudent }" data-exam-batch="${obj.examBatch }"
	data-my-unit-code="${obj.myUnitCode }" data-unit-type-name="${obj.unitTypeName }">
	<div class="operator">
		<c:if test="${obj.unitType == 8000 }">
			<a href="javascript:void(0);" id="back" class="chartUnitBack">&lt;&lt;返回</a>
		</c:if>
		<c:if test="${obj.unitType != 8000 }">
			<a href="javascript:history.back(-1);">&lt;&lt;返回</a>
		</c:if>
		<button class="sort btn desc in-line">高→低排序</button>
		<button class="sort btn asc in-line">低→高排序</button>
		<button class="btn direction in-line">查看纵向数据</button>
		<c:if test="${obj.unitType != 8000 }">
			<div class="btn-group in-line draw-type" >
				<button class="btn draw-type-txt">划线方式</button>
				<button class="btn dropdown-toggle" data-toggle="dropdown">
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li data-draw-type="score"><a href="javascript:void(0);">按分数</a></li>
					<!-- <li data-draw-type="scorePercent"><a href="javascript:void(0);">按分数百分比</a></li> -->
					<li data-draw-type="person"><a href="javascript:void(0);">按人数</a></li>
					<li data-draw-type="personPercent"><a href="javascript:void(0);">按人数百分比</a></li>
				</ul>
			</div>
			<div class="input-append in-line draw">
			  <input class="span2" type="text">
			  <span class="add-on"></span>
			  <button class="btn draw-add" type="button"><i class="icon icon-plus"></i></button>
			  <button class="btn draw-ok" type="button">确定划线</button>
			</div>
		</c:if>
	</div>
	<div id="main" style="height:500px;border:1px solid #ccc;padding:10px;width: 98%;"></div>
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/echarts.js"></script>
	<script src="js/lib/bootstrap.min.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/mfs/util.js"></script>
	<script src="js/mfs/chart-unit.js"></script>
</body>
</html>