package com.lps.tqms.module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.annotation.InjectName;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import com.lps.tqms.cache.CacheUtil;
import com.lps.tqms.filter.LoginJsonFilter;
import com.lps.tqms.service.ifs.BaseServiceIF;

@IocBean
@InjectName
@At("/review/")
@Fail("json")
public class ReviewModule {
	@Inject
	BaseServiceIF baseService;
	
	@At("questionScore/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getQuestionScore(String examCode, String classCodes,HttpServletRequest req) throws Exception {
		Map<String, Object> rs = new HashMap<String, Object>();
		List<String> cs = Arrays.asList(classCodes.split(","));
		rs.put("question", CacheUtil.getInstance().getQuestionInfos(examCode));
		rs.put("difficulty", baseService.getDifficulty(cs, examCode));
		rs.put("student", baseService.getQuestionScore(cs, examCode));
		return rs;
	}
	@At("stuQuestionScore/*")
	@Ok("json")
	@Filters({@By(type=LoginJsonFilter.class)})
	public Object getStudentQuestionScore(String examCode, String questionTitle, String classCodes, HttpServletRequest req) throws Exception {
		List<String> cs = Arrays.asList(classCodes.split(","));
		return baseService.getStuQuestionScore(cs, examCode, questionTitle);
	}
}
