$(function() {
//	var imagePath = $('body').data('imagePath');
	var imagePath = 'image';
	$('#tabs').tabs({
		fit: true,
		plain: true,
		onSelect: function(title, index) {
			if (index == 1) {
				$('#imageWin').window('close');
			}
		}
	});
	$('#chooseExamBtn').click(function() {
		openChooseExamWin();
	});
	openChooseExamWin(true);
	createExam([]);
	function openChooseExamWin(first) {
		if (first) {
			$.myUtil.ajax({
				url: 'examBatch',
				type: 'get',
				dataType: 'json',
				success: function(data) {
					if (data.status) {
						openChooseExamWin2(first, data.data);
					} else {
						$.messager.alert('权限错误', data.msg, function() {
							window.location.href = 'logout';
						})
					}
				}
			});
		} else {
			openChooseExamWin2(first);
		}
	}
	function openChooseExamWin2(first, batches) {
		$('#chooseExamWin').window({
			closed: false,
			closable: !first,
			minimizable: !first,
			maximizable: false,
			collapsible: false,
			resizable: true
		});
		if (first) {
			createBatch(batches);
		}
	}
	function createBatch(batches) {
		$('#batch').combobox({
			data: batches,
			valueField:	'examCode',
		    textField: 'examName',
		    editable: false,
		    width: 300,
		    onSelect: function(r) {
		    	$('#batch').data('batchCode', r.examCode);
		    	$.myUtil.ajax({
		    		url: 'unit',
		    		type: 'get',
		    		data: {
		    			examBatchCode: r.examCode,
		    			maxTypeCode: 5000
		    		},
		    		dataType: 'json',
		    		success: function(data) {
		    			createUnit(data);
		    		}
		    	});
		    }
		});
		if (batches && batches.length > 0) {
			$('#batch').combobox('select', batches[0].examCode);
		}
	}
	$('body').on('focus', '#school', function() {
		$('#unitTree').show();
	});
	$('body').on('click', '#unitTab', function(e) {
		e.stopPropagation();
		return false;
	});
	$('body').on('click', function(e) {
		$('#unitTree').hide();
	});
	
	function createUnit(data) {
		var temp = {};
		var ck;
		for (var i = 0; i < data.length; i++) {
			var code = data[i].parentCode || '0';
			var node = {
					id: data[i].unitCode,
					pId: data[i].parentCode,
	                text: data[i].unitName,
	                typeCode: data[i].unitTypeCode
				};
			if (temp[code]) {
				temp[code].push(node);
			} else {
				temp[code] = [node];
			}
			if (!ck && data[i].unitTypeCode == 5000) {
				ck = node;
			}
		}
		var top = findTopUnit(temp, data);
		var nodes = temp[top];
		createTree(nodes, temp);
		$('#unitTree').tree({
			data: nodes,
			onSelect: function(node){
				if (node.typeCode != 5000) {
         		   return false;
         	    }
         	    $('#school').val(node.text);
                $('#school').data('unitCode', node.id);
                $('#unitTree').hide();
                $.myUtil.ajax({
             	  url: 'exam',
             	  type: 'get',
             	  data: {
             		  unitCode: node.id,
             		  examBatchCode: $('#batch').data('batchCode')
             	  },
             	  dataType: 'json',
             	  success: function(data) {
             		  createExam(data.exams);
             	  }
                });
			}
		});
		var root = $('#unitTree').tree('getRoot');
		var school = {find: false};
		findFirtSchool(root, school);
		$('#unitTree').tree('select', school.node.target);
	}
	function findFirtSchool(node, school) {
		if (node.typeCode == 5000) {
			school.node = node;
			school.find = true;
		} else {
			var child = $('#unitTree').tree('getChildren', node.target);
			for (var i = 0; i < child.length; i++) {
				if (!school.find) {
					findFirtSchool(child[i], school);
				}
			}
		}
	}
	function createTree(nodes, data) {
		for (var i = 0; i < nodes.length; i++) {
			nodes[i].children = data[nodes[i].id] || [];
			for (var j = 0; j < nodes[i].children.length; j++) {
				createTree(nodes[i].children, data);
			}
		}
	}
	function findTopUnit(units, data) {
		var top;
		for ( var u in units) {
			var flag = false;
			for (var j = 0; j < data.length; j++) {
				if (u == data[j].unitCode) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				top = u;
				break;
			}
		}
		return top;
	}
	function createExam(data) {
		$('#subject').combobox({
			data: data,
			valueField:	'code',
		    textField: 'name',
		    editable: false,
		    width: 300,
		    onSelect: function(r) {
		    	$('#subject').data('examCode', r.code);
		    	$.myUtil.ajax({
		    		url: 'unit/class',
		    		type: 'get',
		    		data: {
		    			examBatchCode: $('#batch').data('batchCode'),
		    			schoolCode: $('#school').data('unitCode'),
		    			examCode: r.code
		    		},
		    		dataType: 'json',
		    		success: function(data) {
		    			createClass(data);
		    		}
		    	});
		    }
		});
		if (data && data.length > 0) {
			$('#subject').combobox('select', data[0].code);
		}
	}
	function createClass(data) {
		var html = '';
		for (var i = 0; i < data.length; i++) {
			var d = '' + data[i];
			html += '<span><input type="checkbox" value="' + d + '" name="cls" >' + parseInt(d.substring(d.length - 2)) + '班</span>';
		}
		$('.classes').html(html);
		$('input[name="cls"]').prop('checked', $('#checkAll').is(':checked'));
	}
	$('#checkAll').change(function() {
		$('input[name="cls"]').prop('checked', $(this).is(':checked'));
	});
	$('body').on('change', 'input[name="cls"]', function() {
		$('#checkAll').prop('checked', $('input[name="cls"]').length == $('input[name="cls"]:checked').length);
	});
	$('#submit').click(function() {
		var batchCode = $('#batch').data('batchCode');
		var schoolCode = $('#school').data('unitCode');
		var examCode = $('#subject').data('examCode');
		var classes = '';
		$('input[name="cls"]:checked').each(function() {
			classes += $(this).val() + ',';
		});
		var flag = true;
		var msg = '';
		if (!batchCode) {
			flag = false;
			msg = '请选择考试批次!';
		} else if (!schoolCode) {
			flag = false;
			msg = '请选择学校!';
		} else if (!examCode) {
			flag = false;
			msg = '请选择考试科目!';
		} else if (classes == '') {
			flag = false;
			msg = '请至少选择一个班级!';
		}
		if (flag) {
			$('body').openLoading({modal: true});
			$('#chooseExamWin').window('close');
			var eCode = batchCode.substring(0, batchCode.length - 2) + examCode;
			$('#chooseExamWin').data('examCode', eCode);
			$.myUtil.ajax({
				url: 'review/questionScore',
				type: 'get',
				data: {
					examCode: eCode,
					classCodes: classes
				},
				dataType: 'json',
				success: function(data) {
					var rs = parseData(data);
					initDifficulty(rs.dd, rs.dt, rs.dft);
					initQuestionScore(rs.sd, rs.st, rs.sft);
					initQuestion(data.question);
					$('body').closeLoading();
				}
			});
		} else {
			$.messager.alert('温馨提示', msg);
		}
	});
	function initQuestion(data) {
		var examCode = $('#chooseExamWin').data('examCode');
		var classes = '';
		$('input[name="cls"]:checked').each(function() {
			classes += $(this).val() + ',';
		});
		$('#question').combobox({
			data: data,
			valueField:	'questionTitle',
		    textField: 'questionTitle',
		    editable: false,
		    width: 290,
		    onSelect: function(r) {
		    	$('#question').data('qestionTitle', r.questionTitle);
		    	$.myUtil.ajax({
		    		url: 'review/stuQuestionScore',
		    		type: 'get',
		    		data: {
		    			examCode: examCode,
						classCodes: classes,
						questionTitle: r.questionTitle
		    		},
		    		dataType: 'json',
		    		success: function(data) {
		    			initStuQuestion(data);
		    		}
		    	});
		    }
		});
		if (data && data.length > 0) {
			$('#question').combobox('select', data[0].questionTitle);
		}
	}
	function initStuQuestion(data) {
		for (var i = 0; i < data.length; i++) {
			data[i].class_name = parseInt(data[i].class_code.substring(data[i].class_code.length - 2)) + '班'; 
		}
		$('#student').datagrid({
			data: data,
		    columns: [[
		        {field: 'class_name',title: '班级',width: 50, sortable: true},
		        {field: 'exam_no',title: '考号',width: 110, sortable: true},
		        {field: 'studentname',title: '学生',width: 50, sortable: true},
		        {field: 'score',title: '得分',width: 50, sortable: true}
		    ]],
		    remoteSort: false,
		    singleSelect: true,
		    rownumbers: true,
		    fit: true,
		    collapsible: false,
		    onSelect: function(index, row) {
		    	var $content = $('#center');
		    	$content.empty();
		    	$('#center').parent().openLoading({modal: false});
		    	var src = imagePath + '?examCode=' + $('#chooseExamWin').data('examCode') + 
		    	'&title=' + $('#question').data('qestionTitle') + '&examNo=' + row.exam_no;
		    	if ($('#questionPaperBtn').hasClass('choose')) {
		    		$('#questionPaperBtn').removeClass('choose');
		    	}
		    	src += '&t=' + Math.random();
				var img = new Image();
				img.onload = function(e) { // 根据图片高度设定图片位置
					var $this = $(this);
					$this.attr('id', 'main');
					imageLoadHandler(e, $this, true);
					$('#center').parent().closeLoading();
				}
				img.src = src;
				$content.data('info', {
					examNo: row.exam_no,
					examCode: $('#chooseExamWin').data('examCode'),
					title: $('#question').data('qestionTitle')
				});
				var $img = $(img);
				$img.attr('id', 'main');
				if (!isSvg) {
					$content.append($img);
					$img.alginCenter();
				}
		    }
		});
		if (data && data.length > 0) {
			$('#student').datagrid('selectRow', 0);
		}
	}
	function initDifficulty(data, columns, frozenColumns) {
		$('#difficulty').datagrid({
			data: data,
		    columns: columns,
		    frozenColumns: frozenColumns,
		    remoteSort: false,
		    singleSelect: true,
		    rownumbers: true,
		    fit: true,
		    collapsible: true
		});
	}
	function initQuestionScore(data, columns, frozenColumns) {
		$('#questionScore').datagrid({
			data: data,
			columns: columns,
			frozenColumns: frozenColumns,
			remoteSort: false,
			singleSelect: true,
			rownumbers: true,
			fit: true,
			onClickCell: function(index, field, value){
				if (field.indexOf('q_') == 0) {
					var row = $('#questionScore').datagrid('getRows')[index];
					var examCode = $('#chooseExamWin').data('examCode');
					var title = $('#questionScore').datagrid('getColumnOption', field).title;
					var qTitle = title.substring(0, title.indexOf('('));
					var point = title.substring(title.indexOf('(') + 1, title.indexOf(')'));
					var src = imagePath + '?' + 'examNo=' + row.exam_no + '&examCode=' + examCode + '&title=' + qTitle + '&t=' + Math.random();
					$('#imageWin').empty();
					$('#imageWin').openLoading({modal: false});
					var image = new Image();
					image.onload = function(e) {
						var $this = $(this);
						imageLoadHandler(e, $this, false);
						$('#imageWin').closeLoading();
					} 
					image.src = src;
					$('#imageWin').append(image);
					$('#imageWin').window({
						closed: false,
						title: row.class_name + ' ' + row.studentname + ' [' + qTitle + '] (' + value + '/' + point + ')'
					});
				}
			},
			onDblClickCell: function(index, field, value){
				if (field.indexOf('q_') == 0) {
					var row = $('#questionScore').datagrid('getRows')[index];
					var examCode = $('#chooseExamWin').data('examCode');
					var title = $('#questionScore').datagrid('getColumnOption', field).title;
					var qTitle = title.substring(0, title.indexOf('('));
					var point = title.substring(title.indexOf('(') + 1, title.indexOf(')'));
					var src = imagePath + '?examCode=' + examCode + '&title=' + qTitle + '&t=' + Math.random();
					$('#imageWin').empty();
					$('#imageWin').openLoading({modal: false});
					var image = new Image();
					image.onload = function(e) {
						var $this = $(this);
						imageLoadHandler(e, $this, false);
						$('#imageWin').closeLoading();
					} 
					image.src = src;
					$('#imageWin').append(image);
					$('#imageWin').window({
						closed: false,
						title: qTitle
					});
				}
			}
		});
	}
	function parseData(data) {
		var dft = [
		        {field: 'class_name',title: '班级',width: 50, sortable: true},
		        {field: 'class_code',title: '编号',width: 110, sortable: true},
		        {field: 'score',title: '均分',width: 50, sortable: true},
		        {field: 'totle',title: '总分',width: 50, sortable: true}
		    ];
		var sft = [
		        {field: 'class_name',title: '班级',width: 50, sortable: true},
		        {field: 'exam_no',title: '考号',width: 110, sortable: true},
		        {field: 'studentname',title: '姓名',width: 50, sortable: true},
		        {field: 'score',title: '总分',width: 50, sortable: true}
		    ];
		var totle = 0;
		var dt = [], st = [];
		for (var i = 0; i < data.question.length; i++) {
			dt.push({
				field: 'q_' + formatTitle(data.question[i].questionTitle),
				title: data.question[i].questionTitle + '(' + data.question[i].difficulty + ')',
				width: 50, 
				sortable: true
				});
			st.push({
				field: 'q_' + formatTitle(data.question[i].questionTitle),
				title: data.question[i].questionTitle + '(' + (data.question[i].questionPoint / 100) + ')',
				width: 50, 
				sortable: true
			});
			totle += data.question[i].questionPoint;
		}
		totle/=100;
		var dd = data.difficulty;
		for (var i = 0; i < dd.length; i++) {
			dd[i].totle = totle;
			dd[i].class_name = parseInt(dd[i].class_code.substring(dd[i].class_code.length - 2)) + '班';
		}
		var sd = data.student;
		for (var i = 0; i < sd.length; i++) {
			sd[i].class_name = parseInt(sd[i].class_code.substring(sd[i].class_code.length - 2)) + '班';
			for ( var k in sd[i]) {
				if (k.indexOf('q_') === 0 || k == 'score') {
					sd[i][k] = sd[i][k] / 100;
				}
			}
		}
		return {
			dt: [dt],
			dft: [dft],
			dd: dd,
			st: [st],
			sft: [sft],
			sd: sd
		};
	}
	function formatTitle(title) {
		title = title.replace(/\\./g, '_');
		title = title.replace(/-/g, '_');
		title = title.toLowerCase();
		return title;
	}
	$('#picSlider').slider({
		showTip : true,
		value : 100,
		tipFormatter : function(value) {
			return value + '%';
		},
		min : 10,
		max : 200,
		width: 100,
		onComplete : function(zoom) {
			zoom = zoom * 1.0 / 100;
			var $main = $('#main');
			var size = $main.data('size');
			var url = $main.data('url');
			if (isSvg) {
				$('#center').empty();
				initCanvas(size, url, zoom);
			} else {
				$main.width(size.width * zoom);
				$main.height(size.height * zoom);
				$main.alginCenter();
			}
		},
		onChange: function(nv, ov) {
			var zoom = nv * 1.0 / 100;
			var $main = $('#main');
			var size = $main.data('size');
			var url = $main.data('url');
			if (isSvg) {
				$('#center').empty();
				initCanvas(size, url, zoom);
			} else {
				$main.width(size.width * zoom);
				$main.height(size.height * zoom);
				$main.alginCenter();
			}
		}
	});
	function getPicZoom() {
		return $('#picSlider').slider('getValue') * 1.0 / 100;
	}
	$('#questionPaperBtn').click(function() {
		$(this).toggleClass('choose');
		var $content = $('#center');
    	$content.empty();
    	$content.parent().openLoading({modal: false});
		var img = new Image();
		var info = $('#center').data('info');
		var src = imagePath + '?examCode=' + 
		info.examCode + '&title=' + info.title;
		if (!$('#questionPaperBtn').hasClass('choose')) {
			src += '&examNo=' + info.examNo
		}
		src += '&t=' + Math.random();
		img.onload = function(e) { // 根据图片高度设定图片位置
			imageLoadHandler(e, $(this), true);
			$content.parent().closeLoading();
		}
		img.src = src;
		var $img = $(img);
		$img.attr('id', 'main');
		if (!isSvg) {
			$content.append($img);
			$img.alginCenter();
		}
	});
	$('body').on('click', '#drawBtn', function(e) {
		if (isSvg) {
			$(this).toggleClass('choose');
		} else {
			alertNoSvg();
		}
	});
	$('body').on('click', '#clearDrawBtn', function(e) {
		if (isSvg) {
			var zoom = getPicZoom();
			var size = $('#main').data('size');
			var url = $('#main').data('url');
			$('#center').empty();
			initCanvas(size, url, zoom);
		} else {
			alertNoSvg();
		}
	});
	function imageLoadHandler(e, $this, zoom) {
		var size;
		if (e) {
			var target = e.target || (e.path && e.path[0]);
			size = { //ie9+,chrome
				width : target.naturalWidth,
				height : target.naturalHeight
			};
		} else {
			size = {
				width: this.width,
				height: this.height
			}
		}
		if (size.width > 0) {
			if (zoom) {
				$this.data('size', size);
				var zoom2 = getPicZoom();
				$this.width(size.width * zoom2);
				$this.height(size.height * zoom2);
				if (isSvg) {
					initCanvas(size, $this.attr('src'), zoom2);
				} else {
					$('#center').append($this);
					$this.alginCenter();
				}
			}
			$this.alginCenter();
		} else {
			setTimeout(function() {
				if (zoom) {
					size = {
						width: $this.width(),
						height: $this.height()
					}
					$this.data('size', size);
					var zoom2 = getPicZoom();
					$this.width(size.width * zoom2);
					$this.height(size.height * zoom2);
					if (isSvg) {
						initCanvas(size, $this.attr('src'), zoom2);
					} else {
						$('#center').append($this);
						$this.alginCenter();
					}
				}
				$this.alginCenter();
			}, 0);
		}
	}
	alertNoSvg = function() {
		$.messager.alert('温馨提示', '你的浏览器太老了，不支持该功能。推荐使用chrome或IE9及以上版本的浏览器访问!', 'info')
	}
});