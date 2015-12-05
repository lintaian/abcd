(function($) {
	$.fn.extend({
		/**
		 * 开启loading
		 */
		openLoading: function(option) {
			var cfg = {
				modal: false,
				text: '加载中...'
			}
			$.extend(cfg, option);
			this.closeLoading();
			return this.each(function() {
				var $this = $(this);
				var html = '<div class="lta-loading"><img src="./img/loader.gif" /><div class="lta-loading-text">' + cfg.text + '</div></div>';
				$this.append(html);
				if (cfg.modal) {
					$this.append('<div class="lta-modal"></div>');
				}
			});
		},
		/**
		 * 关闭loading
		 */
		closeLoading: function() {
			return this.each(function() {
				var $this = $(this);
				$this.find('.lta-loading').remove();
				$this.find('.lta-modal').remove();
			});
		},
		/**
		 * 元素在父容器中居中
		 */
		alginCenter: function(option) {
			var cfg = {
				marginTop: 10
			}
			$.extend(cfg, option);
			return this.each(function() {
				var $this = $(this);
				var contentHeight = $this.height();
				var height = $this.parent().height();
				if (height > contentHeight) {
					$this.css({
						marginTop : (height - contentHeight) / 2
					});
				} else {
					$this.css({
						marginTop : cfg.marginTop
					});
				}
			});
		}
	});
})(jQuery);