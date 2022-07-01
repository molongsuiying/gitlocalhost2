package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourProject;
import org.jeecg.modules.college.vo.HonourVo;
import org.jeecg.modules.college.vo.ProjectVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourProjectService extends IService<HonourProject>{

    List<HonourProject> queryList(Map<String, Object> params);

    long countList(Map<String, Object> params);

    List<ProjectVo> transVo(List<HonourProject> list);

    List<HonourProject> queryListByIds(List<String> ids);

    List<ProjectVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    void saveListByExcel(List<ProjectVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourProject findById(String id);
}
