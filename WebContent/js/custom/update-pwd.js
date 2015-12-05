$(function() {
	$('body').on('submit', '.form-signin', function(e) {
		e.preventDefault();
		e.stopPropagation();
		var oldPwd = $('#oldPwd').val();
		var newPwd = $('#newPwd').val();
		var newPwd2 = $('#newPwd2').val();
		$('#msg').css({color: 'red'});
		if (oldPwd == '') {
			$('#msg').text('旧密码不能为空!')
		} else if (newPwd == '') {
			$('#msg').text('新密码不能为空!')
		} else if (newPwd.length < 6) {
			$('#msg').text('新密码长度不能低于6位!')
		} else if (newPwd != newPwd2) {
			$('#msg').text('两次输入密码不一致!')
		} else {
			$('#msg').text('');
			$.ajax({
				url: 'pwd/update',
				type: 'post',
				data: {
					oldPwd: oldPwd,
					newPwd: newPwd
				},
				dataType: 'json',
				success: function(data) {
					if (data.status) {
						$.myUtil.dialogFrame.close();
						$.myUtil.alert.show('密码修改成功', data.msg, {modal: true});
					} else {
						$('#msg').text(data.msg);
					}
				}
			})
		}
		return false;
	});
});