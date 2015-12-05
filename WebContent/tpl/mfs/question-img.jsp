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
  <link rel="stylesheet" type="text/css" href="css/pxs.css" />
  <link href="css/my-util.css" rel="stylesheet">
  <style type="text/css">
  	.pxs_thumbnails div {
  		position: absolute;
  		left: 5px;
  		top: 2px;
  		font-weight: bold;
  		cursor: pointer;
  	}
  	.pxs_thumbnails div .question-score {
  		color: red;
  	}
  	.pxs_thumbnails div .question-title {
  		color: blue;
  	}
  </style>
  <script src="js/lib/pxs/move.js" type="text/javascript"></script>
  <script src="js/lib/pxs/index.js" type="text/javascript"></script>
</head>
<body data-exam-code="${obj.examCode }" data-exam-no="${obj.examNo }">
	<div id="pxs_container" class="pxs_container">
        <div class="pxs_bg">
            <div class="pxs_bg1"></div>
            <div class="pxs_bg2"></div>
            <div class="pxs_bg3"></div>
        </div>
        <div class="pxs_loading">Loading images...</div>
        <div class="pxs_slider_wrapper">
            <ul class="pxs_slider">
	        	<c:forEach items="${obj.questions }" var="q">
    	            <li><img src="${imagePath }?examNo=${obj.examNo }&examCode=${obj.examCode}&title=${q.questionTitle}" 
    	            	data-question-title="${q.questionTitle }"/></li>
        		</c:forEach>
            </ul>
            <div class="pxs_navigation">
                <span class="pxs_next"></span>
                <span class="pxs_prev"></span>
            </div>
            <ul class="pxs_thumbnails">
                <c:forEach items="${obj.questions }" var="q">
    	            <li>
    	            	<img src="${imagePath }?examNo=${obj.examNo }&examCode=${obj.examCode}&title=${q.questionTitle}"/>
    	            	<div>
    	            		<span class="question-score">${obj.scores[q.questionTitle] }/${q.questionPoint}</span><br/>
    	            		<span class="question-title">${q.questionTitle}</span>
    	            	</div>	
    	            </li>
        		</c:forEach>
            </ul>
        </div>
    </div>
	<script>
		var oLoad = getByClass(document.body, 'pxs_loading')[0];
		var oImgBox = getByClass(document.body,'pxs_slider_wrapper')[0];
		//var oEvent=ev||event;
		//var obj=oEvent.srcElement||oEvent.target;
		var imgs = document.getElementsByTagName('img');
		for(var i=0;i<imgs.length;i++)
		{
		  imgs[i].onload = function()
		  {
		    oLoad.style.display = 'none';
		  }
		  oImgBox.style.display = 'block';
		}
	</script>    
	<script src="js/lib/jquery.js" type="text/javascript"></script>
  	<script src="js/lib/my-util.js" type="text/javascript"></script>
  	<script src="js/mfs/question-image.js" type="text/javascript"></script>
</body>
</html>