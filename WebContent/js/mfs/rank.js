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
        var groupCode = $('body').data('groupCode');
        var examBatch = $('body').data('examBatch');
        var unitType = $('body').data('unitType');
        var name = $('body').data('name');
        var data, temp;
        var markLineLabel = '平均值';
        var base = $('base').attr('href');
        loading();
		$.myUtil.ajax({
			url: base + 'rank/data',
			type: 'get',
			data: {
				unitCode: unitCode,
				examCode: examCode,
				groupCode: groupCode,
				name: name,
				examBatch: examBatch,
				unitType: unitType
			},
			dataType: 'json',
			success: function(d) {
				if (d.batches.length > 0) {
					data = d;
					temp = $.util.parseRankBarData(data);
					setOption();
				} else {
					myChart.hideLoading();
					$.myUtil.alert.show('提示', '该组织无排名', {modal: true}, function() {
						history.back(-1);
					});
				}
			}
		});
        function setOption(noClear) {
        	var option = {
	            title : {
	                text: data.unitName + '-' + data.examName + (data.name ? '-' + data.name.substr(data.name.lastIndexOf('_') + 1) : '') + '-排名',
	                x: 'left',
	                subtext: '共 ' + data.batches.length + ' 次考试'
	            },
	            tooltip: {
	                trigger: 'axis',
	                axisPointer : {
	                    type : 'shadow'
	                },
	                formatter: function (params,ticket,callback) {
	                    var res = params[0][1] + '<br/>';
	                    for (var i = 0; i < params.length; i++) {
	                        res += params[i][0] + ' : ' + params[i][2] + 
	                        	'% (' + (params[i][2] == '-' ? '-' : temp.position[params[i][1] + params[i][0]])
	                        	+ ')' + '<br/>';
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
						rs.push({
                            name: k,
                            type: 'line',
                            itemStyle : { normal: {label : {show: true, position: 'top',  textStyle: { color: 'black' }}}},
                            data: d[k].data,
                            markLine: (temp.selected && temp.selected[k]) ? {
                                data: [[
                                        {name: data.unitName + markLineLabel, value: d[k].markLine, xAxis: -1, yAxis: d[k].markLine},
                                        {xAxis: d[k].data.length, yAxis: d[k].markLine}
                                    ]]
                                 } : '',
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
        		var index = -1;
        		if (chart.chart.bar) {
        			index = $.util.findIndex(chart.chart.bar.series, key, 'name');
				} else if (chart.chart.line) {
        			index = $.util.findIndex(chart.chart.line.series, key, 'name');
				}
        		if (index > -1) {
        			chart.addMarkLine(index, {
        				data: [
				        	{type : 'average', name: markLineLabel}
				        ]
        			});
        		}
        		$('.sort').addClass('single');
        	} else {
				for (var i = 0; i < sum; i++) {
					chart.delMarkLine(i, markLineLabel);
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