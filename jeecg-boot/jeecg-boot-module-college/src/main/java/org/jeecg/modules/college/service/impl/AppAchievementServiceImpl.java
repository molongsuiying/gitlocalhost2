package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.AppAchievement;
import org.jeecg.modules.college.entity.AppAchievement;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.*;
import org.jeecg.modules.college.service.AppAchievementService;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.FileVo;
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
public class AppAchievementServiceImpl extends ServiceImpl<AppAchievementMapper, AppAchievement> implements AppAchievementService{


    @Autowired
    private AppAchievementMapper appAchievementMapper;

    @Override
    public List<AppAchievement> queryList(Map<String, Object> params) {

        List<AppAchievement> files = appAchievementMapper.queryList(params);

        params.remove("order");
        params.remove("column");
        params.remove("field");

//        HonourQueryHistory history = new HonourQueryHistory();
//        history.setHonourType(CommonConstant.HONOUR_CLASS_FILE);
//        history.setQueryParam(params.toString());
//        historyMapper.insert(history);

        return files;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return appAchievementMapper.countList(params);
    }

//    @Override
//    public List<FileVo> transVo(List<AppAchievement> list) {
//        List<FileVo> vos = new ArrayList<>();
//        //
//        List<CommonVo> authority_types = appAchievement.queryDictMap("certificate_authority");
//        List<CommonVo> achievement_types = appAchievement.queryDictMap("achievement_type");
//        List<CommonVo> levels = appAchievement.queryDictMap("achievement_level");
//        List<CommonVo> honourLevels = appAchievement.queryDictMap("honour_level");
//
//        for (int i = 0; i < list.size(); i++) {
//            AppAchievement entity = list.get(i);
//            FileVo vo = new FileVo();
//            vo.setTitle(entity.getTitle());
//            vo.setLeader(entity.getLeader());
//            vo.setAcquireTime(entity.getAcquireTime());
//            vo.setMajorName(entity.getMajorName());
//            vo.setArticleNum(entity.getArticleNum());
//            vo.setTeamPersons(entity.getTeamPersons());
//            vo.setWorkName(entity.getWorkName());
//
//            if(entity.getHonourLevel() != null){
//                String honourLevel = honourLevels.stream()
//                        .filter(item -> item.getId().equals(entity.getHonourLevel().toString())).findFirst().get().getText();
//
//                vo.setHonourLevel(honourLevel);
//            }
//
//
//            //????????????
//            if(entity.getAuthorityType().equals(99)){
//                vo.setAuthorityType("??????("+entity.getAuthorityTxt()+")");
//            }else{
//                String value;
//                value = authority_types.stream()
//                        .filter(item -> item.getId().equals(entity.getAuthorityType().toString()))
//                        .findFirst().get().getText();
//                vo.setAuthorityType(value);
//            }
//            //????????????
//            if(entity.getAchievementType().equals(99)){
//                vo.setAchievementType("??????("+entity.getAchievementTypeTxt()+")");
//            }else{
//                String value;
//                value = achievement_types.stream()
//                        .filter(item -> item.getId().equals(entity.getAchievementType().toString()))
//                        .findFirst().get().getText();
//                vo.setAchievementType(value);
//            }
//
//            //????????????
//            if(entity.getAchievementLevel().equals(99)){
//                vo.setAchievementLevel("??????("+entity.getAchievementLevelTxt()+")");
//            }else{
//                String value;
//                value = levels.stream()
//                        .filter(item -> item.getId().equals(entity.getAchievementLevel().toString()))
//                        .findFirst().get().getText();
//                vo.setAchievementLevel(value);
//            }
//            vos.add(vo);
//        }
//        return vos;
//    }

//    @Override
//    public List<FileVo> transVoByExcel(List<List<Object>> list) throws ParseException {
//        List<FileVo> vos = new ArrayList<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        //??????list????????????????????????List???
//        for (int i = 0; i < list.size(); i++) {
//            List<Object> ob = list.get(i);
//            FileVo vo = new FileVo();
//            vo.setTitle(String.valueOf(ob.get(0)));
//            vo.setLeader(String.valueOf(ob.get(1)));
//            vo.setWorkName(String.valueOf(ob.get(2)));
//            vo.setHonourLevel(String.valueOf(ob.get(3)));
//            vo.setArticleNum(String.valueOf(ob.get(4)));
//            vo.setAuthorityType(String.valueOf(ob.get(5)));
//            vo.setAchievementType(String.valueOf(ob.get(6)));
//            vo.setAchievementLevel(String.valueOf(ob.get(7)));
//            vo.setAcquireTime(simpleDateFormat.parse(String.valueOf(ob.get(8))));
//            String teams = String.valueOf(ob.get(9));
//            teams = teams.replaceAll("???",",");
//            vo.setTeamPersons(teams);
//            vo.setMajorName(String.valueOf(ob.get(10)));
//            vos.add(vo);
//        }
//        return vos;
//    }

    @Override
    public List<AppAchievement> queryListByIds(List<String> ids) {
        return appAchievementMapper.queryListByIds(ids);
    }

    @Override
    @Transactional
    public void saveListByExcel(List<AppAchievement> files) {
        this.saveBatch(files);
    }

//    @Override
//    public void saveAppendixList(List<AppAchievement> vos) {
//        List<String> ids = vos.stream().map(AppAchievement::getId).collect(Collectors.toList());
//        List<AppAchievement> list = this.appAchievementMapper.queryListByIds(ids);
//        for (int i = 0; i < list.size(); i++) {
//            AppAchievement entity = list.get(i);
//            if(entity.getStatus() < 1){
//                List<String> appendixIds = map.get(list.get(i).getId());
//
//                String oldAppendixIds = entity.getAppendixIds();
//
//                String aIds = String.join(",", appendixIds);
//                if(StringUtils.isNotBlank(oldAppendixIds)){
//                    if(",".equals(oldAppendixIds.substring(oldAppendixIds.length()-1, oldAppendixIds.length()))){
//                        entity.setAppendixIds(oldAppendixIds+aIds);
//                    }else{
//                        entity.setAppendixIds(oldAppendixIds+","+aIds);
//                    }
//                }else{
//                    entity.setAppendixIds(aIds);
//                }
//            }
//
//        }
//        this.updateBatchById(list);
//    }

    @Override
    public AppAchievement findById(String id) {
        return appAchievementMapper.findById(id);
    }

    @Override
    public void removeByHonour(AppAchievement file) {
        this.baseMapper.deleteById(file.getId());
    }

}
