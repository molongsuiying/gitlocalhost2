package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.HonourProject;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourProjectMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.HonourProjectService;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.ProjectVo;
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
public class HonourProjectServiceImpl extends ServiceImpl<HonourProjectMapper, HonourProject> implements HonourProjectService{


    @Autowired
    private HonourProjectMapper projectMapper;

    @Autowired
    private HonourMapper honourMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Override
    public List<HonourProject> queryList(Map<String, Object> params) {

        List<HonourProject> projects = projectMapper.queryList(params);
        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_PROJECT);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);
        return projects;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return projectMapper.countList(params);
    }

    @Override
    public List<ProjectVo> transVo(List<HonourProject> list) {
        List<ProjectVo> vos = new ArrayList<>();
        //
        List<CommonVo> honour_levels = honourMapper.queryDictMap("honour_level");
        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");


        for (int i = 0; i < list.size(); i++) {
            HonourProject entity = list.get(i);
            ProjectVo vo = new ProjectVo();
            vo.setTitle(entity.getTitle());
            vo.setLeader(entity.getLeader());
            vo.setAcquireTime(entity.getAcquireTime());
            vo.setMajorName(entity.getMajorName());

            vo.setTeamPersons(entity.getTeamPersons());
            if(StringUtils.isNotBlank(entity.getStudentName())){
                vo.setStudentName(entity.getStudentName());
            }
            vo.setTeacherName(entity.getTeacherName());
            vo.setBuildProjectCharge(entity.getBuildProjectCharge());
            vo.setOverProjectCharge(entity.getOverProjectCharge());

            //荣誉等级
            if(entity.getHonourLevel().equals(99)){
                vo.setHonourLevel("其他("+entity.getHonourLevel()+")");
            }else{
                String value;
                value = honour_levels.stream()
                        .filter(item -> item.getId().equals(entity.getHonourLevel().toString()))
                        .findFirst().get().getText();
                vo.setHonourLevel(value);
            }
            //颁发机关
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
    public List<HonourProject> queryListByIds(List<String> ids) {
        return projectMapper.queryListByIds(ids);
    }

    @Override
    public List<ProjectVo> transVoByExcel(List<List<Object>> list) throws ParseException {
        List<ProjectVo> vos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历list数据，把数据放到List中
        for (int i = 0; i < list.size(); i++) {
            List<Object> ob = list.get(i);
            ProjectVo vo = new ProjectVo();
            vo.setTitle(String.valueOf(ob.get(0)));
            vo.setLeader(String.valueOf(ob.get(1)));
            vo.setStudentName(String.valueOf(ob.get(2)));
            vo.setTeacherName(String.valueOf(ob.get(3)));
            vo.setBuildProjectCharge(String.valueOf(ob.get(4)));
            vo.setOverProjectCharge(String.valueOf(ob.get(5)));
            vo.setHonourLevel(String.valueOf(ob.get(6)));
            vo.setAuthorityType(String.valueOf(ob.get(7)));
            vo.setAchievementType(String.valueOf(ob.get(8)));
            vo.setAchievementLevel(String.valueOf(ob.get(9)));
            vo.setAcquireTime(simpleDateFormat.parse(String.valueOf(ob.get(10))));

            String teams = String.valueOf(ob.get(11));
            teams = teams.replaceAll("，",",");
            vo.setTeamPersons(teams);
            vo.setMajorName(String.valueOf(ob.get(12)));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public void saveListByExcel(List<ProjectVo> vos) {
        List<CommonVo> honour_levels = honourMapper.queryDictMap("honour_level");
        List<CommonVo> authority_types = honourMapper.queryDictMap("certificate_authority");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");

        List<HonourProject> list = new ArrayList<>();
        for (int i = 0; i < vos.size(); i++) {
            ProjectVo vo = vos.get(i);
            HonourProject entity = new HonourProject();
            entity.setTitle(vo.getTitle());
            entity.setLeader(vo.getLeader());
            entity.setStudentName(vo.getStudentName());
            entity.setTeacherName(vo.getTeacherName());
            entity.setBuildProjectCharge(vo.getBuildProjectCharge());
            entity.setOverProjectCharge(vo.getOverProjectCharge());
            entity.setTeamPersons(vo.getTeamPersons());
            String value;
            value = honour_levels.stream()
                    .filter(item -> item.getText().equals(vo.getHonourLevel()))
                    .findFirst().get().getId();
            if(StringUtils.isNotBlank(value)){
                entity.setHonourLevel(Integer.parseInt(value));
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

        List<HonourProject> list = this.projectMapper.queryListByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            HonourProject entity = list.get(i);
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
    public HonourProject findById(String id) {
        return projectMapper.findById(id);
    }
}
