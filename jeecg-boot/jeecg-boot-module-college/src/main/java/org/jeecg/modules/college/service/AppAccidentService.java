package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.AppAccident;
import org.jeecg.modules.college.vo.FileVo;
import org.jeecg.modules.college.vo.HonourVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface AppAccidentService extends IService<AppAccident>{

    List<AppAccident> queryList(Map<String,Object> params);

    long countList(Map<String,Object> params);

//    List<FileVo> transVo(List<AppAccident> list);
//
//    List<FileVo> transVoByExcel(List<List<Object>> list) throws ParseException;

    List<AppAccident> queryListByIds(List<String> ids);

//    void saveListByExcel(List<AppAccident> vos);
//
//    void saveAppendixList(List<AppAccident> vos);

    AppAccident findById(String id);

    void removeByHonour(AppAccident file);
}
