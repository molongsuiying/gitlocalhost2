package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.college.entity.Bunch;
import org.jeecg.modules.college.mapper.BunchMapper;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.BunchService;
import org.jeecg.modules.college.vo.BunchVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class BunchServiceImpl extends ServiceImpl<BunchMapper, Bunch> implements BunchService{

    @Override
    public List<TreeModel> queryMyBunches(Map<String, Object> queryParam) {
        List<TreeModel> treeModels = new ArrayList<>();
        List<BunchVo> vos = this.baseMapper.findListByMajorId(queryParam);
        for (int i = 0; i < vos.size(); i++) {
            TreeModel treeModel = new TreeModel(vos.get(i));
            treeModels.add(treeModel);
        }
        return treeModels;
    }
}
