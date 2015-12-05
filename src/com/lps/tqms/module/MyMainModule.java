package com.lps.tqms.module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.filter.LoginFilter;
import com.lps.tqms.filter.LoginJsonFilter;
import com.lps.tqms.image.Alert;
import com.lps.tqms.image.ImageHelper;
import com.lps.tqms.service.BaseService;
import com.lps.tqms.user.User;
import com.lps.tqms.util.SessionHelper;
import com.lps.tqms.util.Util;

@IocBean
@InjectName
@At("/")
@Fail("json")
public class MyMainModule {
	@Inject
	BaseService baseService;
	
	@At("login")
	@POST
	@Ok("json")
	@AdaptBy(type=JsonAdaptor.class)
	public Object loginPost(Map<String, String> body, HttpServletRequest req) throws Exception {
		String name = body.get("name");
		String pwd = body.get("pwd");
		String unitCode = body.get("unitCode");
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("status", false);
		if (name != null && !"".equals(name) && pwd != null && !"".equals(pwd)) {
			User user;
			if (unitCode != null && !"".equals(unitCode)) {
				user = baseService.getUser(name, unitCode);
			} else {
				user = baseService.getUser(name);
			}
			if (user == null) {
				rs.put("msg", (unitCode != null && !"".equals(unitCode)) ? "该组织下没有该用户或密码错误!" : "用户名或密码错误!");
			} else {
//				pwd = Util.MD5(pwd);
				if (pwd.equalsIgnoreCase(user.getPwd())) {
					rs.put("status", true);
					SessionHelper.set(req, "userName", name);
					SessionHelper.set(req, "unitCode", unitCode);
					SessionHelper.set(req, "imagePath", baseService.getImagePath());
				} else {
					rs.put("msg", "密码错误!");
				}
			}
		} else {
			rs.put("msg", "用户名或密码不能为空!");
		}
		return rs;
	}
	@At("auth")
	@Ok("json")
	public Object auth(HttpServletRequest req) {
		Map<String, Object> rs = new HashMap<String, Object>();
		String name = SessionHelper.get(req, "userName").toString();
		String unitCode = SessionHelper.get(req, "unitCode").toString();
		User user;
		if (unitCode != null && !"".equals(unitCode)) {
			user = baseService.getUserWithAuth(name, unitCode);
		} else {
			user = baseService.getUserWithAuth(name);
		}
		if (user.getAuths().size() > 0 && user.getModules().size() > 0) {
			SessionHelper.setUser(req, user);
			rs.put("status", true);
			rs.put("msg", "权限认证成功，正在跳转页面...");
			rs.put("jump", "./main/");
		} else {
			rs.put("status", false);
			rs.put("msg", "权限认证失败，请联系管理员!");
		}
		return rs;
	}
	
	@At("login")
	@Ok("jsp:jsp.login")
	@Fail("redirect:/main")
	public void loginGet(HttpServletRequest req) throws Exception {
		if (SessionHelper.isLogin(req)) {
			throw new Exception();
		}
	}
	@At("main/*")
	@Filters({@By(type=LoginFilter.class)})
	@Ok("jsp:jsp.main")
	public Object main(String unitCode, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return null;
	}
	@At("mfs/*")
	@Filters({@By(type=LoginFilter.class)})
	@Ok("jsp:jsp.mfs")
	public Object mfs(String unitCode, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		return rs;
	}
	@At("review/*")
	@Filters({@By(type=LoginFilter.class)})
	@Ok("jsp:jsp.review")
	public Object review(String unitCode, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("unitCode", unitCode);
		return rs;
	}
	
	@At("")
	@Ok("redirect:/main")
	public void index() {
	}
	@At("logout")
	@Ok("redirect:/login")
	public void logout(HttpServletRequest req) throws IOException {
		req.getSession().invalidate();
	}
	@At("pwd")
	@Ok("jsp:/tpl/update-pwd.jsp")
	@Filters({@By(type=LoginFilter.class)})
	public void updatePwd(HttpServletRequest req) throws Exception {
	}
	@At("pwd/update")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object updatePwdPost(HttpServletRequest req, String oldPwd, String newPwd) throws Exception {
		User user = SessionHelper.getUser(req);
		Map<String, Object> rs = new HashMap<String, Object>();
		if (Util.MD5(oldPwd).equals(user.getPwd())) {
			boolean status = baseService.updateUserPwd(user.getId(), Util.MD5(newPwd));
			rs.put("status", status);
			if (status) {
				rs.put("msg", "密码修改成功，请牢记你的新密码，下次登陆请使用新密码!");
			} else {
				rs.put("msg", "密码修改失败!");
			}
		} else {
			rs.put("status", false);
			rs.put("msg", "旧密码错误!");
		} 
		return rs;
	}
	@At("unit/ztree")
	@Ok("json")
	public Object getUnitTree(HttpServletRequest req) throws Exception {
		return CacheUtil.getInstance().getExamUnitCache().getInfo();
	}
	@At("clearCache/*")
	@Ok("json")
	public Object clearCache(String user, String pwd, String name, HttpServletRequest req) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		if ("admin".equals(user) && "lps".equals(pwd)) {
			rs.put("msg", CacheUtil.getInstance().clearCache(name) ? "清理成功!" : "清理失败!");
		} else {
			rs.put("msg", "权限错误!");
		}
		return rs;
	}
	@At("image")
	@Ok("raw:png")
	public Object getImage(String title, String examNo, String examCode, int height, HttpServletRequest req) throws Exception {
	    // 这个参数必须要
	    if (ImageHelper.isNullOrEmpty(examCode)) {
	      return null;
	    }
	    byte[] data = null;
	    String batchCode = examCode.substring(0, 14) + "00";
	    String courseCode = examCode.substring(14);
	    String imagePath = baseService.getImagePath2();
	    if (ImageHelper.isNullOrEmpty(examNo)) {
	      // 没有考号，表示取题干
	      String imgUrl = String.format("%s/%s/%s/03/%s.png", imagePath, batchCode, courseCode, title);
	      data = ImageHelper.getImage(imgUrl, height).toByteArray();
	    } else {
	      // 这里处理综合卷
	      ExamCourseInfo courseInfo = CacheUtil.getInstance().getCourse(examCode);
	      if (courseInfo.getComprehensive() == 1) {
	        if (courseCode.equals("06") || courseCode.equals("07") || courseCode.equals("08")) {
	          courseCode = "12";
	        } else if (courseCode.equals("09") || courseCode.equals("10") || courseCode.equals("11")) {
	          courseCode = "13";
	        }
	      }
	      if (ImageHelper.isNullOrEmpty(title)) {
	        // 没有题号，表示取全图
	        String imgUrl = String.format("%s/%s/%s/04/00/%s.png", imagePath, batchCode, courseCode, examNo);
	        data = ImageHelper.getImage(imgUrl, height).toByteArray();
	      } else {
	        // 有题号，取全图并按坐标切图
	        String area = baseService.getExamQuestionInfo(examCode, title).getArea();
	        if (area != null && area.length() > 0) {
	          String imgUrl = String.format("%s/%s/%s/04/00/%s.png", imagePath, batchCode, courseCode, examNo);
	          data = ImageHelper.getImage(imgUrl, area, height).toByteArray();
	        }
	      }
	    }
	    if (data == null || data.length == 0) {
	    	data = Alert.data;
		}
	    return new ByteArrayInputStream(data);
	}
}
