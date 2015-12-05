$(function() {
	var timer;
	var count = 0;
	$('body').on('click', '.pxs_slider img', function() {
		var $this = $(this);
		if (count == 0) {
			count++;
			timer = setTimeout(function() {
				count = 0;
				var title = $this.data('questionTitle');
				var examCode = $('body').data('examCode');
				var examNo = $('body').data('examNo');
				var src = 'question/original?examCode=' + examCode + '&title=' + title + '&examNo=' + examNo;
				$.myUtil.dialogFrame.show(src, {modal: true});
			}, 300);
		} else {
			count = 0;
			clearTimeout(timer);
			var title = $this.data('questionTitle');
			var examCode = $('body').data('examCode');
			var src = 'question/original?examCode=' + examCode + '&title=' + title;
			$.myUtil.dialogFrame.show(src, {modal: true});
		}
	});
});