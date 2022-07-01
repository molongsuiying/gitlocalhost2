package org.jeecg.modules.college.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.vo.HonourEntityVo;
import org.jeecg.modules.college.vo.RankVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface HonourService extends IService<Honour>{

    List<HonourEntityVo> findHonourAllList(Map<String,Object> params);

    List<String> findValuesByHonour(Map<String,Object> params);

    long countHonourAllList(Map<String,Object> params);

    List<RankVo> countRankList();

    JSONObject findDataVoList(Integer type);

    Boolean findDuplicateItemByTitle(String id,Map<String,Object> params);
}
