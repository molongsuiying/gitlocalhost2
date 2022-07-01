package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.vo.CommonVo;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public interface CollegeService extends IService<College>{

    List<CommonVo> findMyCollegeNames();

    void saveUserCollege(College college);

    void deleteUserCollege(College college);

    List<CommonVo> findNamesByRegister();

    List<TreeModel> queryMyColleges(Map<String,Object> query);

    void removeCollege(String id);
}
