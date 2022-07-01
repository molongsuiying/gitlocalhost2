package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourCertificate;
import org.jeecg.modules.college.vo.CerVo;
import org.jeecg.modules.college.vo.HonourVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourCertificateService extends IService<HonourCertificate>{

    List<HonourCertificate> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<CerVo> transVo(List<HonourCertificate> list);

    List<HonourCertificate> queryListByIds(List<String> ids);

    List<CerVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<CerVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourCertificate findById(String id);
}
