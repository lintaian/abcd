require.config({
    paths: {
        echarts: 'js/lib'
    }
});
require(['echarts', 'echarts/theme/macarons', 'echarts/chart/bar', 'echarts/chart/line'], function (ec, theme) {
	$(function() {
		var myChart = ec.init(document.getElementById('main'), theme); 
        var type = $('body').data('type');
        var value = $('body').data('value');
        var unitCode = $('body').data('unitCode');
        var childTypeCode = $('body').data('childTypeCode');
        var unitTypeName = $('body').data('unitTypeName');
        var examName = $('body').data('examName');
        var batchCode = $('body').data('batchCode');
        var data, temp;
        var markLineLabel = '平均值';
        var base = $('base').attr('href');
        loading();
		$.myUtil.ajax({
			url: base + 'data/markingOn',
			type: 'post',
			data: {
				type: type,
				value: value,
				unitCode: unitCode,
				childTypeCode: childTypeCode,
				examName: examName,
				batchCode: batchCode
			},
			dataType: 'json',
			success: function(d) {
				console.log(d);
				data = d;
				temp = $.util.parseMarkingOnData(data, {
					zoom: null
				});
				setOption();
			}
		});
        function setOption(noClear) {
        	var option = {
	            title : {
	                text: data.unitName + '-' + examName + '(自定义划线)',
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
	                    var name = params[0][1];
	                    name = temp.unitMap[name] + '-' + name;
	                    var ms = data.childs[name];
	                    for (var i = 0; i < ms.length; i++) {
							res += ms[i].name + ':<br/>';
							res += '&nbsp;&nbsp;' + unitTypeName + '内占比: ' + ms[i].unitScale + '%<br/>';
							res += '&nbsp;&nbsp;' + '横向占比: ' + ms[i].scale + '%<br/>';
							res += '&nbsp;&nbsp;' + '上线分数: ' + ms[i].score + '分<br/>';
							res += '&nbsp;&nbsp;' + '上线人数: ' + ms[i].amount + '/' + data.students[ms[i].name] + '人<br/>';
						}
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
                            itemStyle : { normal: {label : {show: false, position: 'top',  textStyle: { color: 'black' }}}},
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
        		if (childTypeCode >= 7000) {
				} else if (type == 'person') {
					$.myUtil.alert.show('提示', '按人数划线模式下不能进入下一层', {modal: true});
				} else {
        			var unitCode = temp.unitMap[param.name];
        			var href = 'markingOnTpl?type=' + type + '&value=' + value + '&unitCode=' + unitCode 
        			+ '&childTypeCode=0&batchCode=' + batchCode + '&examName=' + examName;
        			window.location.href = href;
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
        		$('.sort').removeClass('single');
        		temp = $.util.seriesMulti(temp, zoom);
        		setOption();
			} else {
				$('.sort').addClass('single');
				temp = $.util.seriesSingle(temp, param.seriesName, zoom);
        		setOption();
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
        		$('.sort').addClass('single');
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
        	} else {
        		$('.sort').removeClass('single');
				for (var i = 1; i < sum; i++) {
					chart.delMarkLine(i, data.unitName + markLineLabel);
				}
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
        	var count = 0;
        	var key;
        	for ( var k in selectedMap) {
        		if (selectedMap[k]) {
					count++;
					key = k;
				}
			}
        	if (count == 1) {
        		loading('排序中...');
        		temp.selected = selectedMap;
        		temp = $.util.sort(temp, key, $(this).hasClass('asc'), zoom);
        		setOption();
			}
        });
        
        function loading(text, type) {
        	myChart.showLoading({
                text : text || '数据加载中...',
                effect: type || 'spin'
            });
        }
	})
});