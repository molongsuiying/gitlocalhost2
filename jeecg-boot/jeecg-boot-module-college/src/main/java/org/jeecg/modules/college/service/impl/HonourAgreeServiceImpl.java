package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.HonourAgree;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.HonourAgreeMapper;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.HonourAgreeService;
import org.jeecg.modules.college.vo.AgreeVo;
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
public class HonourAgreeServiceImpl extends ServiceImpl<HonourAgreeMapper, HonourAgree> implements HonourAgreeService {


    @Autowired
    private HonourAgreeMapper agreeMapper;

    @Autowired
    private HonourMapper honourMapper;

    @Autowired
    private MajorMapper majorMapper;
    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Override
    public List<HonourAgree> queryList(Map<String, Object> params) {
        List<HonourAgree> agrees = agreeMapper.queryList(params);

        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_AGREE);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);
        return agrees;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return agreeMapper.countList(params);
    }

    @Override
    public List<AgreeVo> transVo(List<HonourAgree> list) {
        List<AgreeVo> vos = new ArrayList<>();
        List<CommonVo> commonVos = honourMapper.queryDictMap("cooperation_type");
        for (int i = 0; i < list.size(); i++) {
            HonourAgree agree = list.get(i);
            AgreeVo vo = new AgreeVo();
            vo.setTitle(agree.getTitle());
            vo.setLeader(agree.getLeader());
            vo.setAcquireTime(agree.getAcquireTime());
            vo.setCooperationUnit(agree.getCooperationUnit());
            vo.setMajorName(agree.getMajorName());
            vo.setTeamPersons(agree.getTeamPersons());
            if(agree.getCooperationType().equals(99)){
                vo.setCooperationType("其他("+agree.getCooperationTypeTxt()+")");
            }else{
                String value;
                value = commonVos.stream()
                        .filter(item -> item.getId().equals(agree.getCooperationType().toString()))
                        .findFirst().get().getText();
                vo.setCooperationType(value);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<HonourAgree> queryListByIds(List<String> ids) {
        return agreeMapper.queryListByIds(ids);
    }

    @Override
    public List<AgreeVo> transVoByExcel(List<List<Object>> list) throws ParseException {
        List<AgreeVo> vos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历list数据，把数据放到List中
        for (int i = 0; i < list.size(); i++) {
            List<Object> ob = list.get(i);
            AgreeVo vo = new AgreeVo();
            vo.setTitle(String.valueOf(ob.get(0)));
            vo.setLeader(String.valueOf(ob.get(1)));
            vo.setCooperationType(String.valueOf(ob.get(2)));
            vo.setCooperationUnit(String.valueOf(ob.get(3)));
            vo.setAcquireTime(simpleDateFormat.parse(String.valueOf(ob.get(4))));
            String teams = String.valueOf(ob.get(5));
            teams = teams.replaceAll("，",",");
            vo.setTeamPersons(teams);
            vo.setMajorName(String.valueOf(ob.get(6)));
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public void saveListByExcel(List<AgreeVo> vos) {
        List<HonourAgree> list = new ArrayList<>();
        List<CommonVo> commonVos = honourMapper.queryDictMap("cooperation_type");

        for (int i = 0; i < vos.size(); i++) {
            AgreeVo vo = vos.get(i);
            HonourAgree entity = new HonourAgree();
            entity.setTitle(vo.getTitle());
            entity.setLeader(vo.getLeader());
            entity.setAcquireTime(vo.getAcquireTime());
            entity.setCooperationUnit(vo.getCooperationUnit());

            entity.setTeamPersons(vo.getTeamPersons());
            if(vo.getCooperationType().contains("其他")){
                entity.setCooperationType(99);
                String text = vo.getCooperationType().substring(vo.getCooperationType().indexOf("-") + 1);
                entity.setCooperationTypeTxt(text);
            }else{
                String key;
                key = commonVos.stream()
                        .filter(item -> item.getText().equals(vo.getCooperationType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setCooperationType(Integer.parseInt(key));
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

        List<HonourAgree> list = this.agreeMapper.queryListByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            HonourAgree entity = list.get(i);
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
    public HonourAgree findById(String id) {
        return agreeMapper.findById(id);
    }
}
