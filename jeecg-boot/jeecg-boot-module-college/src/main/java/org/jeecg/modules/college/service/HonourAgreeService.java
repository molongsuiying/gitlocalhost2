package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAgree;
import org.jeecg.modules.college.vo.AgreeVo;
import org.jeecg.modules.college.vo.HonourVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourAgreeService extends IService<HonourAgree>{

    List<HonourAgree> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<AgreeVo> transVo(List<HonourAgree> list);

    List<HonourAgree> queryListByIds(List<String> ids);

    List<AgreeVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<AgreeVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourAgree findById(String id);

}
