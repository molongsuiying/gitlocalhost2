package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.mapper.DepartmentMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.MajorService;
import org.jeecg.modules.college.vo.DepartVo;
import org.jeecg.modules.college.vo.MajorVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService{


    @Resource
    private DepartmentMapper departmentMapper;
    @Override
    public long countList(Map<String, Object> queryParam) {
        return this.baseMapper.countList(queryParam);
    }

    @Override
    public List<MajorVo> queryList(Map<String, Object> queryParam) {
        return this.baseMapper.queryList(queryParam);
    }

    @Override
    public List<TreeModel> queryMyMajors(Map<String, Object> queryParam) {
        List<TreeModel> treeModels = new ArrayList<>();
        List<MajorVo> majorVos = this.baseMapper.findListByDepartmentId(queryParam);
        for (int i = 0; i < majorVos.size(); i++) {
            TreeModel treeModel = new TreeModel(majorVos.get(i));
            treeModels.add(treeModel);
        }
        return treeModels;
    }

    @Override
    public List<TreeModel> queryTreeList() {
        List<TreeModel> treeList = new ArrayList<>();
        List<Department> vos = departmentMapper.selectList(new QueryWrapper<>());

        for (int i = 0; i < vos.size(); i++) {
            List<TreeModel> children = new ArrayList<>();
            TreeModel treeModel = new TreeModel(vos.get(i));
            treeModel.setIsLeaf(true);
            Map<String, Object> queryParam = new HashMap<>(1);
            queryParam.put("departmentId",treeModel.getKey());
            List<MajorVo> majorVos = this.baseMapper.findListByDepartmentId(queryParam);
            for (int j = 0; j < majorVos.size(); j++) {
                TreeModel child = new TreeModel(majorVos.get(j));
                if(child.getTitle().equals(treeModel.getTitle())){
                    treeModel.setKey(child.getKey());
                    treeModel.setValue(child.getValue());
                }else{
                    children.add(child);
                }

            }
            if(children.size() > 0){
                treeModel.setIsLeaf(false);
            }
            treeModel.setChildren(children);
            treeList.add(treeModel);
        }

        return treeList;
    }
}
