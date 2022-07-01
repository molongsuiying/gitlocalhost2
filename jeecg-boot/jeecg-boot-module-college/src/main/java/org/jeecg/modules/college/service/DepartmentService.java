package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.vo.DepartVo;

import java.util.List;
import java.util.Map;

public interface DepartmentService extends IService<Department>{

    void saveDepartment(Department department);

    long countList(Map<String,Object> queryParam);

    List<DepartVo> queryList(Map<String,Object> queryParam);

    List<TreeModel> queryMyDeparts(Map<String,Object> queryParam);
}
