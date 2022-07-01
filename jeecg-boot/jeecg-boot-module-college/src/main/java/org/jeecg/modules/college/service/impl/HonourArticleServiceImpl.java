package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.HonourArticle;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.HonourArticleMapper;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.HonourArticleService;
import org.jeecg.modules.college.vo.ArticleVo;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourArticleServiceImpl extends ServiceImpl<HonourArticleMapper, HonourArticle> implements HonourArticleService {


    @Autowired
    private HonourArticleMapper articleMapper;

    @Autowired
    private HonourMapper honourMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Override
    public List<HonourArticle> queryList(Map<String, Object> params) {
        List<HonourArticle> articles = articleMapper.queryList(params);


        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_ARTICLE);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);

        return articles;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return articleMapper.countList(params);
    }

    @Override
    public List<ArticleVo> transVo(List<HonourArticle> list) {
        List<ArticleVo> vos = new ArrayList<>();
        //
        List<CommonVo> article_types = honourMapper.queryDictMap("article_type");
        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");


        for (int i = 0; i < list.size(); i++) {
            HonourArticle entity = list.get(i);
            ArticleVo vo = new ArticleVo();
            vo.setTitle(entity.getTitle());
            vo.setLeader(entity.getLeader());
            vo.setAcquireTime(entity.getAcquireTime());
            vo.setMajorName(entity.getMajorName());

            vo.setTeamPersons(entity.getTeamPersons());

            //物图类别
            if(entity.getArticleType().equals(99)){
                vo.setArticleType("其他("+entity.getArticleTypeTxt()+")");
            }else{
                String value;
                value = article_types.stream()
                        .filter(item -> item.getId().equals(entity.getArticleType().toString()))
                        .findFirst().get().getText();
                vo.setArticleType(value);
            }
            if(entity.getAuthorityType().equals(99)){
                vo.setAuthorityType("其他("+entity.getAuthorityTxt()+")");
            }else{
                String value;
                value = authority_types.stream()
                        .filter(item -> item.getId().equals(entity.getAuthorityType().toString()))
                        .findFirst().get().getText();
                vo.setAuthorityType(value);
            }
            //成果类别
            if(entity.getAchievementType().equals(99)){
                vo.setAchievementType("其他("+entity.getAchievementTypeTxt()+")");
            }else{
                String value;
                value = achievement_types.stream()
                        .filter(item -> item.getId().equals(entity.getAchievementType().toString()))
                        .findFirst().get().getText();
                vo.setAchievementType(value);
            }

            //成果等级
            if(entity.getAchievementLevel().equals(99)){
                vo.setAchievementLevel("其他("+entity.getAchievementLevelTxt()+")");
            }else{
                String value;
                value = levels.stream()
                        .filter(item -> item.getId().equals(entity.getAchievementLevel().toString()))
                        .findFirst().get().getText();
                vo.setAchievementLevel(value);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<HonourArticle> queryListByIds(List<String> ids) {
        return articleMapper.queryListByIds(ids);
    }

    @Override
    public List<ArticleVo> transVoByExcel(List<List<Object>> list) throws ParseException {
        List<ArticleVo> vos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历list数据，把数据放到List中
        for (int i = 0; i < list.size(); i++) {
            List<Object> ob = list.get(i);
            ArticleVo vo = new ArticleVo();
            vo.setTitle(String.valueOf(ob.get(0)));
            vo.setLeader(String.valueOf(ob.get(1)));
            vo.setArticleType(String.valueOf(ob.get(2)));
            vo.setAuthorityType(String.valueOf(ob.get(3)));
            vo.setAchievementType(String.valueOf(ob.get(4)));
            vo.setAchievementLevel(String.valueOf(ob.get(5)));
            vo.setAcquireTime(simpleDateFormat.parse(String.valueOf(ob.get(6))));

            String teams = String.valueOf(ob.get(7));
            teams = teams.replaceAll("，",",");
            vo.setTeamPersons(teams);
            vo.setMajorName(String.valueOf(ob.get(8)));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public void saveListByExcel(List<ArticleVo> vos) {

        List<CommonVo> article_types = honourMapper.queryDictMap("article_type");
        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");

        List<HonourArticle> list = new ArrayList<>();
        for (int i = 0; i < vos.size(); i++) {
            ArticleVo vo = vos.get(i);
            HonourArticle entity = new HonourArticle();
            entity.setTitle(vo.getTitle());

            entity.setLeader(vo.getLeader());
            entity.setTeamPersons(vo.getTeamPersons());

            if(vo.getArticleType().contains("其他")){
                entity.setArticleType(99);
                String text = vo.getArticleType().substring(vo.getArticleType().indexOf("-") + 1);
                entity.setArticleTypeTxt(text);
            }else{
                String key;
                key = article_types.stream()
                        .filter(item -> item.getText().equals(vo.getArticleType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setArticleType(Integer.parseInt(key));
                }
            }

            if(vo.getAuthorityType().contains("其他")){
                entity.setAuthorityType(99);
                String text = vo.getAuthorityType().substring(vo.getAuthorityType().indexOf("-") + 1);
                entity.setAuthorityTxt(text);
            }else{
                String key;
                key = authority_types.stream()
                        .filter(item -> item.getText().equals(vo.getAuthorityType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setAuthorityType(Integer.parseInt(key));
                }
            }


            if(vo.getAchievementType().contains("其他")){
                entity.setAchievementType(99);
                String text = vo.getAchievementType().substring(vo.getAchievementType().indexOf("-") + 1);
                entity.setAchievementTypeTxt(text);
            }else{
                String key;
                key = achievement_types.stream()
                        .filter(item -> item.getText().equals(vo.getAchievementType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setAchievementType(Integer.parseInt(key));
                }
            }


            if(vo.getAchievementLevel().contains("其他")){
                entity.setAchievementLevel(99);
                String text = vo.getAchievementLevel().substring(vo.getAchievementLevel().indexOf("-") + 1);
                entity.setAchievementLevelTxt(text);
            }else{
                String key;
                key = levels.stream()
                        .filter(item -> item.getText().equals(vo.getAchievementLevel()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setAchievementLevel(Integer.parseInt(key));
                }

            }

            String majorId = majorMapper.findIdByName(vo.getMajorName());
            entity.setMajorId(majorId);

            Integer status = CommonConstant.HONOUR_NO_SUBMIT;
            if(CommonConstant.HONOUR_EXAMINE_ING.equals(vo.getStatus())){
                status = CommonConstant.HONOUR_EXAMINE_ING;
            }

            entity.setAcquireTime(vo.getAcquireTime());
            entity.setStatus(status);
            list.add(entity);
        }
        this.saveBatch(list);
    }

    @Override
    public void saveAppendixList(List<HonourVo> vos) {
        List<String> ids = vos.stream().map(HonourVo::getId).collect(Collectors.toList());

        Map<String,List<String>> map = new HashMap<>();
        for (int i = 0; i < vos.size(); i++) {
            map.put(vos.get(i).getId(),vos.get(i).getAppendixIds());
        }

        List<HonourArticle> list = this.articleMapper.queryListByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            HonourArticle entity = list.get(i);
            if(entity.getStatus() < 1){
                List<String> appendixIds = map.get(list.get(i).getId());

                String oldAppendixIds = entity.getAppendixIds();

                String aIds = String.join(",", appendixIds);
                if(StringUtils.isNotBlank(oldAppendixIds)){
                    if(",".equals(oldAppendixIds.substring(oldAppendixIds.length()-1, oldAppendixIds.length()))){
                        entity.setAppendixIds(oldAppendixIds+aIds);
                    }else{
                        entity.setAppendixIds(oldAppendixIds+","+aIds);
                    }
                }else{
                    entity.setAppendixIds(aIds);
                }
            }

        }
        this.updateBatchById(list);
    }

    @Override
    public HonourArticle findById(String id) {
        return articleMapper.findById(id);
    }
}
