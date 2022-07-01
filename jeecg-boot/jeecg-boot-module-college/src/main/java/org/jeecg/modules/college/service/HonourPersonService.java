package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourPerson;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.PersonVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourPersonService extends IService<HonourPerson>{

    List<HonourPerson> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<PersonVo> transVo(List<HonourPerson> list);

    List<HonourPerson> queryListByIds(@Param("ids")List<String> ids);

    List<PersonVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<PersonVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourPerson findById(String id);
}
