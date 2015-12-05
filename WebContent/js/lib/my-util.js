(function($) {
	var baseDoc = window.document;
	$.myUtil = {
		alert: {
			fn: function(){},
			show: function(title, content, cfg, fn) {
				if ($.myUtil.isIE() && $.myUtil.getIEVersion() <= 8) {
					alert(content);
				} else {
					(fn && typeof(fn) == typeof(Function)) ? $.myUtil.alert.fn = fn : '';
					title = title || '来自网页的信息';
					if ($('.mu-alert', baseDoc).length == 0) {
						var html = '<div class="mu-alert mu-hide">' 
							+ '<div class="mu-alert-title" ></div>'
							+ '<div class="mu-alert-content"></div>'
							+ '<div class="mu-alert-btn"><button>确 定</button></div>'
							+ '</div>';
						$('body', baseDoc).append(html);
					}
					var $alert = $('.mu-alert', baseDoc);
					$alert.find('.mu-alert-title').text(title);
					$alert.find('.mu-alert-content').text(content);
					var h = $alert.height(),
					w = $alert.width(),
					winH = $.myUtil.getWinHeight(),
					winW = $.myUtil.getWinWidth();
					$alert.css({
						top: (winH - h) / 2,
						left: (winW - w) / 2
					});
					if (cfg.modal) {
						$.myUtil.modal.show(null, function() {
							$alert.fadeIn(500, function() {
								$alert.find('button').focus();
							});
						});
					} else {
						$alert.fadeIn(500, function() {
							$alert.find('button').focus();
						});
					}
				}
			},
			close: function() {
				$('.mu-alert', baseDoc).fadeOut(500, 'swing', function() {
					$.myUtil.modal.close();
					$.myUtil.alert.fn();
				});
			}
		},
		loading: {
			show: function(text, modal) {
				text = text || '加载中...';
				if ($('.mu-loading', baseDoc).length == 0) {
					var html = '<div class="mu-loading mu-hide"><img src="./img/loader.gif" /><div class="mu-loading-text">加载中...</div></div>';
					$('body', baseDoc).append(html);
				}
				var $load = $('.mu-loading', baseDoc);
				$load.children('.mu-loading-text').text(text);
				var h = $load.height(),
					w = $load.width(),
					winH = $.myUtil.getWinHeight(),
					winW = $.myUtil.getWinWidth();
				$load.css({
					top: (winH - h) / 2,
					left: (winW - w) / 2
				});
				if (modal) {
					$.myUtil.modal.show();
				}
				$load.show();
			},
			close: function() {
				$('.mu-loading', baseDoc).hide();
				$.myUtil.modal.close();
			}
		},
		dialog: {
			show: function(src, modal) {
				if ($('.mu-dialog', baseDoc).length == 0) {
					var html = '<div class="mu-dialog mu-hide"></div>';
					$('body', baseDoc).append(html);
				}
				var $dialog = $('.mu-dialog', baseDoc);
				$dialog.load(src, function() {
					var h = $dialog.height(),
					w = $dialog.width(),
					winH = $.myUtil.getWinHeight(),
					winW = $.myUtil.getWinWidth();
					$dialog.css({
						top: (winH - h) / 2,
						left: (winW - w) / 2
					});
					if (modal) {
						$.myUtil.modal.show(null, function() {
							$dialog.fadeIn(500);
						});
					} else {
						$dialog.fadeIn(500);
					}
				});
			},
			close: function() {
				$('.mu-dialog', baseDoc).fadeOut(500, 'swing', function() {
					$.myUtil.modal.close();
				});
			}
		},
		dialogFrame: {
			show: function(src, config) {
				if ($('.mu-dialog-frame', baseDoc).length == 0) {
					var html = '<div class="mu-dialog-frame mu-hide"><iframe src="" id="dialogFrame" width="100%" height="570" scrolling="false" frameborder="0"></iframe></div>';
					$('body', baseDoc).append(html);
				}
				var $dialog = $('.mu-dialog-frame', baseDoc);
				if (config.width) {
					$dialog.width(config.width);
				}
				if (config.height) {
					$dialog.find('iframe').height(config.height);
				}
				$dialog.children('iframe').attr('src', src);
				var h = $dialog.height(),
				w = $dialog.width(),
				winH = $.myUtil.getWinHeight(),
				winW = $.myUtil.getWinWidth();
				$dialog.css({
					top: (winH - h) / 2,
					left: (winW - w) / 2
				});
				if (config.modal) {
					$.myUtil.modal.show(null, function() {
						$dialog.fadeIn(500);
					});
				} else {
					$dialog.fadeIn(500);
				}
			},
			close: function() {
				$('.mu-dialog-frame', baseDoc).fadeOut(500, 'swing', function() {
					$.myUtil.modal.close();
					$('.mu-dialog-frame', baseDoc).children('iframe').attr('src', '');
				});
			}
		},
		modal: {
			show: function(speed, cb) {
				if ($('.mu-modal', baseDoc).length == 0) {
					var html = '<div class="mu-modal mu-hide"></div>';
					$('body', baseDoc).append(html);
				}
				$('.mu-modal', baseDoc).show();
				$('.mu-modal', baseDoc).animate({
					height: $.myUtil.getWinHeight()
				}, speed || 100, 'swing', function() {
					cb && cb();
				});
			},
			close: function() {
				$('.mu-modal', baseDoc).animate({
					height: 0
				}, 100);
			}
		},
		getWinHeight: function() {
		    var winHeight = 0;
		    // 获取窗口高度
		    if (window.innerHeight)
		        winHeight = window.innerHeight;
		    else if ((baseDoc.body) && (baseDoc.body.clientHeight))
		        winHeight = baseDoc.body.clientHeight;
		    // 通过深入 Document 内部对 body 进行检测，获取窗口大小
		    if (baseDoc.documentElement && baseDoc.documentElement.clientHeight && baseDoc.documentElement.clientWidth)
		    {
		        winHeight = baseDoc.documentElement.clientHeight;
		    }
		    if(navigator.userAgent.indexOf("Safari") > -1 && navigator.userAgent.indexOf("Chrome") < 0
		    		&& navigator.userAgent.indexOf("CriOS") < 0) {
		    	winHeight -= 20;
			} 
		    return winHeight;
		},
		getWinWidth: function() {
		    var winWidth = 0;
		    // 获取窗口宽度
		    if (window.innerWidth)
		        winWidth = window.innerWidth;
		    else if ((baseDoc.body) && (baseDoc.body.clientWidth))
		        winWidth = baseDoc.body.clientWidth;
		    if (baseDoc.documentElement && baseDoc.documentElement.clientHeight && baseDoc.documentElement.clientWidth)
		    {
		        winWidth = baseDoc.documentElement.clientWidth;
		    }
		    return winWidth;
		},
		ajax: function(config) {
			$.ajax({
				url: config.url,
				type: config.type,
				dataType: config.dataType,
				data: config.data,
				success: function(data){
					config.success && config.success(data);
				},
				error: function(e) {
					if (e.status == 403) {
						$.myUtil.alert.show('错误提示', '登陆用户session已过期，请重新登陆!', {modal: true}, 
							function() {
							window.location.href = "#";
						});
					}
					config.error && config.error(e);
				}
			});
		},
		isIE: function() {
			return !-[1,];
		},
		getIEVersion: function() {
			if ($.myUtil.isIE()) {
				var ver = navigator.appVersion;
				ver = ver.substring(ver.indexOf('MSIE ') + 5);
				ver = ver.substring(0, ver.indexOf(';'));
				return parseInt(ver);
			}
			return 11;
		},
		/**
		 * post访问后台
		 * @param url
		 * @param params 参数，一对象形式
		 * @returns
		 */
		post: function(url, params, isMulti) {      
		    var temp = document.createElement("form");      
		    temp.action = url;      
		    temp.method = "post";      
		    temp.style.display = "none";  
		    if (isMulti) {
				temp.enctype = 'multipart/form-data';
			}
		    for ( var i = 0; i < params.length; i++) {
		    	var opt = document.createElement(params[i].element);      
		    	opt.name = params[i].name;      
		    	opt.value = params[i].value;      
		    	opt.type = params[i].type;
		    	temp.appendChild(opt);      
			}
		    document.body.appendChild(temp);      
		    temp.submit();      
		    return temp;      
		}
	};
	$(function() {
		$('body', baseDoc).on('click', '.mu-alert .mu-alert-btn button', function() {
			$.myUtil.alert.close();
		});
		$('body', baseDoc).on('click', '.mu-alert', function(e) {
			e.stopPropagation();
		});
		$('body', baseDoc).on('click', '.mu-modal', function() {
			$.myUtil.alert.close();
			$.myUtil.dialog.close();
			$.myUtil.dialogFrame.close();
		});
		$('body', baseDoc).on('click', '.mu-dialog', function(e) {
			e.stopPropagation();
		});
		$('body', baseDoc).on('click', '.mu-dialog-frame', function(e) {
			e.stopPropagation();
		});
		$('body', baseDoc).on('click', '.mu-dialog-frame .mu-close', function(e) {
			$.myUtil.dialogFrame.close();
		});
	});
})(jQuery)