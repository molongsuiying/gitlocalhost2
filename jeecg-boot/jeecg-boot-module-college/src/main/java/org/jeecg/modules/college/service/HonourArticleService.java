package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourArticle;
import org.jeecg.modules.college.vo.ArticleVo;
import org.jeecg.modules.college.vo.HonourVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourArticleService extends IService<HonourArticle>{

    List<HonourArticle> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<ArticleVo> transVo(List<HonourArticle> list);

    List<HonourArticle> queryListByIds(List<String> ids);

    List<ArticleVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<ArticleVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourArticle findById(String id);
}
