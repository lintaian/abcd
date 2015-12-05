require.config({
    paths: {
        echarts: 'js/lib'
    }
});
require(['echarts', 'echarts/theme/macarons', 'echarts/chart/bar', 'echarts/chart/line'], function (ec, theme) {
	$(function() {
		var myChart = ec.init(document.getElementById('main'), theme); 
        var unitCode = $('body').data('unitCode');
        var examCode = $('body').data('examCode');
        var examGroupCode = $('body').data('examGroupCode');
        var examBatch = $('body').data('examBatch');
        var isStudent = $('body').data('isStudent');
        var data, temp;
        var markLineLabel = '平均值';
        var base = $('base').attr('href');
        loading();
		$.myUtil.ajax({
			url: base + 'direction/data',
			type: 'get',
			data: {
				unitCode: unitCode,
				examCode: examCode,
				examGroupCode: examGroupCode,
				examBatch: examBatch,
				isStudent: isStudent
			},
			dataType: 'json',
			success: function(d) {
				data = d;
				var cfg = {
					single: examCode && examCode != '', 
					unitName: data.unitName
				};
				if (isStudent) {
					cfg.hideAbility = true;
				}
				temp = $.util.parseBarData(data, cfg);
				setOption();
			}
		});
        function setOption(noClear) {
        	var option = {
	            title : {
	                text: data.unitName + '-' + data.courseName + '-历次数据',
	                x: 'left',
	                subtext: '共 ' + data.childs.length + ' 次考试'
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
	            		var i = 0;
	            		for ( var s in temp.selected) {
	            			if (temp.selected[s]) {
	            				i++;
							}
						}
	            		rs.push({
                            name: k,
                            type: 'bar',
                            itemStyle : { normal: {label : {show: true, position: 'top',  textStyle: { color: 'black' }}}},
                            data: d[k].data,
                            markLine: !d[k].stack || (temp.selected && temp.selected[k] && i == 1) ? {
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
        		var examCode = $('body').data('examCode');
        		var name = '', groupCode = '';
        		if (examCode) {
					if (param.seriesIndex > 0) {
						name = param.seriesName;
						var index = $.util.findIndex(data.kNames, name);
						if (index != -1) {
							name = 'k_' + name;
						} else {
							name = 'a_' + name;
						}
					}
				} else {
					if (param.seriesIndex == 0) {
						groupCode = $('body').data('examGroupCode');
					} else {
						var index = $.util.findIndex(data.cNames, param.seriesName);
						examCode = data.cCodes[index];
					}
				}
        		var src = base;
        		if (isStudent) {
					src += 'rank/student';
				} else {
					src += 'rank/unit';
				}
        		window.location.href = src + '?unitCode=' + unitCode + '&examCode=' + examCode
					+ '&name=' + name + '&groupCode=' + groupCode + '&examBatch=' + examBatch;
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
			} else {
				temp = $.util.seriesSingle(temp, param.seriesName, zoom);
        		setOption();
        		$('.sort').addClass('single');
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
        	} else {
				for (var i = 1; i < sum; i++) {
					chart.delMarkLine(i, data.unitName + markLineLabel);
				}
				$('.sort').removeClass('single');
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