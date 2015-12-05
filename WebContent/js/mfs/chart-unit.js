require.config({
    paths: {
        echarts: 'js/lib'
    }
});
require(['echarts', 'echarts/theme/macarons', 'echarts/chart/bar', 'echarts/chart/line'], function (ec, theme) {
	$(function() {
		var myChart = ec.init(document.getElementById('main'), theme); 
        var unitCode = $('body').data('unitCode');
        var unitType = $('body').data('unitType');
        var unitTypeName = $('body').data('unitTypeName');
        var examCode = $('body').data('examCode');
        var examGroupCode = $('body').data('examGroupCode');
        var examBatch = $('body').data('examBatch');
        var myUnitCode = $('body').data('myUnitCode');
        var data, temp;
        var markLineLabel = '平均值';
        var base = $('base').attr('href');
        loading();
		$.myUtil.ajax({
			url: base + 'data/chart/unit',
			type: 'get',
			data: {
				unitCode: unitCode,
				childTypeCode: unitType,
				examCode: examCode,
				examGroupCode: examGroupCode,
				examBatch: examBatch,
				myUnitCode: myUnitCode
			},
			dataType: 'json',
			success: function(d) {
				data = d;
				temp = $.util.parseBarData(data, {
					single: examCode && examCode != '',
					zoom: null, 
					hideAbility: false,
					unitName: data.unitName
				});
				setOption();
			}
		});
        function setOption(noClear) {
        	var option = {
	            title : {
	                text: data.unitName + '-' + data.courseName,
	                x: 'left',
	                subtext: function() {
	                	var t = '共' + temp.axis.length + '个' + unitTypeName;
	                	return t;
	                }()
	            },
	            tooltip: {
	                trigger: 'axis',
	                axisPointer : {
	                    type : 'shadow'
	                },
	                formatter: function (params,ticket,callback) {
	                    var res = params[0][1] + '<br/>';
	                    for (var i = 0; i < params.length; i++) {
	                        res += params[i][0] + ' : ' + params[i][2] + '<br/>';
	                    };
	                    return res;
	                }
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: true, type: ['line', 'bar']},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                },
	                orient: 'vertical',
	                y: 'bottom'
	            },
	            dataZoom : {
			        show : true,
			        realtime : true,
			        start : (temp.zoom && temp.zoom.start) || 0,
			        end : (temp.zoom && temp.zoom.end) || function(data) {
			        	var end = parseInt(10 * 1.0 / data.length * 100);
			        	return end > 100 ? 100 : end || 1;
			        }(temp.axis)
			    },
			    grid: temp.grid,
	            legend: {
	                data: temp.legend,
	                x: 'right',
	                selected: temp.selected || {},
	                padding: [0, 5, 5, 5]
	            },
	            xAxis: {
                    data: temp.axis
                },
	            yAxis: {
                    type: 'value'
	            },
	            series: function(d) {
	            	var rs = [];
	            	for ( var k in d) {
						rs.push({
                            name: k,
                            type: 'bar',
                            itemStyle : { normal: {label : {show: true, position: 'top',  textStyle: { color: 'black' }}}},
                            data: d[k].data,
                            markLine: !d[k].stack || (temp.selected && temp.selected[k]) ? {
                                data: [[
                                        {name: data.unitName + markLineLabel, value: d[k].markLine, xAxis: -1, yAxis: d[k].markLine},
                                        {xAxis: d[k].data.length, yAxis: d[k].markLine}
                                    ]]
                                 } : '',
                            stack: d[k].stack
                        });
					}
	            	return rs;
	            }(temp.series)
	        };
        	myChart.hideLoading();
        	if (!noClear) {
        		myChart.clear();
			}
        	myChart.setOption(option, true); 
        }

        var ecConfig = require('echarts/config');
        var clickTimeout;
        myChart.on(ecConfig.EVENT.CLICK, function(param) {
        	clearTimeout(clickTimeout);
        	clickTimeout = setTimeout(function() {
        		var index = $.util.findIndex(data.childs, param.name, 1);
        		var unitCode = data.childs[index][0];
        		if (myUnitCode && myUnitCode != unitCode) {
        			$.myUtil.alert.show('权限问题', '你没有查看该组织或个人的权限!', {modal: true});
					return;
				}
	        	if (unitType == $.util.unitType.student) {
	        		var tempExamCode = '';
	        		if (data.cNames && param.seriesIndex > 0) {
		        		var index2 = $.util.findIndex(data.cNames, param.seriesName);
		        		tempExamCode = data.cCodes && data.cCodes[index2];
					}
	        		var studentId = data.childs[index][0];
        			var src = base + 'chart/student?studentId=' + studentId + '&examCode='
        				+ (examCode || tempExamCode)+ '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch;
        			$.myUtil.dialogFrame.show(src, {
        				modal: true,
        				width: 1000,
        				height: 570
        			});
				} else if (unitCode.length == 16) {
					if ($('body').data('showStudent') == true) {
						window.location.href = base + 'chart/unit?unitCode=' + unitCode + '&childTypeCode=' + $.util.unitType.student
						+ '&examCode=' + examCode + '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch;
					} else {
						$.myUtil.alert.show('权限问题', '你没有查看该组织或个人的权限!', {modal: true});
					}
				} else {
					window.location.href = base + 'chart/unit?unitCode=' + unitCode + '&childTypeCode=0'
					+ '&examCode=' + examCode + '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch;
				}
        	}, 300);
        });
        myChart.on(ecConfig.EVENT.DBLCLICK, function(param, chart) {
        	clearTimeout(clickTimeout);
        	var zoom = {};
        	if (myChart.chart.bar) {
				selectedMap = myChart.chart.bar.selectedMap;
				zoom = {
					start: myChart.chart.bar.component.dataZoom._zoom.start,
					end: myChart.chart.bar.component.dataZoom._zoom.end
				};
			} else if (myChart.chart.line) {
				selectedMap = myChart.chart.line.selectedMap;
				zoom = {
					start: myChart.chart.line.component.dataZoom._zoom.start,
					end: myChart.chart.line.component.dataZoom._zoom.end
				};
			}
        	var count = 0;
        	for ( var k in selectedMap) {
        		if (selectedMap[k]) {
					count++;
				}
			}
        	loading();
        	if (count == 1) {
        		temp = $.util.seriesMulti(temp, zoom);
        		setOption();
        		$('.sort').removeClass('single');
        		$('.draw-type').removeClass('single');
        		$('.draw').hide();
			} else {
				temp = $.util.seriesSingle(temp, param.seriesName, zoom);
        		setOption();
        		$('.sort').addClass('single');
        		$('#main').data('key', param.seriesName);
        		if (data.cNames || data.courseName == param.seriesName) {
        			$('.draw-type').addClass('single');
        			if (drawList.length > 0) {
        				$('.draw').show();
        			}
				}
			}
        });
        myChart.on(ecConfig.EVENT.LEGEND_SELECTED, function(param, chart, bb) {
        	var count = 0,
        		sum = 0,
        		key;
        	for ( var k in param.selected) {
        		sum++;
        		if (param.selected[k]) {
					count++;
					key = k;
				}
			}
        	if (count == 1) {
        		var index;
        		if (chart.chart.bar) {
        			index = $.util.findIndex(chart.chart.bar.series, key, 'name');
				} else if (chart.chart.line) {
        			index = $.util.findIndex(chart.chart.line.series, key, 'name');
				}
        		if (index > 0) {
        			chart.addMarkLine(index, {
        				data: [[
					        {name: data.unitName + markLineLabel, value: temp.series[key].markLine, xAxis: -1, yAxis: temp.series[key].markLine},
					        {xAxis: temp.series[key].data.length, yAxis: temp.series[key].markLine}
				        ]]
        			});
        		}
        		$('.sort').addClass('single');
        		$('#main').data('key', key);
        		if (data.cNames || data.courseName == key) {
        			$('.draw-type').addClass('single');
        			if (drawList.length > 0) {
        				$('.draw').show();
        			}
				}
        	} else {
				for (var i = 1; i < sum; i++) {
					chart.delMarkLine(i, data.unitName + markLineLabel);
				}
				$('.sort').removeClass('single');
				$('.draw-type').removeClass('single');
				$('.draw').hide();
			}
        });
        $('body').on('click', '.sort.single', function() {
        	var selectedMap;
        	var zoom = {};
        	if (myChart.chart.bar) {
				selectedMap = myChart.chart.bar.selectedMap;
				zoom = {
					start: myChart.chart.bar.component.dataZoom._zoom.start,
					end: myChart.chart.bar.component.dataZoom._zoom.end
				};
			} else if (myChart.chart.line) {
				selectedMap = myChart.chart.line.selectedMap;
				zoom = {
					start: myChart.chart.line.component.dataZoom._zoom.start,
					end: myChart.chart.line.component.dataZoom._zoom.end
				};
			}
        	var key = $('#main').data('key');
        	loading('排序中...');
        	temp.selected = selectedMap;
        	temp = $.util.sort(temp, key, $(this).hasClass('asc'), zoom);
        	setOption();
        });
        
        $('body').on('click', '.direction', function() {
        	 var unitCode = $('body').data('unitCode');
             var examCode = $('body').data('examCode');
             var examGroupCode = $('body').data('examGroupCode');
             var examBatch = $('body').data('examBatch');
             window.location.href = base + 'direction/unit?unitCode=' + unitCode + '&examCode=' + examCode 
             	+ '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch;
        });
        $('body').on('click', '.chartUnitBack', function() {
        	var unitCode = $('body').data('unitCode');
            var unitType = $('body').data('unitType');
            var examCode = $('body').data('examCode');
            var examGroupCode = $('body').data('examGroupCode');
            var examBatch = $('body').data('examBatch');
            window.location.href = base + 'chart/unit?unitCode=' + unitCode + '&unitType=' + unitType
            	+ '&examCode=' + examCode + '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch
            	+ '&back=1';
        });
        
        $('body').on('click', '.draw-type .dropdown-toggle', function(e) {
        	//当图标为多根柱子时不允许选中划线类型
        	if (!$(this).parent().hasClass('single')) {
        		e.stopPropagation();
        		e.preventDefault();
        		return false;
			}
        });
        //不同划线方式对应的单位
        var addOn = {
        	score: '分',
        	scorePercent: '%(分)',
        	person: '人',
        	personPercent: '%(人)'
        };
        var valMax = 100;
        $('body').on('click', '[data-draw-type]', function() {
        	var drawType = $(this).data('drawType');
        	if (drawType != $('.draw-type-txt').data('drawType')) {
        		$('.draw-type-txt').data('drawType', drawType);
        		if (drawType == 'score') {
        			var selectedMap;
                	if (myChart.chart.bar) {
        				selectedMap = myChart.chart.bar.selectedMap;
        			} else if (myChart.chart.line) {
        				selectedMap = myChart.chart.line.selectedMap;
        			}
                	var count = 0;
                	var key;
                	for ( var k in selectedMap) {
                		if (selectedMap[k]) {
        					count++;
        					key = k;
        				}
        			}
                	valMax = data.points[key]; 
				} else if (drawType == 'person') {
					valMax = data.students; 
				} else {
					valMax = 100;
				}
        		$('.draw input').prop('placeholder', '1-' + (valMax - 1));
        		$('.draw-type-txt').text($(this).children('a').text());
        		$('.draw').show();
        		$('.draw .add-on').text(addOn[drawType]);
        		drawList = [];
        		$('.draw-add').next('.popover').children('.popover-content').html('');
        		$('.draw input').val('');
        		$('.draw-add').popover('hide');
			}
        });
        $('.draw').hide();//划线输入默认隐藏
        $('body').on('keyup', '.draw input', function() {
        	var drawType = $('.draw-type-txt').data('drawType');
        	var oldVal = $(this).val();
        	var val = parseFloat(oldVal);
        	if (val) {
				if (val <= 0) {
					val = 1;
				} else if (val >= valMax) {
					val = valMax - 1;
				}
			} else {
				val = '';
			}
        	if (oldVal != val) {
        		$(this).val(val);
			}
        });
        var drawList = [];
        $('body').on('click', '.draw-add', function(e) {
        	e.stopPropagation();
        	e.preventDefault();
        	var val = $('.draw input').val();
        	if (val) {
        		var flag = true;
        		for (var i = 0; i < drawList.length; i++) {
        			if (drawList[i] == val) {
        				flag = false;
        				break;
        			}
        		}
        		if (drawList.length == 0) {
        			$('.draw-add').popover('show');
				}
        		if (flag) {
        			drawList.push(val);
        		}
        		showDrawList();
			}
    		return false;
        });
        $('.draw-add').popover({
        	html: true,
        	title: '已添加的划线',
        	placement: 'bottom',
        	content: '',
        	trigger: 'manual'
        });
        $('body').on('click', '.icon-remove', function() {
        	var val = $(this).prev('span').text();
        	var temp = [];
        	for (var i = 0; i < drawList.length; i++) {
        		if (drawList[i] != val) {
					temp.push(drawList[i]);
				}
			}
        	drawList = temp;
        	showDrawList();
        });
        $('body').on('click', '.draw .draw-ok', function() {
        	var drawType = $('.draw-type .draw-type-txt').data('drawType');
        	if (drawList.length > 0) {
        		var value = '';
        		for ( var d in drawList) {
        			value += drawList[d] + ',';
        		}
        		if (value.length > 1) {
        			value = value.substring(0, value.length - 1);
        		}
        		var href = 'markingOn?type=' + drawType + '&value=' + value + '&unitCode=' + unitCode 
        		+ '&childTypeCode=' + unitType + '&unitTypeName=' + unitTypeName + '&batchCode='
        		+ examBatch + '&examName=' + $('#main').data('key');
        		window.open(href);
			} else {
				$.myUtil.alert.show('提示', '请至少添加一个划线值!', {modal: true});
			}
        });
        
        function showDrawList() {
        	drawList.sort(function(a,b) {
        		return parseFloat(a) > parseFloat(b) ? 1 : -1;
        	});
        	var content = '<ul>';
        	for (var i = 0; i < drawList.length; i++) {
				content += '<li><span>' + drawList[i] +'</span><i class="icon icon-remove"></i></li>';
			}
        	content += '</ul>';
        	$('.draw-add').next('.popover').children('.popover-content').html(content);
        }
        
        function loading(text, type) {
        	myChart.showLoading({
                text : text || '数据加载中...',
                effect: type || 'spin'
            });
        }
	})
});