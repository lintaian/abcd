$(function() {
	var base = $('base').attr('href');
   $('body').on('click', 'a[data-exam-batch]', function() {
       $('.exam-batch').text($(this).text());
       $('.exam-batch').data('examBatch', $(this).data('examBatch'));
       setUnit($(this).data('examBatch') + '');
   });
   $('body').on('click', 'a[data-exam]', function() {
       $('.exam').text($(this).text());
       $('.exam').data('examCode', $(this).data('exam'));
       $('.exam').data('examGroupCode', '');
   });
   $('body').on('click', 'a[data-exam-group]', function() {
       $('.exam').text($(this).text());
       $('.exam').data('examGroupCode', $(this).data('examGroup'));
       $('.exam').data('examCode', '');
   });
   $('body').on('click', 'a[data-unit-type]', function() {
       var name = $(this).text();
       $('.unit-type').text(name);
       $('.unit-type').data('unitType', $(this).data('unitType'));
   });
   $('body').on('click', '.query', function() {
       var unitType = $('.unit-type').data('unitType');
       var aa = $('.unit-type').data('unitCode');
       var isSpacial = aa != null && aa != '' &&  unitType == $('.unit-type-ul li:first a').data('unitType');
       var unitCode = isSpacial ? aa :  $('.unit').data('unitCode');
       var examCode = $('.exam').data('examCode');
       var examGroupCode = $('.exam').data('examGroupCode');
       var examBatch = $('.exam-batch').data('examBatch');
       $('#frame').attr('src', 'chart/unit?unitCode=' + unitCode + '&childTypeCode=' + unitType 
               + '&examCode=' + examCode + '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch
               + '&myUnitCode=' + (isSpacial ? $('.unit').data('unitCode') : ''));
   });
   $('body').on('click', '.unit-caret', function(e) {
       e.stopPropagation();
       $('.unit-tree').toggle();
   });
   $('body').on('click', function() {
       $('.unit-tree').hide();
   });
   $('body').on('click', '.unit-tree', function(e) {
       e.stopPropagation();
   });
   $('body').on('submit', '#searchForm', function(e) {
       e.stopPropagation();
       var examNo = $('#searchForm .examNo').val();
       if (examNo != '') {
           var examBatch = $('.exam-batch').data('examBatch');
           $.myUtil.ajax({
               url: 'search',
               type: 'post',
               dataType: 'json',
               data: {
                   examBatch: examBatch,
                   examNo: examNo
               },
               success: function(data) {
                   if (data.suc) {
                       var src = base + 'chart/student?classCode=' + data.classCode + '&studentId=' + data.studentId 
                           + '&examCode=' + '&examGroupCode=' + data.examGroupCode + '&examBatch=' + examBatch;
                       $.myUtil.dialogFrame.show(src, {modal: true});
                   } else {
                       $.myUtil.alert.show('错误提示', data.msg, {modal: true});
                   }
               }
           });
       }
       return false;
   });
   
   setExamBatch();
   
   function setUnitType(unitCode) {
       if (unitCode == '') {
           $('.unit-type-ul').html('');
           $('.unit-type').text('请选择');
           $('.unit-type').data('unitType', '');
       } else {
           var examBatchCode = $('.exam-batch').data('examBatch');
           $.myUtil.ajax({
               url: 'unitType/' + unitCode + '/' + examBatchCode,
               type: 'get',
               dataType: 'json',
               success: function(data) {
                   var html = '';
                   for (var i = 0; i < data.length; i++) {
                       html += '<li><a href="javascript:void(0)" data-unit-code="' + data[i].unitCode + '" data-unit-type="' + data[i].code + '">' + data[i].name + '</a></li>';
                   }
                   $('.unit-type-ul').html(html);
                   $('.unit-type').text(data.length > 0 ? data[0].name : '请选择');
                   $('.unit-type').data('unitType', data.length > 0 ? data[0].code : '');
                   $('.unit-type').data('unitCode', (data.length > 0 && data[0].unitCode) ? data[0].unitCode : '');
               }
           });
       }
   }
   function setExam(unitCode) {
       if (unitCode == '') {
           $('.exam-ul').html('');
           $('.exam').text('请选择');
           $('.exam').data('examGroupCode', '');
           $('.exam').data('examCode', '');
       } else {
    	   var examBatchCode = $('.exam-batch').data('examBatch');
           $.myUtil.ajax({
               url: 'exam/' + unitCode + '/' + examBatchCode,
               type: 'get',
               dataType: 'json',
               success: function(data) {
                   var html = '';
                   for (var i = 0; i < data.examGroups.length; i++) {
                       html += '<li><a href="javascript:void(0)" data-exam-group="' + data.examGroups[i].code 
                       + '">' + data.examGroups[i].name + '</a></li>';
                   }
                   if (data.examGroups.length > 0 && data.exams.length > 0) {
                       html += '<li class="divider"></li>';
                   }
                   for (var i = 0; i < data.exams.length; i++) {
                       html += '<li><a href="javascript:void(0)" data-exam="' + data.exams[i].code 
                       + '">' + data.exams[i].name + '</a></li>';
                   }
                   $('.exam-ul').html(html);
                   if (data.examGroups.length > 0) {
                       $('.exam').text(data.examGroups[0].name);
                       $('.exam').data('examGroupCode', data.examGroups[0].code);
                       $('.exam').data('examCode', '');
                   } else if (data.exams.length > 0) {
                       $('.exam').text(data.exams[0].name);
                       $('.exam').data('examCode', data.exams[0].code);
                       $('.exam').data('examGroupCode', '');
                   } else {
                       $('.exam').text('请选择');
                       $('.exam').data('examCode', '');
                       $('.exam').data('examGroupCode', '');
                   }
               }
           });
       }
   }
   function setExamBatch() {
       $.myUtil.ajax({
           url: 'examBatch',
           type: 'get',
           dataType: 'json',
           success: function(rs) {
        	   if (rs.status) {
        		   var data = rs.data;
        		   var html = '';
        		   for (var i = 0; i < data.length; i++) {
        			   html += '<li><a href="javascript:void(0)" data-exam-batch="' + data[i].examCode + '">' + data[i].examName + '</a></li>';
        		   }
        		   $('.exam-batch-ul').html(html);
        		   $('.exam-batch').text(data.length > 0 ? data[0].examName : '请选择');
        		   $('.exam-batch').data('examBatch', data.length > 0 ? data[0].examCode : '请选择');
        		   setUnit(data[0].examCode);
        	   } else {
        		   $.myUtil.alert.show('错误提示',rs.msg, {modal: true}, function() {
        			   location.href = base + '/logout';
        		   });
        	   }
           }
       });
   }
   function setUnit(examCode) {
       $.myUtil.ajax({
           url: 'unit/' + examCode,
           type: 'get',
           dataType: 'json',
           success: function(data) {
               if (data.length > 0) {
                   var top = $.util.findTopUnit(data);
                   setUnitType(top.unitCode);
                   if (top.showStudent) {
                	   $('#search').show();
                   } else {
                	   $('#search').hide();
                   }
                   setExam(top.unitCode);
                   $('.unit').text(top.unitName);
                   $('.unit').data('unitCode', top.unitCode);
               } else {
                   $('.unit').text('请选择');
                   $('.unit').data('unitCode', '');
                   setUnitType('');
                   setExam('');
               }
               initTree(data);
           }
       });
   }
   function initTree(data) {
       var zNodes = [];
       for (var i = 0; i < data.length; i++) {
           zNodes.push({
               id: data[i].unitCode,
               pId: data[i].parentCode,
               name: data[i].unitName,
               showStudent: data[i].showStudent
           });
       }
       var setting = {
           view: {
               showIcon: false
           },
           data: {
               simpleData: {
                   enable: true
               }
           },
           callback: {
               onClick: onClick
           }
       };
       $.fn.zTree.init($("#unitTree"), setting, zNodes);
   }
   
   function onClick(event, treeCode, treeNode, clickFlag) {
       $('.unit').text(treeNode.name);
       $('.unit').data('unitCode', treeNode.id);
       $('.unit-tree').hide();
       if (treeNode.showStudent) {
    	   $('#search').show();
       } else {
    	   $('#search').hide();
       }
       setUnitType(treeNode.id);
       setExam(treeNode.id);
   }
   function checkWidth() {
       if ($.myUtil.getWinWidth() < 1200) {
           $('.exam-choose .unit').addClass('unit-small');
           $('.exam-choose .unit-type').addClass('unit-type-small');
           $('.exam-choose .exam').addClass('exam-small');
           $('.exam-choose .exam-batch').css({
               width: $.myUtil.getWinWidth() - 1024 + 70
           })
       }
   }
   checkWidth();
   var winHeight = $.myUtil.getWinHeight();
   var height = $('iframe').height();
   var top = (winHeight - height - 50);
   $('iframe').css({marginTop:  top > 0 ? top / 2 : 50});
   
   $('body').on('click', '.update-pwd', function(e) {
	   $.myUtil.dialogFrame.show("pwd", {
		   modal: true,
		   width: 350,
		   height: 285
	   });
   });
});