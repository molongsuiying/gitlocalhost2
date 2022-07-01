package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.AppAccident;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.AppAccidentMapper;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.AppAccidentService;
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
public class AppAccidentServiceImpl extends ServiceImpl<AppAccidentMapper, AppAccident> implements AppAccidentService{


    @Autowired
    private AppAccidentMapper AppAccidentMapper;

//    @Autowired
//    private HonourMapper honourMapper;

//    @Autowired
//    private MajorMapper majorMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Override
    public List<AppAccident> queryList(Map<String, Object> params) {

        List<AppAccident> files = AppAccidentMapper.queryList(params);

        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_FILE);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);

        return files;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return AppAccidentMapper.countList(params);
    }

//    @Override
//    public List<FileVo> transVo(List<AppAccident> list) {
//        List<FileVo> vos = new ArrayList<>();
//        //
//        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
//        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
//        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");
//        List<CommonVo> honourLevels = honourMapper.queryDictMap("honour_level");
//
//        for (int i = 0; i < list.size(); i++) {
//            AppAccident entity = list.get(i);
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
//            //发文机关
//            if(entity.getAuthorityType().equals(99)){
//                vo.setAuthorityType("其他("+entity.getAuthorityTxt()+")");
//            }else{
//                String value;
//                value = authority_types.stream()
//                        .filter(item -> item.getId().equals(entity.getAuthorityType().toString()))
//                        .findFirst().get().getText();
//                vo.setAuthorityType(value);
//            }
//            //成果类别
//            if(entity.getAchievementType().equals(99)){
//                vo.setAchievementType("其他("+entity.getAchievementTypeTxt()+")");
//            }else{
//                String value;
//                value = achievement_types.stream()
//                        .filter(item -> item.getId().equals(entity.getAchievementType().toString()))
//                        .findFirst().get().getText();
//                vo.setAchievementType(value);
//            }
//
//            //成果等级
//            if(entity.getAchievementLevel().equals(99)){
//                vo.setAchievementLevel("其他("+entity.getAchievementLevelTxt()+")");
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
//
//    @Override
//    public List<FileVo> transVoByExcel(List<List<Object>> list) throws ParseException {
//        List<FileVo> vos = new ArrayList<>();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        //遍历list数据，把数据放到List中
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
//            teams = teams.replaceAll("，",",");
//            vo.setTeamPersons(teams);
//            vo.setMajorName(String.valueOf(ob.get(10)));
//            vos.add(vo);
//        }
//        return vos;
//    }

    @Override
    public List<AppAccident> queryListByIds(List<String> ids) {
        return AppAccidentMapper.queryListByIds(ids);
    }

//    @Override
//    @Transactional
//    public void saveListByExcel(List<FileVo> vos) {
//
//        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
//        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
//        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");
//
//        List<CommonVo> honourLevels = honourMapper.queryDictMap("honour_level");
//
//        List<AppAccident> files = new ArrayList<>();
//        for (int i = 0; i < vos.size(); i++) {
//            FileVo vo = vos.get(i);
//            AppAccident file = new AppAccident();
//            file.setTitle(vo.getTitle());
//            file.setArticleNum(vo.getArticleNum());
//            file.setTeamPersons(vo.getTeamPersons());
//            file.setWorkName(vo.getWorkName());
//            String honourLevel;
//            honourLevel = honourLevels.stream()
//                    .filter(item -> item.getText().equals(vo.getHonourLevel()))
//                    .findFirst().get().getId();
//            if(StringUtils.isNotBlank(honourLevel)){
//                file.setHonourLevel(Integer.parseInt(honourLevel));
//            }
//
//            if(vo.getAuthorityType().contains("其他")){
//                file.setAuthorityType(99);
//                String text = vo.getAuthorityType().substring(vo.getAuthorityType().indexOf("-") + 1);
//                file.setAuthorityTxt(text);
//            }else{
//                String key;
//                key = authority_types.stream()
//                        .filter(item -> item.getText().equals(vo.getAuthorityType()))
//                        .findFirst().get().getId();
//                if(StringUtils.isNotBlank(key)){
//                    file.setAuthorityType(Integer.parseInt(key));
//                }
//            }
//
//
//            if(vo.getAchievementType().contains("其他")){
//                file.setAchievementType(99);
//                String text = vo.getAchievementType().substring(vo.getAchievementType().indexOf("-") + 1);
//                file.setAchievementTypeTxt(text);
//            }else{
//                String key;
//                key = achievement_types.stream()
//                        .filter(item -> item.getText().equals(vo.getAchievementType()))
//                        .findFirst().get().getId();
//                if(StringUtils.isNotBlank(key)){
//                    file.setAchievementType(Integer.parseInt(key));
//                }
//            }
//
//
//            if(vo.getAchievementLevel().contains("其他")){
//                file.setAchievementLevel(99);
//                String text = vo.getAchievementLevel().substring(vo.getAchievementLevel().indexOf("-") + 1);
//                file.setAchievementLevelTxt(text);
//            }else{
//                String key;
//                key = levels.stream()
//                        .filter(item -> item.getText().equals(vo.getAchievementLevel()))
//                        .findFirst().get().getId();
//                if(StringUtils.isNotBlank(key)){
//                    file.setAchievementLevel(Integer.parseInt(key));
//                }
//
//            }
//
//            String majorId = majorMapper.findIdByName(vo.getMajorName());
//            file.setMajorId(majorId);
//
//            Integer status = CommonConstant.HONOUR_NO_SUBMIT;
//            if(CommonConstant.HONOUR_EXAMINE_ING.equals(vo.getStatus())){
//                status = CommonConstant.HONOUR_EXAMINE_ING;
//            }
//            file.setLeader(vo.getLeader());
//            file.setAcquireTime(vo.getAcquireTime());
//            file.setStatus(status);
//            files.add(file);
//        }
//        this.saveBatch(files);
//    }

//    @Override
//    public void saveAppendixList(List<HonourVo> vos) {
//        List<String> ids = vos.stream().map(HonourVo::getId).collect(Collectors.toList());
//
//        Map<String,List<String>> map = new HashMap<>();
//        for (int i = 0; i < vos.size(); i++) {
//            map.put(vos.get(i).getId(),vos.get(i).getAppendixIds());
//        }
//
//        List<AppAccident> list = this.AppAccidentMapper.queryListByIds(ids);
//        for (int i = 0; i < list.size(); i++) {
//            AppAccident entity = list.get(i);
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
    public AppAccident findById(String id) {
        return AppAccidentMapper.findById(id);
    }

    @Override
    public void removeByHonour(AppAccident file) {
        this.baseMapper.deleteById(file.getId());
    }

}
