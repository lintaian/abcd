$(function() {
	$('#switch_qlogin').click(function(){
		$('#switch_login').removeClass("switch_btn_focus").addClass('switch_btn');
		$('#switch_qlogin').removeClass("switch_btn").addClass('switch_btn_focus');
		$('#switch_bottom').animate({left:'0px',width:'70px'});
		$('#uintArea').show('slow');
		$('#web_qr_login').animate({height: '290px'});
	});
	$('#switch_login').click(function(){
		$('#switch_login').removeClass("switch_btn").addClass('switch_btn_focus');
		$('#switch_qlogin').removeClass("switch_btn_focus").addClass('switch_btn');
		$('#switch_bottom').animate({left:'154px',width:'70px'});
		$('#uintArea').hide('slow');
		$('#web_qr_login').animate({height: '235px'});
	});
	$('body').on('submit', '#loginform', function(e) {
		e.preventDefault();
		e.stopPropagation();
		var name = $('#name').val();
		var pwd = $('#password').val();
		var unitCode = '';
		var msg = '';
		var flag = true;
		if (name == null || name == '' || pwd == null || pwd == '') {
			flag = false;
			msg = '请输入用户名和密码!';
		}
		if (!$('#uintArea').is(':hidden')) {
			unitCode = $('#unit').data('unitCode');
			if (unitCode == null || unitCode == '') {
				flag = false;
				msg = '请选择机构!';
			}
		}
		if (flag) {
			$('body').openLoading({modal: true, text: '用户信息确认中...'});
			$.ajax({
				url: 'login',
				type: 'post',
				data: JSON.stringify({
					name: name,
					pwd: pwd,
					unitCode: unitCode
				}),
				dataType: 'json',
				success: function(data) {
					if (data.status) {
						$('body').openLoading({modal: true, text: '用户信息已确认，正在配置权限...'});
						$.ajax({
							url: 'auth',
							type: 'get',
							dataType: 'json',
							success: function(data2) {
								if (data2.status) {
									$('body').openLoading({modal: true, text: data2.msg});
									window.location.href = data2.jump;
								} else {
									$('body').closeLoading();
									$.myUtil.alert.show('提示信息', data2.msg, {modal: true});
								}
							}
						});
					} else {
						$('body').closeLoading();
						$.myUtil.alert.show('提示信息', data.msg, {modal: true});
					}
				}
			});
		} else {
			$.myUtil.alert.show('提示信息', msg, {modal: true});
		}
		return false;
	});
	$('body').on('focus', '#unit', function() {
		$('#unitTree').show();
	});
	$('body').on('click', '#unitTab', function(e) {
		e.stopPropagation();
		return false;
	});
	$('body').on('click', function(e) {
		$('#unitTree').hide();
	});
	$.ajax({
		url: 'unit/ztree',
		type: 'get',
		dataType: 'json',
		success: function(data) {
			initTree(data);
		},
		error: function() {
			$.myUtil.alert.show('提示信息', '获取组织结构失败!', {modal: true});
		}
	});
	function initTree(data) {
       var zNodes = [];
       for (var i = 0; i < data.length; i++) {
           zNodes.push({
               id: data[i].unitCode,
               pId: data[i].parentCode,
               name: data[i].unitName
           });
       }
       var setting = {
           view: {
               showIcon: false
           },
           data: {
               simpleData: {
                   enable: true
               }
           },
           callback: {
               onClick: onClick
           }
       };
       $.fn.zTree.init($("#unitTree"), setting, zNodes);
   }
   function onClick(event, treeCode, treeNode, clickFlag) {
       $('#unit').val(treeNode.name);
       $('#unit').data('unitCode', treeNode.id);
       $('#unitTree').hide();
   }
});