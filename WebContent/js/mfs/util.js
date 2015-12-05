(function($) {
	$.util = {
		unitType: {
			country: 1000,
			province: 2000,
			city: 3000,
			area: 4000,
			school: 5000,
			grade: 6000,
			klass: 7000,
			student: 8000
		},
		parseBarData: function(data, cfg) {
			var config = {
				single: false, 
				zoom: null, 
				hideAbility: false,
				unitName: ''
			}
			$.extend(config, cfg);
			var legend = [];
			var axis = [];
			var series = {};
			var selected = {};
			var grid = {};
			if (config.single) {
				var markLineIndex = 0;
				legend.push(data.courseName);
				series[data.courseName] = getBase(undefined, data.markLines && data.markLines[markLineIndex]);
				markLineIndex++;
				legend.push('');
				if (config.hideAbility) {
					selected[data.courseName] = true;
				}
				for(var i = 0; i < data.kNames.length; i++) {
					legend.push(data.kNames[i]);
					series[data.kNames[i]] = getBase('knowledge', data.markLines && data.markLines[markLineIndex]);
					markLineIndex++;	
					if (config.hideAbility) {
						selected[data.kNames[i]] = true;
					}
				}
				legend.push('');
				for(var i = 0; i < data.aNames.length; i++) {
					legend.push(data.aNames[i]);
					series[data.aNames[i]] = getBase('ability', data.markLines && data.markLines[markLineIndex]);
					markLineIndex++;
					if (config.hideAbility) {
						selected[data.aNames[i]] = false;
					}
				}
				for(var i = 0; i < data.childs.length; i++) {
					var un = '';
					if (!config.isDirection) {
						if (data.childs[i][2]) {	//个人
							if (config.unitName == data.childs[i][2]) { //班下面的个人
							} else if (config.unitName == data.childs[i][3]) { //校下面的个人
								un += data.childs[i][2] + '\n';
							} else { //校以上的个人
								un += data.childs[i][3] + data.childs[i][2] + '\n';
							}
						} else if(data.childs[i][3]) {	//班级
							if (config.unitName == data.childs[i][3]) { //校下面的班级
							} else { //校以上的班级
								un += data.childs[i][3] + '\n';
							}
						}
						if (un.length > 0) {
							grid.y2 = 80;
						}
					}
					axis.push(un + data.childs[i][1]);
					series[data.courseName].data.push(this.fixed(data.childs[i][4]));
					var flag = 5;
					for (var j = 0; j < data.kNames.length; j++) {
						series[data.kNames[j]].data.push(this.fixed(data.childs[i][j + flag]));
					}
					flag = 5 + data.kNames.length;
					for (var j = 0; j < data.aNames.length; j++) {
						series[data.aNames[j]].data.push(this.fixed(data.childs[i][j + flag]));
					}
//					for ( var s in series) {
//						if (series[s].data.length < i + 1) {
//							series[s].data.push('-');
//						}
//					}
				}
			} else {
				var markLineIndex = 0;
				legend.push(data.courseName);
				series[data.courseName] = getBase(undefined, data.markLines && data.markLines[markLineIndex]);
				markLineIndex++;
				legend.push('');
				for (var i = 0; i < data.cNames.length; i++) {
					legend.push(data.cNames[i]);
					series[data.cNames[i]] = getBase('course', data.markLines && data.markLines[markLineIndex]);
					markLineIndex++;
				}
				for(var i = 0; i < data.childs.length; i++) {
					var un = '';
					if (!config.isDirection) {
						if (data.childs[i][2]) {	//个人
							if (config.unitName == data.childs[i][2]) { //班下面的个人
							} else if (config.unitName == data.childs[i][3]) { //校下面的个人
								un += data.childs[i][2] + '\n';
							} else { //校以上的个人
								un += data.childs[i][3] + data.childs[i][2] + '\n';
							}
						} else if(data.childs[i][3]) {	//班级
							if (config.unitName == data.childs[i][3]) { //校下面的班级
							} else { //校以上的班级
								un += data.childs[i][3] + '\n';
							}
						}
						if (un.length > 0) {
							grid.y2 = 80;
						}
					}
					axis.push(un + data.childs[i][1]);
					series[data.courseName].data.push(this.fixed(data.childs[i][4]));
					var flag = 5;
					for (var j = 0; j < data.cNames.length; j++) {
						series[data.cNames[j]].data.push(this.fixed(data.childs[i][j + flag]));
					}
//					for ( var s in series) {
//						if (series[s].data.length < i + 1) {
//							series[s].data.push('-');
//						}
//					}
				}
			}
			for(var d in series) {
				if (!series[d].markLine) {
					var total = 0;
					for(var i=0; i< series[d].data.length; i++) {
						total += series[d].data[i];
					}
					series[d].markLine = this.fixed(total / series[d].data.length);
				}
			}
			return {
				legend: legend,
				axis: axis,
				series: series,
				zoom: config.zoom,
				selected: selected,
				grid: grid
			};
		},
		findIndex: function(list, value, key) {
			if (value.lastIndexOf('\n') > 0) {
				value = value.substring(value.lastIndexOf('\n') + 1, value.length)
			}
			if (key) {
				for (var i = 0; i < list.length; i++) {
					if (list[i][key] == value) {
						return i;
					}
				}				
			} else {
				for (var i = 0; i < list.length; i++) {
					if (list[i] == value) {
						return i;
					}
				}
			}
			return -1;
		},
		sort: function(data, name, order, zoom) {
			var series = data.series;
			var axis = data.axis;
			var dd = series[name].data;
			for (var i = 0; i < dd.length; i++) {
				for (var j = 0; j < dd.length; j++) {
					if (order) {
						if (dd[i] < dd[j]) {
							series = swap(series, i, j);
							axis = swap2(axis, i, j);
						}
					} else {
						if (dd[i] > dd[j]) {
							series = swap(series, i, j);
							axis = swap2(axis, i, j);
						}
					}
				}
			}
			data.series = series;
			data.axis = axis;
			data.zoom = zoom;
			return data;
		},
		seriesSingle: function(data, name, zoom) {
			var legend = data.legend;
			var selected = {};
			for (var i = 0; i < legend.length; i++) {
				selected[legend[i]] = false;
			}
			selected[name] = true;
			data.selected = selected;
			data.zoom = zoom;
			return data;
		},
		seriesMulti: function(data, zoom) {
			data.selected = '';
			data.zoom = zoom;
			return data;
		},
		parseRadarData: function(data, single) {
			var legend = [], series = [], polar = [], 
				selected = {}, scale = 1.0, full = [];
			var flag = 1;
			var max = 0;
			var names = [];
			if (single) {
				names = data.kNames;
				names = names.concat(data.aNames);
			} else {
				names = data.cNames;
			}
			for (var i = 0; i < names.length; i++) {
				var val = this.fixed(data.datas[0][i + flag]);
				full.push(val);
				polar.push({
					text: names[i] + '(' + val + ')',
					max: 100
				});
				if (val > max) {
					max = val;
				}
			}
			scale = 100.0 / max;
			for (var i = 0; i < data.datas.length; i++) {
				var values = [];
				values = values.concat(values, data.datas[i]);				var name = values[0];
				legend.push(name);
				if (i < 3) {
					selected[name] = true;
				} else {
					selected[name] = false;
				}
				values.shift();
				for (var j = 0; j < values.length; j++) {
					values[j] = values[j] * scale;
				}
				series.push({
					name: name,
					value: values
				});
			}
			return {
				legend: legend,
				series: [{
					name: '',
					data: series
				}],
				polar: [polar],
				selected: selected,
				scale: scale,
				full: [full]
			};
		},
//		getFullAndMyScore: function(data) {
//			var full = 0;
//			for (var i = 0; i < data.datas[0].length; i++) {
//				full += data.datas[0][i];
//			}
//			var myTotal = 0;
//			for (var i = 0; i < data.datas[1].length; i++) {
//				myTotal += data.datas[1][i];
//			}
//			return {
//				full: full,
//				mine: myTotal
//			};
//		},
		goalAnalysis: function(dataIn, ratio) {
			var data = this.parseRadarData(dataIn, false);
			var full = 0;
			for (var i = 0; i < data.series[0].data[0].value.length; i++) {
				full += data.series[0].data[0].value[i];
			}
			var myTotal = 0;
			for (var i = 0; i < data.series[0].data[1].value.length; i++) {
				myTotal += data.series[0].data[1].value[i];
			}
			var myRatio = myTotal * 1.0 / full;
			var legend = ['实际分数', '参照分数', '目标分数', ''];
			for (var i = 0; i < data.legend.length; i++) {
				if (i != 1)
					legend.push(data.legend[i]);
			}
			var selected = {};
			for (var i = 0; i < legend.length; i++) {
				if (i != 3) 					
					selected[legend[i]] = i < 3;
			}
			var seriesData = [];
			seriesData.push({
				name: '实际分数',
				value: data.series[0].data[1].value
			});
			seriesData.push({
				name: '参照分数',
				value: function() {
					var rs = [];
					for (var i = 0; i < data.series[0].data[0].value.length; i++) {
						rs.push($.util.fixed(data.series[0].data[0].value[i] * myRatio));
					}
					return rs;
				}()
			});
			seriesData.push({
				name: '目标分数',
				value: function() {
					var rs = [];
					for (var i = 0; i < data.series[0].data[0].value.length; i++) {
						rs.push($.util.fixed(data.series[0].data[0].value[i] * ratio));
					}
					return rs;
				}()
			});
			for (var i = 0; i < data.series[0].data.length; i++) {
				if (i != 1)
					seriesData.push(data.series[0].data[i]);
			}
			data.legend = legend;
			data.selected = selected;
			data.series[0].data = seriesData;
			return data;
		},
		getGoalScore: function(data, ratio) {
			var full = 0;
			for (var i = 1; i < data.datas[0].length; i++) {
				full += data.datas[0][i];
			}
			var myTotal = 0;
			for (var i = 1; i < data.datas[1].length; i++) {
				myTotal += data.datas[1][i];
			}
			var myRatio = myTotal * 1.0 / full;
			ratio = ratio * 1.0 / 100;
			var flag = 1;
			return {
				real: function() {
					var rs = [];
					for (var i = 0; i < data.cNames.length; i++) {
						rs[data.cNames[i]] = $.util.fixed(data.datas[1][i + flag]);
					}
					return rs;
				}(),
				goal: function() {
					var rs = [];
					for (var i = 0; i < data.cNames.length; i++) {
						rs[data.cNames[i]] = $.util.fixed(data.datas[0][i + flag] * ratio);
					}
					return rs;
				}(),
				consult: function() {
					var rs = [];
					for (var i = 0; i < data.cNames.length; i++) {
						rs[data.cNames[i]] = $.util.fixed(data.datas[0][i + flag] * myRatio);
					}
					return rs;
				}()
			};
		},
		fixed: function(num) {
			if(typeof num === 'number') {
				return num.toFixed(1) * 1.0;
			} else {
				return num;
			}
		},
		substrCode: function(code) {
			code = code.length > 8 ? code.substring(0, 8) : code;
			if (code.endWith('00')) {
				code = code.substring(0, code.length -2);
			}
			return code;
		},
		findTopUnit: function(units) {
			var code = 10000;
			var rs = {};
			for (var i = 0; i < units.length; i++) {
				if (units[i].unitTypeCode < code) {
					code = units[i].unitTypeCode;
					rs = units[i];
				}
			}
			return rs;
		},
		parseRankBarData: function(data, zoom) {
			var legend = [];
			var axis = [];
			var series = {};
			var selected = {};
			var position = {};
			for (var i = 0; i < data.batches.length; i++) {
				axis.push(data.batches[i].batchName);
			}
			var temp = {};
			for (var i = 0; i < data.batches.length; i++) {
				for (var j = 0; j < data.batches[i].units.length; j++) {
					if (!temp[data.batches[i].units[j][1]]) {
						temp[data.batches[i].units[j][1]] = 1;
					}
					series[data.batches[i].units[j][1]] ? 
						series[data.batches[i].units[j][1]].data.push(data.batches[i].units[j][3]) :
						series[data.batches[i].units[j][1]] = {data: [data.batches[i].units[j][3]]};
					position[data.batches[i].batchName + data.batches[i].units[j][1]] = data.batches[i].units[j][2];
				}
			}
			for ( var k in temp) {
				legend.push(k);
				selected[k] = false;
			}
			selected[legend[0]] = true;
			for(var d in series) {
				var total = 0;
				for(var i=0; i< series[d].data.length; i++) {
					total += series[d].data[i];
				}
				series[d].markLine = this.fixed(total / series[d].data.length);
			}
			return {
				legend: legend,
				axis: axis,
				series: series,
				zoom: zoom,
				selected: selected,
				position: position
			};
		},
		parseMarkingOnData: function(data, cfg) {
			var config = {
				zoom: null, 
				unitName: ''
			}
			$.extend(config, cfg);
			var legend = [];
			var axis = [];
			var series = {};
			var grid = {};
			var unitMap = {};
			for ( var k in data.childs) {
				var names = k.split('-');
				unitMap[names[1]] = names[0];
				var un = names[1];
				if (names.length == 3) {	//非学校下的班级
					un += names[2] + '\n' + names[1];
					grid.y2 = 80;
				}
				axis.push(un);
				if (legend.length == 0) {
					for (var i = data.childs[k].length - 1; i >= 0; i--) {
						var name = data.childs[k][i].name;
						legend.push(name);
						series[name] = {data: [], stack: 'markingOn'};
					}
				}
				for (var i = data.childs[k].length - 1; i >= 0; i--) {
					var name = data.childs[k][i].name;
					series[name].data.push(this.fixed(data.childs[k][i].unitScale));
				}
			}
			for(var d in series) {
				if (!series[d].markLine) {
					var total = 0;
					for(var i=0; i< series[d].data.length; i++) {
						total += series[d].data[i];
					}
					series[d].markLine = this.fixed(total / series[d].data.length);
				}
			}
			return {
				legend: legend,
				axis: axis,
				series: series,
				zoom: config.zoom,
				grid: grid,
				unitMap: unitMap
			};
		}
	};
	
	function swap(series, a, b) {
		for ( var i in series) {
			var t = series[i].data[a];
			series[i].data[a] = series[i].data[b];
			series[i].data[b] = t;
		}
		return series;
	}
	function swap2(axis, a, b) {
		var t = axis[a];
		axis[a] = axis[b];
		axis[b] = t;
		return axis;
	}
	function getBase(stack, markLine) {
		return {data: [], stack: stack, markLine: markLine};
	}
	String.prototype.endWith = function(str) {
		if(str==null||str==""||this.length==0||str.length>this.length)
			return false;
		if(this.substring(this.length-str.length)==str)
			return true;
		else
			return false;
		return true;
	}
	String.prototype.startWith = function(str) {
		if(str==null||str==""||this.length==0||str.length>this.length)
			return false;
		if(this.substr(0,str.length)==str)
			return true;
		else
			return false;
		return true;
	}
})(jQuery)