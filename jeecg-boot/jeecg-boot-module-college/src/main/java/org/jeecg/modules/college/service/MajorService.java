package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.vo.MajorVo;

import java.util.List;
import java.util.Map;

public interface MajorService extends IService<Major>{

    long countList(Map<String,Object> queryParam);

    List<MajorVo> queryList(Map<String,Object> queryParam);

    List<TreeModel> queryMyMajors(Map<String,Object> queryParam);

    /**
     * 部门-专业 树结构
     * @return
     */
    List<TreeModel> queryTreeList();
}
