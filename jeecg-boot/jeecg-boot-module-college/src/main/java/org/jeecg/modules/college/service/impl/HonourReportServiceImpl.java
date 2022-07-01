package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.entity.HonourReport;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.HonourReportMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.HonourReportService;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.ReportVo;
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
public class HonourReportServiceImpl extends ServiceImpl<HonourReportMapper, HonourReport> implements HonourReportService {


    @Autowired
    private HonourReportMapper reportMapper;

    @Autowired
    private HonourMapper honourMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Override
    public List<HonourReport> queryList(Map<String, Object> params) {
        List<HonourReport> reports = reportMapper.queryList(params);

        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_REPORT);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);
        return reports;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return reportMapper.countList(params);
    }

    @Override
    public List<ReportVo> transVo(List<HonourReport> list) {
        List<ReportVo> vos = new ArrayList<>();
        //
        List<CommonVo> medium_types = honourMapper.queryDictMap("medium_type");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");


        for (int i = 0; i < list.size(); i++) {
            HonourReport report = list.get(i);
            ReportVo vo = new ReportVo();
            vo.setTitle(report.getTitle());
            vo.setLeader(report.getLeader());
            vo.setAcquireTime(report.getAcquireTime());
            vo.setAuthor(report.getAuthor());
            vo.setMediumName(report.getMediumName());
            vo.setMajorName(report.getMajorName());

            vo.setTeamPersons(report.getTeamPersons());
            //媒体类型
            if(report.getMediumType().equals(99)){
                vo.setMediumType("其他("+report.getMediumTypeTxt()+")");
            }else{
                String value;
                value = medium_types.stream()
                        .filter(item -> item.getId().equals(report.getMediumType().toString()))
                        .findFirst().get().getText();
                vo.setMediumType(value);
            }
            //成果类别
            if(report.getAchievementType().equals(99)){
                vo.setAchievementType("其他("+report.getAchievementTypeTxt()+")");
            }else{
                String value;
                value = achievement_types.stream()
                        .filter(item -> item.getId().equals(report.getAchievementType().toString()))
                        .findFirst().get().getText();
                vo.setAchievementType(value);
            }

            //成果等级
            if(report.getAchievementLevel().equals(99)){
                vo.setAchievementLevel("其他("+report.getAchievementLevelTxt()+")");
            }else{
                String value;
                value = levels.stream()
                        .filter(item -> item.getId().equals(report.getAchievementLevel().toString()))
                        .findFirst().get().getText();
                vo.setAchievementLevel(value);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<HonourReport> queryListByIds(List<String> ids) {
        return reportMapper.queryListByIds(ids);
    }

    @Override
    public List<ReportVo> transVoByExcel(List<List<Object>> list) throws ParseException {
        List<ReportVo> vos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历list数据，把数据放到List中
        for (int i = 0; i < list.size(); i++) {
            List<Object> ob = list.get(i);
            ReportVo vo = new ReportVo();
            vo.setTitle(String.valueOf(ob.get(0)));
            vo.setLeader(String.valueOf(ob.get(1)));
            vo.setMediumType(String.valueOf(ob.get(2)));
            vo.setMediumName(String.valueOf(ob.get(3)));
            vo.setAuthor(String.valueOf(ob.get(4)));
            vo.setAchievementType(String.valueOf(ob.get(5)));
            vo.setAchievementLevel(String.valueOf(ob.get(6)));
            vo.setAcquireTime(simpleDateFormat.parse(String.valueOf(ob.get(7))));

            String teams = String.valueOf(ob.get(8));
            teams = teams.replaceAll("，",",");
            vo.setTeamPersons(teams);

            vo.setMajorName(String.valueOf(ob.get(9)));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public void saveListByExcel(List<ReportVo> vos) {

        List<CommonVo> medium_types = honourMapper.queryDictMap("medium_type");
        List<CommonVo> achievement_types = honourMapper.queryDictMap("achievement_type");
        List<CommonVo> levels = honourMapper.queryDictMap("achievement_level");

        List<HonourReport> list = new ArrayList<>();
        for (int i = 0; i < vos.size(); i++) {
            ReportVo vo = vos.get(i);
            HonourReport entity = new HonourReport();
            entity.setTitle(vo.getTitle());
            entity.setLeader(vo.getLeader());
            entity.setMediumName(vo.getMediumName());
            entity.setAuthor(vo.getAuthor());
            entity.setTeamPersons(vo.getTeamPersons());
            if(vo.getMediumType().contains("其他")){
                entity.setMediumType(99);
                String text = vo.getMediumType().substring(vo.getMediumType().indexOf("-") + 1);
                entity.setMediumTypeTxt(text);
            }else{
                String key;
                key = medium_types.stream()
                        .filter(item -> item.getText().equals(vo.getMediumType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setMediumType(Integer.parseInt(key));
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

        List<HonourReport> list = this.reportMapper.queryListByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            HonourReport entity = list.get(i);
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
    public HonourReport findById(String id) {
        return reportMapper.findById(id);
    }
}
