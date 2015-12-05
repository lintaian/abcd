package com.lps.tqms.filter;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.ViewWrapper;

import com.lps.tqms.util.SessionHelper;

public class LoginJsonFilter implements ActionFilter {
	@Override
	public View match(ActionContext arg0) {
		if (!SessionHelper.isLogin(arg0.getRequest())) {
			return new ViewWrapper(new HttpStatusView(403), null);
		}
		return null;
	}
}
