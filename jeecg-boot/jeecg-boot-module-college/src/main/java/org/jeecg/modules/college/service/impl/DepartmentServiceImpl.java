package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.mapper.DepartmentMapper;
import org.jeecg.modules.college.mapper.MajorMapper;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.DepartmentService;
import org.jeecg.modules.college.vo.DepartVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
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
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService{

    @Resource
    private MajorMapper majorMapper;

    @Override
    public void saveDepartment(Department department) {
        this.baseMapper.insert(department);
        Major major = new Major();
        major.setMajorName(department.getDepartmentName());
        major.setBuildTime(new Date());
        major.setDelFlag("0");
        major.setMajorCode(department.getDepartmentCode()+"01");
        major.setStatus(1);
        majorMapper.insert(major);
    }

    @Override
    public long countList(Map<String, Object> queryParam) {
        return this.baseMapper.countList(queryParam);
    }

    @Override
    public List<DepartVo> queryList(Map<String, Object> queryParam) {
        return this.baseMapper.queryList(queryParam);
    }

    @Override
    public List<TreeModel> queryMyDeparts(Map<String, Object> queryParam) {
        List<TreeModel> treeModels = new ArrayList<>();
        List<DepartVo> vos = this.baseMapper.findListByCollegeId(queryParam);
        for (int i = 0; i < vos.size(); i++) {
            TreeModel treeModel = new TreeModel(vos.get(i));
            treeModels.add(treeModel);
        }
        return treeModels;
    }
}
