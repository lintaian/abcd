package com.lps.tqms.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.nutz.dao.entity.Record;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import com.lps.tqms.dao.ExamDao;
import com.lps.tqms.dao.ifs.ExamDaoIF;
import com.lps.tqms.db.pojo.ExamBatchInfo;
import com.lps.tqms.db.pojo.ExamCourseInfo;
import com.lps.tqms.db.pojo.ExamQuestionInfo;
import com.lps.tqms.pojo.CacheConfig;
import com.lps.tqms.pojo.ExamType;
import com.lps.tqms.pojo.ExamTypeGroup;
import com.lps.tqms.user.UnitQueryInfo;
import com.lps.tqms.util.Util;

public class CacheUtil {
	private static volatile CacheUtil instance = null;
	private static Object lockHelper = new Object();
	public static CacheUtil getInstance() {
		if (instance == null) {
			synchronized (lockHelper) {
				if (instance == null) {
					instance = new CacheUtil();
				}
			}
		}
		return instance;
	}
	private Map<String, KaCache> kas = new HashMap<String, KaCache>();
	private Map<String, BatchCache> batches = new HashMap<String, BatchCache>();
	private Map<String, CourseCache> courses = new HashMap<String, CourseCache>();
	private Map<String, UnitCache> unitInfos = new HashMap<String, UnitCache>();
	private Map<String, QuestionCache> questionInfos = new HashMap<String, QuestionCache>();
	private SubjectCache subjectCache = new SubjectCache(); //科目信息缓存
	private ExamUnitCache examUnitCache = new ExamUnitCache();
	private ExamDaoIF examDao;
	private CacheConfig cacheConfig;
	private static final Log log = Logs.get();
	private CacheUtil() {
		examDao = Mvcs.ctx.getDefaultIoc().get(ExamDao.class);
		cacheConfig = Mvcs.ctx.getDefaultIoc().get(CacheConfig.class);
		timer();
	}
	public Ka getKa(String examCode) {
		if (!kas.containsKey(examCode)) {
			List<Record> knowledges = examDao.getKnowledge(examCode);
			List<Record> abilities = examDao.getAbility(examCode);
			Ka ka = new Ka();
			for (Record record : knowledges) {
				ka.knowledge.add(new NA(record.getString("name"), record.getString("code"), record.get("val")));
			}
			for (Record record : abilities) {
				ka.ability.add(new NA(record.getString("name"), record.getString("code"), record.get("val")));
			}
			kas.put(examCode, new KaCache(ka));
		}
		return kas.get(examCode).getKa();
	}
	public ExamBatchInfo getBatch(String batchCode) {
		if (!batches.containsKey(batchCode)) {
			batches.put(batchCode, new BatchCache(examDao.getBatchInfo(batchCode)));
		}
		return batches.get(batchCode).getBatchInfo();
	}
	public ExamCourseInfo getCourse(String examCode) {
		if (!courses.containsKey(examCode)) {
			courses.put(examCode, new CourseCache(examDao.getCourseInfo(examCode)));
		}
		return courses.get(examCode).getCourseInfo();
	}
	public List<Object> getCoursePoint(String batchCode, ExamTypeGroup group, Set<ExamType> exams) {
		List<Object> rs = new ArrayList<Object>();
		batchCode = batchCode.substring(0, 14);
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				String key = batchCode + exam.code;
				ExamCourseInfo courseInfo = getCourse(key);
				rs.add(courseInfo.getExamPoint());
			}
		}
		return rs;
	}
	public Map<String, Object> getCoursePoints(String batchCode, ExamTypeGroup group, Set<ExamType> exams) {
		Map<String, Object> rs = new HashMap<String, Object>();
		batchCode = batchCode.substring(0, 14);
		for (ExamType exam : group.exams) {
			if (exams.contains(exam)) {
				String key = batchCode + exam.code;
				ExamCourseInfo courseInfo = getCourse(key);
				rs.put(courseInfo.getExamName(), courseInfo.getExamPoint());
			}
		}
		return rs;
	}
	public UnitQueryInfo getUnitQueryInfo(String batchCode) {
		if (!unitInfos.containsKey(batchCode)) {
			unitInfos.put(batchCode, new UnitCache(Util.transListToUnitQueryInfo(examDao.getUnitInfo(batchCode))));
		}
		return unitInfos.get(batchCode).getInfo();
	}
	public UnitQueryInfo getUnitQueryInfo(String batchCode, String queryId) {
		String key = batchCode + "-" + queryId;
		if (!unitInfos.containsKey(key)) {
			unitInfos.put(key, new UnitCache(Util.transListToUnitQueryInfo(examDao.getUnitInfo(queryId, batchCode))));
		}
		return unitInfos.get(key).getInfo();
	}
	public List<ExamQuestionInfo> getQuestionInfos(String examCode) {
		if (!questionInfos.containsKey(examCode)) {
			questionInfos.put(examCode, new QuestionCache(examDao.getExamQuestionInfos(examCode)));
		}
		return questionInfos.get(examCode).getInfos();
	}
	public SubjectCache getSubjectCache() {
		if (subjectCache.getSubjects().size() == 0) {
			subjectCache.setSubjects(examDao.getSubjects());
		}
		return subjectCache;
	}
	public ExamUnitCache getExamUnitCache() {
		if (examUnitCache.getInfo().size() == 0) {
			examUnitCache.setInfo(examDao.getUnitInfos());
		}
		return examUnitCache;
	}
	public boolean clearCache(String name) {
		boolean rs = true;
		try {
			if (name == null || "".equals(name)) {
				clearCacheKa();
				log("ka");	
				clearCacheBatch();
				log("batch");	
				clearCacheCourse();
				log("course");	
				clearCacheUnit();
				log("unitInfo");
				clearCacheQuestion();
				log("questionInfo");
				clearCacheSbuject();
				log("subject");
				clearCacheExamUnitInfo();
				log("examUnitInfo");
			} else if ("ka".equals(name)) {
				clearCacheKa();
				log("ka");	
			} else if ("batch".equals(name)) {
				clearCacheBatch();
				log("batch");
			} else if ("course".equals(name)) {
				clearCacheCourse();
				log("course");
			} else if ("unitInfo".equals(name)) {
				clearCacheUnit();
				log("unitInfo");
			} else if ("questionInfo".equals(name)) {
				clearCacheQuestion();
				log("questionInfo");	
			} else if ("subject".equals(name)) {
				clearCacheSbuject();
				log("subject");
			} else if ("examUnitInfo".equals(name)) {
				clearCacheExamUnitInfo();
				log("examUnitInfo");
			}
		} catch (Exception e) {
			rs = false;
			e.printStackTrace();
		}
		return rs;
	}
	private void timer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				log(kas.size(), clearCacheKa(kas).size(), "ka");	
				log(batches.size(), clearCacheBatch(batches).size(), "batch");	
				log(courses.size(), clearCacheCourse(courses).size(), "course");	
				log(unitInfos.size(), clearCacheUnit(unitInfos).size(), "unitInfo");	
				log(questionInfos.size(), clearCacheQuestion(questionInfos).size(), "questionInfo");	
				log(1, clearCacheSbuject(subjectCache), "subject");	
			}
		}, cacheConfig.getClearInterval(), cacheConfig.getClearInterval());
	}
	private List<String> clearCacheKa(Map<String, KaCache> source) {
		long time = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>();
		for (Map.Entry<String, KaCache> entry : source.entrySet()) {
			if (time - entry.getValue().getActive() > cacheConfig.getClearTime()) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			source.remove(key);
		}
		return keys;
	}
	private void clearCacheKa() {
		kas = new HashMap<String, KaCache>();
	}
	private List<String> clearCacheBatch(Map<String, BatchCache> source) {
		long time = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>();
		for (Map.Entry<String, BatchCache> entry : source.entrySet()) {
			if (time - entry.getValue().getActive() > cacheConfig.getClearTime()) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			source.remove(key);
		}
		return keys;
	}
	private void clearCacheBatch() {
		batches = new HashMap<String, BatchCache>();
	}
	private List<String> clearCacheCourse(Map<String, CourseCache> source) {
		long time = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>();
		for (Map.Entry<String, CourseCache> entry : source.entrySet()) {
			if (time - entry.getValue().getActive() > cacheConfig.getClearTime()) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			source.remove(key);
		}
		return keys;
	}
	private void clearCacheCourse() {
		courses = new HashMap<String, CourseCache>();
	}
	private List<String> clearCacheUnit(Map<String, UnitCache> source) {
		long time = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>();
		for (Map.Entry<String, UnitCache> entry : source.entrySet()) {
			if (time - entry.getValue().getActive() > cacheConfig.getClearTime()) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			source.remove(key);
		}
		return keys;
	}
	private void clearCacheUnit() {
		unitInfos = new HashMap<String, UnitCache>();
	}
	private List<String> clearCacheQuestion(Map<String, QuestionCache> source) {
		long time = System.currentTimeMillis();
		List<String> keys = new ArrayList<String>();
		for (Map.Entry<String, QuestionCache> entry : source.entrySet()) {
			if (time - entry.getValue().getActive() > cacheConfig.getClearTime()) {
				keys.add(entry.getKey());
			}
		}
		for (String key : keys) {
			source.remove(key);
		}
		return keys;
	}
	private void clearCacheQuestion() {
		questionInfos = new HashMap<String, QuestionCache>();
	}
	private int clearCacheSbuject(SubjectCache source) {
		long time = System.currentTimeMillis();
		int clear = 0;
		if (time - source.getActive() > cacheConfig.getClearTime()) {
			source = new SubjectCache();
			clear++;
		}
		return clear;
	}
	private void clearCacheSbuject() {
		subjectCache = new SubjectCache();
	}
	private void clearCacheExamUnitInfo() {
		examUnitCache = new ExamUnitCache();
	}
	private void log(int count, int removeSize, String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(Thread.currentThread().getName());
		sb.append(":");
		sb.append(String.format("定时清理掉[%s] %d 个对象，剩余  %d 个", name, removeSize, count - removeSize));
		log.info(sb);
	}
	private void log(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(Thread.currentThread().getName());
		sb.append(":");
		sb.append(String.format("外部接口清空[%s]", name));
		log.info(sb);
	}
}
