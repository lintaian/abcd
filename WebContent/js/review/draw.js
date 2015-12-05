var fileLocation = 'js/lib/';
require.config({
    paths:{ 
        raphael: fileLocation + 'raphael-min',
        rephaelExport: fileLocation + 'raphael-export',
        canvg: fileLocation + 'canvg'
    },
    shim: {
    	rephaelExport: {deps: ['raphael']}
	}
});
require(['raphael', 'rephaelExport', 'canvg'], function(Raphael) {
	$(function() {
		var mousedown = false;
		var lastX, lastY, path, pathString, paper;
		var colour = 'red', width = 1; 
		isSvg = Raphael.svg;
		if (!isSvg) {
			$('#drawBtn').addClass('invalid');
			$('#clearDrawBtn').addClass('invalid');
			$('#saveImage').addClass('invalid');
			$('#styleSet').addClass('invalid');
		}
		initCanvas = function(s, url, zoom) {
			size = {
				width: s.width * zoom,
				height: s.height * zoom
			};
			var $div = $('<div></div>');
			$div.attr('id', 'main');
			$('#center').append($div);
			$div.data('url', url);
			$div.data('size', s);
			paper = new Raphael(document.getElementById('main'), size.width, size.height);
			$div.alginCenter();
			convertImgToBase64(url, function(data) {
				paper.image(data, 0, 0, size.width, size.height);
		    });
		}
		$('#canvasWidth').slider({
			showTip : true,
			value : 1,
			tipFormatter : function(value) {
				return value + 'px';
			},
			min : 1,
			max : 50,
			width: 200,
			onComplete : function(zoom) {
				width = zoom;
				preview();
			},
			onChange: function(nv, ov) {
				width = nv;
				preview();
			}
		});
		$('#canvasWidth').slider('setValue', 2);
		function preview() {
			$('#preview').css({
				height: width,
				backgroundColor: colour
			});
		}
		$('body').on('click', '#styleSet', function(e) {
			if (isSvg) {
				$('#styleWin').window({
					closed: false
				});
			} else {
				alertNoSvg();
			}
		});
		$('body').on('click', '#canvasColours div', function() {
			colour = $(this).data('colour');
			$('#canvasColours div').removeClass('choose');
			$(this).addClass('choose');
			preview();
		});
		$('#canvasColours div:first').click();
		$('body').on('click', '#saveImage', function(e) {
			if (isSvg) {
				$('body').openLoading({modal: true, text: '图片生成中，请稍候...'});
				var svg = paper.toSVG();
				canvg(document.getElementById('myCanvas'), svg);
				setTimeout(function() {
					var dataURL = document.getElementById('myCanvas').toDataURL('png');
					var $img = $('<img>')
					$('#imageWin').empty().append('<div style="color: red;">请右键保存图片</div>').append($img);
					$('#imageWin img').attr('src', dataURL);
					$('#imageWin').window({
						closed: false,
						title: '图片保存'
					});
					$('body').closeLoading({modal: true});
				}, 1000);
			} else {
				alertNoSvg();
			}
		});
		$('body').on('mousedown', '#main', function(e) {
			if ($('#drawBtn').hasClass('choose')) {
				mousedown = true;
				$(this).addClass('pen');
				var x = e.offsetX, y = e.offsetY;
				pathString = 'M' + x + ' ' + y + 'l0 0';
				path = paper.path(pathString);
				path.attr({
			        'stroke': colour,
			        'stroke-linecap': 'round',
			        'stroke-linejoin': 'round',
			        'stroke-width': width
			    });
				lastX = x;
				lastY = y;
			}
		});
		$(document).mouseup(function() {
			mousedown = false;
			$('#main').removeClass('pen');
		});
		$('body').on('mousemove', '#main', function(e) {
			if (!mousedown || !$('#drawBtn').hasClass('choose')) {
				return;
			}
			var x = e.offsetX, y = e.offsetY;
			pathString += 'l' + (x - lastX) + ' ' + (y - lastY);
			path.attr('path', pathString);
			lastX = x;
			lastY = y;
		});
		function convertImgToBase64(url, callback, outputFormat){ 
			var canvas = document.createElement('CANVAS'); 
			var ctx = canvas.getContext('2d'); 
			var img = new Image; 
			img.onload = function(){ 
				canvas.height = img.height; 
				canvas.width = img.width; 
				ctx.drawImage(img,0,0); 
				var dataURL = canvas.toDataURL(outputFormat || 'image/png'); 
				callback.call(this, dataURL); 
				// Clean up 
				canvas = null; 
			}
			img.src = url; 
		} 
	})
})