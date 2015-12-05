require.config({
    paths: {
        echarts: 'js/lib'
    }
});
require(['echarts', 'echarts/theme/macarons', 'echarts/chart/radar'], function (ec, theme) {
	$(function() {
		var myChart = ec.init(document.getElementById('main'), theme); 
        var studentId = $('body').data('studentId') + '';
        var examCode = $('body').data('examCode') + '';
        var examGroupCode = $('body').data('examGroupCode') + '';
        var examBatch = $('body').data('examBatch') + '';
        var data, temp;
        var markLineLabel = '平均值';
        var base = $('base').attr('href');
        loading();
    	$.myUtil.ajax({
    		url: base + 'data/chart/student',
    		type: 'get',
    		data: {
    			studentId: studentId,
    			examCode: examCode,
    			examGroupCode: examGroupCode,
    			examBatch: examBatch
    		},
    		dataType: 'json',
    		success: function(d) {
    			data = d;
    			temp = $.util.parseRadarData(data, examCode && examCode != '');
    			setOption();
    			setSummary(d, examCode && examCode != '');
    			if (!(examCode && examCode != '')) {
    				writeGoalRate();
				}
    		}
    	});
        function setOption() {
        	var option = {
	            title : {
	                text: data.studentName + '-' + data.examName,
	                x: 'center'
	            },
	            tooltip : {
	                trigger: 'axis',
	                formatter: function (params,ticket,callback) {
	    	            var res = params[0][3];
	    	            for (var i = 0, l = params.length; i < l; i++) {
	    	                res += '<br/>' + params[i][1] + ' : ' + $.util.fixed(params[i][2] / temp.scale);
	    	            }
	                	return res;
	            	}
	            },
	            toolbox: {
	                show : true,
	                feature : {
	                    mark : {show: true},
	                    dataView : {show: true, readOnly: false},
	                    magicType : {show: false, type: []},
	                    restore : {show: true},
	                    saveAsImage : {show: true}
	                },
	                orient: 'vertical',
	                y: 'bottom'
	            },
	            legend: {
	                data: temp.legend,
	                x: 'center',
	                y: 'bottom',
	                selected: temp.selected || {}
	            },
	            polar: function(data) {
	            	var rs = [];
	            	for (var i = 0; i < data.length; i++) {
						rs.push({
							indicator: data[i],
			            	radius: '65%',
			            	center: ['56%', '50%'],
			            	splitNumber: 10,
			            	splitArea : {
			                    show : false,
			                    areaStyle : {
			                        color: ['rgba(250,0,250,0.3)','rgba(0,200,200,0.3)']
			                    }
			                },
			                splitLine : {
			                    show : true,
			                    lineStyle : {
			                        width : 1,
			                        color : 'rgba(225,225,225,0.8)'
			                    }
			                }
						});
						rs[i].indicator[0].axisLabel = {           // 坐标轴文本标签，详见axis.axisLabel
							show: false,
							textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
								color: '#000'
							}
						}
					}
	            	return rs;
	            }(temp.polar),
	            series: function(data) {
	            	var rs = [];
	            	for (var i = 0; i < data.length; i++) {
						rs.push({
							type: 'radar',
							polarIndex: i,
		                    data: data[i].data,
		                    symbolSize: 2
						})
					}
	            	return rs;
	            }(temp.series)
	        };
        	myChart.hideLoading();
        	myChart.clear();
        	myChart.setOption(option, true); 
        }
        
        var ecConfig = require('echarts/config');
        myChart.on(ecConfig.EVENT.CLICK, function(param) {
        	if (data.cNames) {
        		var code = data.cCodes && data.cCodes[param.special];
        		window.location.href = 'chart/student?studentId=' + studentId + '&examCode='
        		+ code+ '&examGroupCode=&examBatch=' + examBatch + '&backable=1';
        	 } else {
        		 var name = param.name;
        		 name = name.substring(0, name.lastIndexOf('('));
        		 examBatch = "" + examBatch;
        		 examCode = "" + examCode;
        		 var code = examBatch.substring(0,14) + examCode.substring(examCode.length - 2, examCode.length);
        		 window.open(base + 'question?studentId=' + studentId + '&examCode=' + code
        				 + '&name=' + name);
             }
        });
        
        function loading(text, type) {
        	myChart.showLoading({
                text : text || '数据加载中...',
                effect: type || 'spin'
            });
        }
        
        function setSummary(data, single) {
        	var sm = '<p class="title"><span class="name">' + data.studentName + '</span>&nbsp;同学:</p>';
        	var full = {}, mine = {}, cls = {};
        	if (single) {
        		full[data.examName] = sum(data.datas[0]);
				mine[data.examName] = sum(data.datas[1]);
				cls[data.examName] = sum(data.datas[2]);
				sm += sumWrite(full, mine, cls);
				full = formatData(data.datas[0], data.kNames, 1);
				mine = formatData(data.datas[1], data.kNames, 1);
				cls = formatData(data.datas[2], data.kNames, 1);
				sm += sumWrite(full, mine, cls);
				full = formatData(data.datas[0], data.aNames, 1 + data.kNames.length);
				mine = formatData(data.datas[1], data.aNames, 1 + data.kNames.length);
				cls = formatData(data.datas[2], data.aNames, 1 + data.kNames.length);
				sm += sumWrite(full, mine, cls);
			} else {
				full[data.examName] = sum(data.datas[0]);
				mine[data.examName] = sum(data.datas[1]);
				cls[data.examName] = sum(data.datas[2]);
				sm += sumWrite(full, mine, cls);
				full = formatData(data.datas[0], data.cNames, 1);
				mine = formatData(data.datas[1], data.cNames, 1);
				cls = formatData(data.datas[2], data.cNames, 1);
				sm += sumWrite(full, mine, cls);
			}
        	$('.summary').html(sm);
        }
        
        function sum(data) {
        	var rs = 0;
        	for (var i = 1; i < data.length; i++) {
				rs += data[i];
			}
        	return rs;
        }
        
        function formatData(data, name, start) {
        	var rs = {};
        	for (var i = 0; i < name.length; i++) {
				rs[name[i]] = data[i + start];
			}
        	return rs;
        }
        
        function sumWrite(full, mine, cls) {
        	var a = [], b = [], c = [], d = [], e = [], f = [], g = [];
        	for (var k in full) {
				if (mine[k] >= full[k] * 0.9) {
					a.push(k);
				} else if (mine[k] >= cls[k]) {
					if (mine[k] >= full[k] * 0.8) {
						b.push(k);
					} else if (mine[k] >= full[k] * 0.6) {
						c.push(k);
					} else {
						d.push(k);
					}
				} else {
					if (mine[k] >= full[k] * 0.8 ) {
						e.push(k);
					} else if (mine[k] >= full[k] * 0.6 ) {
						f.push(k);
					} else {
						g.push(k);
					}
				}
			}
        	var sm = '<p class="content">';
        	sm += '你的&nbsp;'
        	for (var i = 0; i < a.length; i++) {
				sm += '<span class="good">' + a[i] + '</span>';
				if (i < a.length - 1) {
					sm += '/';
				} else {
					sm += '&nbsp;非常优秀,请继续保持;&nbsp;'
				}
			}
        	for (var i = 0; i < b.length; i++) {
        		sm += '<span class="good">' + b[i] + '</span>';
        		if (i < b.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;优秀,请继续保持;&nbsp;'
        		}
        	}
        	for (var i = 0; i < c.length; i++) {
        		sm += '<span class="good">' + c[i] + '</span>';
        		if (i < c.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;有优势,但还有提升空间;&nbsp;'
        		}
        	}
        	for (var i = 0; i < d.length; i++) {
        		sm += '<span class="commonly">' + d[i] + '</span>';
        		if (i < d.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;虽然有优势,但还需要继续努力;&nbsp;'
        		}
        	}
        	for (var i = 0; i < e.length; i++) {
        		sm += '<span class="commonly">' + e[i] + '</span>';
        		if (i < e.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;优秀,但还还有提升空间;&nbsp;'
        		}
        	}
        	for (var i = 0; i < f.length; i++) {
        		sm += '<span class="bad">' + f[i] + '</span>';
        		if (i < f.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;有一定的基础,但还需要努力;&nbsp;'
        		}
        	}
        	for (var i = 0; i < g.length; i++) {
        		sm += '<span class="bad">' + g[i] + '</span>';
        		if (i < g.length - 1) {
        			sm += '/';
        		} else {
        			sm += '&nbsp;处于相对劣势,需要努力了;&nbsp;'
        		}
        	}
        	sm = sm.substring(0, sm.lastIndexOf(';&nbsp;')) + '。';
        	sm += '</p>';
        	return sm;
        }
        
        function writeGoalRate() {
        	var full = sum(data.datas[0]),
        		mine = sum(data.datas[1]);
        	var rate = parseInt((mine / full) * 100);
        	var html = '<li><a href="javascript:void(0)" data-rate="' + rate 
        		+ '" style="color: red;" data-unclickable="true">' + rate + '%</a></li>';
        	for (var i = rate + 1; (i <= rate + 10) && i <= 100; i++) {
				html += '<li><a href="javascript:void(0)" data-rate="' + i + '">' + i + '%</a></li>';
			}
        	$('.goal-analysis .goal-rate-ul').html(html);
        }
        
        function writeGoalAnalsis(ratio) {
        	var score = $.util.getGoalScore(data, ratio);
        	var html = '<p class="title"><span class="name">' + data.studentName + '</span>&nbsp;同学:</p>';
        	var good = [];
        	var bad = [];
        	var advance = [{name: '', val: 0}, {name: '', val: 0}];
        	for (var name in score['real']) {
        		var t = score['consult'][name] - score['real'][name];
				if (t < 0) {
					good.push(name);
				} else if (t > 0) {
					bad.push(name);
				}
				var rate = name == '语文' ? 0.5 : 1;
				if (advance[0].val < t * rate ) {
					advance[0] = {name: name, val: t};
				} else if (advance[1].val < t * rate ) {
					advance[1] = {name: name, val: t};
				}
			}
        	html += '<p class="content">你的&nbsp;'
        	for (var i = 0; i < good.length; i++) {
				html += '<span class="good">' + good[i] + '</span>';
				if (i < good.length - 1) {
					html+= '/';
				}
			}
        	html += '&nbsp;有优势;';
        	for (var i = 0; i < bad.length; i++) {
				html += '<span class="bad">' + bad[i] + '</span>';
				if (i < bad.length - 1) {
					html+= '/';
				}
			}
        	html += '&nbsp;处于相对劣势;';
        	for (var i = 0; i < advance.length; i++) {
				html += '<span class="commonly">' + advance[i].name + '</span>';
				if (i < advance.length - 1) {
					html+= '/';
				}
			}
        	html += '&nbsp;具有较大的成长空间。';
        	$('.summary').html(html);
        }
        
        $('body').on('click', '.goal-analysis li a:not([data-unclickable])', function() {
        	$('.goal-analysis .goal-rate').data('rate', $(this).data('rate'));
        	$('.goal-analysis .goal-rate').html($(this).html());
        });
        
        $('body').on('click', '.goal-analysis .goal-confirm', function() {
        	var ratio = $('.goal-analysis .goal-rate').data('rate');
        	if (ratio) {
        		loading();
        		temp = $.util.goalAnalysis(data, ratio/ 100);
        		setOption();
        		writeGoalAnalsis(ratio);
			} else {
				alert('请选择目标系数!');
			}
        });
        $('body').on('click', '.direction-check', function() {
            var studentId = $('body').data('studentId');
            var examCode = $('body').data('examCode');
            var examGroupCode = $('body').data('examGroupCode');
            var examBatch = $('body').data('examBatch');
            window.location.href = 'direction/student?unitCode=' + studentId
            	+ '&examCode=' + examCode + '&examGroupCode=' + examGroupCode + '&examBatch=' + examBatch;
        });
	})
});