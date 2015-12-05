<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()	+ path + "/";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>讲评卷—乐培生</title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="content-type" content="no-cache, must-revalidate" />
<meta http-equiv="expires" content="0" />
<link rel="shortcut icon" href="img/favicon.png">
<link rel="stylesheet" href="themes/default/easyui.css">
<link rel="stylesheet" href="themes/icon.css">
<link href="css/my-util.css" rel="stylesheet">
<link href="css/my-plugin.css" rel="stylesheet">
<link href="css/review.css" rel="stylesheet">

<script src="js/lib/es5-shim.js"></script>
<script src="js/lib/es5-sham.js"></script>
<!--[if lte IE 8]>
  <script src="js/lib/html5shiv.js"></script>
  <script src="js/lib/json3.js"></script>
  <script src="js/lib/ie-fix.js"></script>
<![endif]-->
</head>
<body class="easyui-layout" data-image-path="${imagePath }">
	<div data-options="region:'north',split:true,border:true" style="min-height: 50px;overflow: hidden;">
		<div class="title">
			<span>账号:</span><span>${loginUser.name }</span> 
			<div class="right" style="right: 10px;">
				<a href="./logout" class="easyui-linkbutton" data-options="iconCls:'icon-back'">退出</a>
			</div>
			<div class="right" style="right: 100px;">
				<a href="javascript:void(0)" id="chooseExamBtn" class="easyui-linkbutton" data-options="">考试选择</a>
			</div>
		</div>
	</div>
	<div data-options="region:'center',border:false,fit:true">
		<div id="tabs" class="easyui-tabs" data-options="fit:true">
	        <div title="小题得分表" style="padding:10px">
				<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'north',split:true,border:false" title="班级平均值" style="height: 100px;">
			            <table id="difficulty"></table>
					</div>
					<div data-options="region:'center',split:true,border:false" title="个人小题得分">
			            <table id="questionScore"></table>
					</div>
					<div data-options="region:'south',split:true,border:false" style="height: 35px;">
					</div>
				</div>
	        </div>
	        <div title="试卷图片浏览">
	        	<div class="easyui-layout" data-options="fit:true">
					<div data-options="region:'east',split:true,border:false" style="width: 300px;">
						<div class="easyui-layout" data-options="fit:true">
							<div id="chooseQuestion" data-options="region:'north',split:true,border:false" title="选择题号" style="height: 70px;">
								<input id="question">
							</div>
							<div data-options="region:'center',split:true,border:false" title="选择学生">
								<table id="student"></table>
							</div>
							<div data-options="region:'south',split:true,border:false" style="height: 50px;">
							</div>
						</div>
					</div>
					<div data-options="region:'center',split:true,border:false">
						<div class="easyui-layout" data-options="fit:true">
							<div data-options="region:'north',split:true,border:false" style="height: 60px;min-height: 50px;line-height: 50px;overflow: hidden;">
								<div class="title2">
									<div class="left" style="left: 20px;">
										<input id="picSlider" style="width: 100px;" />
									</div>
									<div class="left operaterBtn" style="left: 140px;top: 22px;">
										<a href="javascript:void(0)" class="easyui-linkbutton" id="questionPaperBtn"> 题 干 </a>
										<a href="javascript:void(0)" class="easyui-linkbutton" id="drawBtn"> &nbsp;绘&nbsp; </a>
										<a href="javascript:void(0)" class="easyui-linkbutton" id="clearDrawBtn"> &nbsp;清&nbsp; </a>
										<a href="javascript:void(0)" class="easyui-linkbutton" id="saveImage"> 保 存 </a>
										<a href="javascript:void(0)" class="easyui-linkbutton" id="styleSet"> 设 置 </a>
									</div>
								</div>
								<div>&nbsp;</div>
							</div>
							<div id="center" data-options="region:'center',split:true,border:false">
							</div>
							<div data-options="region:'south',split:true,border:false" style="height: 50px;">
							</div>
						</div>
					</div>
				</div>
	        </div>
	    </div>
	</div>
	<div id="chooseExamWin" class="easyui-window" title="请选择" data-options="modal:true,closed:true" 
		style="width: 420px; height: 350px; padding: 10px;">
		<table>
			<tr>
				<td style="min-width: 60px;">考试批次</td>
				<td><input id="batch"></td>
			</tr>
			<tr style="z-index: 1;">
				<td>考试学校</td>
				<td id="unitTab" style="position: relative;z-index: 1">
					<input id="school" type="text">
					<ul class="easyui-tree unit-tree hide" id="unitTree"></ul>
				</td>
			</tr>
			<tr>
				<td>考试科目</td>
				<td><input id="subject"></td>
			</tr>
			<tr>
				<td>考试班级</td>
				<td><input type="checkbox" id="checkAll" checked="checked">全选</td>
			</tr>
		</table>
		<div class="classes"></div>
		<a href="javascript:void(0);" class="easyui-linkbutton" id="submit">确&nbsp;&nbsp;定</a>
	</div>
	<div id="imageWin" class="easyui-window" data-options="modal: false, closed: true,collapsible: false"
		style="width: 800px; height: 600px; text-align: center;" >
	</div>
	<div id="styleWin" class="easyui-window" data-options="modal: false, closed: true,collapsible: false"
		style="width: 370px; height: 260px; text-align: center;" title="设置线条样式">
		<table>
			<tr>
				<td style="min-width: 60px;">线条颜色</td>
				<td>
					<div id="canvasColours">
					    <div data-colour="red" style="background-color: red;"></div>
					    <div data-colour="orange" style="background-color: orange;"></div>
					    <div data-colour="yellow" style="background-color: yellow;"></div>
					    <div data-colour="green" style="background-color: green;"></div>
					    <div data-colour="blue" style="background-color: blue;"></div>
					    <div data-colour="indigo" style="background-color: rgb(75, 0, 130);"></div>
					    <div data-colour="violet" style="background-color: rgb(238, 130, 238);"></div>
					    <div data-colour="white" style="background-color: white;"></div>
					    <div data-colour="black" style="background-color: black;"></div>
					</div>
				</td>
			</tr>
			<tr>
				<td>线条大小</td>
				<td class="widthSlider">
					<input id="canvasWidth" style="width: 200px;" />
				</td>
			</tr>
			<tr>
				<td>预览</td>
				<td>
					<div id="preview" style="width: 200px;margin-top: 10px;"></div>
				</td>
			</tr>
		</table>
	</div>
	<canvas id="myCanvas" style="display: none;"></canvas>
	<img id="myImg" src="">
	<script src="js/lib/jquery.js"></script>
	<script src="js/lib/require.js"></script>
	<script src="js/lib/jquery.easyui.min.js"></script>
	<script src="js/lib/datagrid-detailview.js"></script>
	<script src="js/lib/jquery.scrollTo.js"></script>
	<script src="js/lib/my-util.js"></script>
	<script src="js/lib/myPlugin.js"></script>
	<script src="js/review/draw.js"></script>
	<script src="js/review/review.js"></script>
</body>
</html>