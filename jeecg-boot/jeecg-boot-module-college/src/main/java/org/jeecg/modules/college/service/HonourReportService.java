package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourReport;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.ReportVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourReportService extends IService<HonourReport>{

    List<HonourReport> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<ReportVo> transVo(List<HonourReport> list);

    List<HonourReport> queryListByIds(List<String> ids);

    List<ReportVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<ReportVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourReport findById(String id);
}
