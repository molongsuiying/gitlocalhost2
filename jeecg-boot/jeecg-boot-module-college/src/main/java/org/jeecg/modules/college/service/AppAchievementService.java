package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.AppAchievement;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface AppAchievementService extends IService<AppAchievement>{

    List<AppAchievement> queryList(Map<String,Object> params);

    long countList(Map<String,Object> params);

//    List<AppAchievement> transVo(List<AppAchievement> list);
//
//    List<AppAchievement> transVoByExcel(List<List<Object>> list) throws ParseException;

    List<AppAchievement> queryListByIds(List<String> ids);

    void saveListByExcel(List<AppAchievement> vos);

//    void saveAppendixList(List<AppAchievement> vos);

    AppAchievement findById(String id);

    void removeByHonour(AppAchievement file);
}
