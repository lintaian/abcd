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
  <style type="text/css">
       .helper p {
           line-height: 26px;
           margin: 5px 0;
       }
       div.helper {
           margin: 100px auto;
           width: 900px;
           color: green;
       }
       .helper .title {
           font-size: 20px;
           font-weight: bold;
       }
       .helper .welcome {
           margin: 10px 0;
       }
   </style>
</head>
<body>
   <div class="helper">
       <div class="welcome">欢迎使用乐培生教学质量监测反馈系统，为了更好的使用本系统，请阅读以下操作帮助。</div>
       <div class="title">操作帮助:</div>
       <p style="color: blue">特别提示：为了更好的显示效果，请将屏幕分辨率调至 1280 * 1080</p>
       <p>1.请用Chrome 浏览器【Chrome 浏览器下载地址：<a href="http://rj.baidu.com/soft/detail/14744.html?ald" target="_blank">http://rj.baidu.com/soft/detail/14744.html?ald</a>】，（360百度搜狗等浏览器极速模式亦可）。</p>
       <p>2.登陆后，右上可以查询选择显示对象和属性。单击柱形图进入对应下一级，直到班级层次，学校和班级层次显示当前数量。</p>
       <p>3.双击柱形图主图任意色块，则只显示该色块，仅此时排序按钮可用（折线图也支持排序功能），再次双击复原为多色块。</p>
       <p>4.多色块同时显示，点击右上任意图例可以隐去对应色块，再次点击复原。</p>
       <p>5.返回按钮可返到上一级层次。</p>
       <p>6.右侧属性按钮可以添加和取消辅助线，可以转换折线图，可以保存图片，可以数据视图和刷新。</p>
       <p>7.单击学生个人成绩对应色块可弹出学生个人评价页面雷达图，可查看学生个人纵向数据和作答图片（点击雷达图节点查看,如果不能弹出新页面，请到浏览器地址栏修改设置为“允许”弹出新页面）；点击雷达图方形页面以外区域可以退出当前雷达图。</p>
       <p>8.如果看不到学生答题卡图片，很可能是学校或区域未提供图片或图片未上载所致。</p>
   </div>
</body>
</html>