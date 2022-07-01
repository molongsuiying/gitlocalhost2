package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.HonourPerson;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.mapper.HonourPersonMapper;
import org.jeecg.modules.college.mapper.HonourQueryHistoryMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.service.HonourPersonService;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.PersonVo;
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
public class HonourPersonServiceImpl extends ServiceImpl<HonourPersonMapper, HonourPerson> implements HonourPersonService {


    @Autowired
    private HonourPersonMapper personMapper;

    @Autowired
    private HonourMapper honourMapper;

    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;


    @Override
    public List<HonourPerson> queryList(Map<String, Object> params) {

        List<HonourPerson> people =personMapper.queryList(params);
        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_PERSON);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);
        return people;
    }

    @Override
    public long countList(Map<String, Object> params) {
        return personMapper.countList(params);
    }

    @Override
    public List<PersonVo> transVo(List<HonourPerson> list) {
        List<PersonVo> vos = new ArrayList<>();
        List<CommonVo> commonVos = honourMapper.queryDictMap("certificate_type");
        for (int i = 0; i < list.size(); i++) {
            HonourPerson person = list.get(i);
            PersonVo vo = new PersonVo();
            vo.setTitle(person.getTitle());
            vo.setLeader(person.getLeader());
            vo.setAcquireTime(person.getAcquireTime());
            vo.setAcquireUnit(person.getAcquireUnit());
            vo.setMajorName(person.getMajorName());
            vo.setTeamPersons(person.getTeamPersons());
            if(person.getCertificateType().equals(99)){
                vo.setCertificateType("其他("+person.getCertificateTypeTxt()+")");
            }else{
                String value;
                value = commonVos.stream()
                        .filter(item -> item.getId().equals(person.getCertificateType().toString()))
                        .findFirst().get().getText();
                vo.setCertificateType(value);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<HonourPerson> queryListByIds(List<String> ids) {
        return personMapper.queryListByIds(ids);
    }

    @Override
    public List<PersonVo> transVoByExcel(List<List<Object>> list) throws ParseException {
        List<PersonVo> vos = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //遍历list数据，把数据放到List中
        for (int i = 0; i < list.size(); i++) {
            List<Object> ob = list.get(i);
            PersonVo vo = new PersonVo();
            vo.setTitle(String.valueOf(ob.get(0)));
            vo.setLeader(String.valueOf(ob.get(1)));
            vo.setCertificateType(String.valueOf(ob.get(2)));
            vo.setAcquireUnit(String.valueOf(ob.get(3)));
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
    public void saveListByExcel(List<PersonVo> vos) {
        List<HonourPerson> list = new ArrayList<>();
        List<CommonVo> commonVos = honourMapper.queryDictMap("certificate_type");

        for (int i = 0; i < vos.size(); i++) {
            PersonVo vo = vos.get(i);
            HonourPerson entity = new HonourPerson();
            entity.setTitle(vo.getTitle());
            entity.setLeader(vo.getLeader());
            entity.setAcquireTime(vo.getAcquireTime());
            entity.setAcquireUnit(vo.getAcquireUnit());

            entity.setTeamPersons(vo.getTeamPersons());
            if(vo.getCertificateType().contains("其他")){
                entity.setCertificateType(99);
                String text = vo.getCertificateType().substring(vo.getCertificateType().indexOf("-") + 1);
                entity.setCertificateTypeTxt(text);
            }else{
                String key;
                key = commonVos.stream()
                        .filter(item -> item.getText().equals(vo.getCertificateType()))
                        .findFirst().get().getId();
                if(StringUtils.isNotBlank(key)){
                    entity.setCertificateType(Integer.parseInt(key));
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

        List<HonourPerson> list = this.personMapper.queryListByIds(ids);
        for (int i = 0; i < list.size(); i++) {
            HonourPerson entity = list.get(i);
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
    public HonourPerson findById(String id) {
        return personMapper.findById(id);
    }
}
