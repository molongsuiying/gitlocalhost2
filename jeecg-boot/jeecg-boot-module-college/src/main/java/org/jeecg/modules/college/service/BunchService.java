package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Bunch;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.model.TreeModel;

import java.util.List;
import java.util.Map;

public interface BunchService extends IService<Bunch>{

    List<TreeModel> queryMyBunches(Map<String, Object> queryParam);
}
