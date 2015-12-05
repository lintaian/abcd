var fileLocation = 'js/lib/zrender';
require.config({
    paths:{ 
        zrender: fileLocation,
        'zrender/shape/Rose': fileLocation,
        'zrender/shape/Trochoid': fileLocation,
        'zrender/shape/Circle': fileLocation,
        'zrender/shape/Sector': fileLocation,
        'zrender/shape/Ring': fileLocation,
        'zrender/shape/Ellipse': fileLocation,
        'zrender/shape/Rectangle': fileLocation,
        'zrender/shape/Text': fileLocation,
        'zrender/shape/Heart': fileLocation,
        'zrender/shape/Droplet': fileLocation,
        'zrender/shape/Line': fileLocation,
        'zrender/shape/Image': fileLocation,
        'zrender/shape/Star': fileLocation,
        'zrender/shape/Isogon': fileLocation,
        'zrender/shape/BezierCurve': fileLocation,
        'zrender/shape/Polyline': fileLocation,
        'zrender/shape/Path': fileLocation,
        'zrender/shape/Polygon': fileLocation
    }
});
require(['zrender', 'zrender/shape/Image', 'zrender/shape/Polyline'], function(zrender, ImageShape, Polyline) {
	$(function() {
		var pointList = [],
		img, lineShape,
		mouseDown = false,
		interval, zr, size;
		$('body').on('mousedown', '#main', function(e) {
			if ($('#drawBtn').hasClass('choose')) {
				mouseDown = true;
				pointList = [];
				img = zr.toDataURL('png');
				clearInterval(interval);
				interval = setInterval(function() {
					drawLine(pointList);
					if (!mouseDown) {
						clearInterval(interval);
						img = zr.toDataURL('png');
						zr.clear();
						drawImage(img);
					}
				}, 50);
			}
		});
		$('body').on('mousemove', '#main', function(e) {
			if (mouseDown) {
				pointList.push([e.offsetX, e.offsetY]);
			}
		});
		$('body').on('mouseup mouseout', '#main', function(e) {
			mouseDown = false;
		});
		initZR = function(s, url, zoom) {
			size = {
				width: s.width * zoom,
				height: s.height * zoom
			};
			var $div = $('<div></div>');
			$div.attr('id', 'main');
			$div.width(size.width);
			$div.height(size.height);
			$('#center').append($div);
			$div.alginCenter();
			$div.data('url', url);
			$div.data('size', s);
			zr = zrender.init(document.getElementById('main'));
			zr.clear();
			drawImage(url, true);
		}
		drawImage = function(url, first) {
			var image = new ImageShape({
		        style: {
		            image: url,
		            x: 0,
		            y: 0,
		            width: first ? size.width : 0,
		            height: first ? size.height : 0
		        }
		    });
		    zr.addShape(image);
		    zr.render();
		}
		function drawLine(pl) {
			zr.delShape(lineShape);
			lineShape = new Polyline({
		        style: {
		            pointList: pl,
		            smooth: 'spline',
		            strokeColor: 'red',
		            lineWidth: 1
		        }
		    });
		    zr.addShape(lineShape);
			zr.render();
		}
	})
});