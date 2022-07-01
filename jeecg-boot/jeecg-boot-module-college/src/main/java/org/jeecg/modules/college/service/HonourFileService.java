package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.entity.HonourFile;
import org.jeecg.modules.college.vo.FileVo;
import org.jeecg.modules.college.vo.HonourVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface HonourFileService extends IService<HonourFile>{

    List<HonourFile> queryList(Map<String,Object> params);

    long countList(Map<String,Object> params);

    List<FileVo> transVo(List<HonourFile> list);

    List<FileVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    List<HonourFile> queryListByIds(List<String> ids);

    void saveListByExcel(List<FileVo> vos);

    void saveAppendixList(List<HonourVo> vos);

    HonourFile findById(String id);

    void removeByHonour(HonourFile file);

    Map<String, Object> getApplyForm(String tableId, String tableName);

    HonourFile queryList(String id);
}
