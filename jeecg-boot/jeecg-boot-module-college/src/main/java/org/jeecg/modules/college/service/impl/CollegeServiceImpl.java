package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.entity.UserCollege;
import org.jeecg.modules.college.mapper.CollegeMapper;
import org.jeecg.modules.college.mapper.DepartmentMapper;
import org.jeecg.modules.college.mapper.UserCollegeMapper;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.CollegeService;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.DepartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService{


    @Resource
    private UserCollegeMapper userCollegeMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Override
    public List<CommonVo> findMyCollegeNames() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Map<String,Object> query = new HashMap<>();
        query.put("userId",sysUser.getId());
        return this.baseMapper.findMyCollegeNames(query);
    }

    @Override
    public void saveUserCollege(College college) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        UserCollege userCollege = new UserCollege();
        userCollege.setUserId(sysUser.getId());
        userCollege.setCollegeId(college.getId());
        userCollegeMapper.insert(userCollege);
    }

    @Override
    public void deleteUserCollege(College college) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        UserCollege userCollege = new UserCollege();
        userCollege.setUserId(sysUser.getId());
        userCollege.setCollegeId(college.getId());
        userCollegeMapper.deleteByUIdAndCid(userCollege);
    }

    @Override
    public List<CommonVo> findNamesByRegister() {
        return this.baseMapper.findNamesByRegister();
    }

    @Override
    public List<TreeModel> queryMyColleges(Map<String, Object> query) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        query.put("userId",sysUser.getId());
        List<TreeModel> treeModels = new ArrayList<>();
        List<College> collegeList = this.baseMapper.findMyColleges(query);

        String str[] = {};
        if (query.containsKey("ids")){
            str = query.get("ids").toString().split("-");
        }

        for (int i = 0; i < collegeList.size(); i++) {
            TreeModel treeModel = new TreeModel(collegeList.get(i));

            if(str.length > 0 && treeModel.getKey().equals(str[0])){
                List<TreeModel> children = new ArrayList<>();
                Map<String,Object> params = new HashMap<>();
                params.put("collegeId",treeModel.getKey());
                List<DepartVo> vos = this.departmentMapper.findListByCollegeId(params);
                for (int j = 0; j < vos.size(); j++) {
                    TreeModel child = new TreeModel(vos.get(j));
                    children.add(child);
                }
                treeModel.setChildren(children);
            }

            treeModels.add(treeModel);
        }
        return treeModels;
    }

    @Override
    @Transactional
    public void removeCollege(String id) {
        College college = this.baseMapper.selectById(id);
        sysBaseAPI.deleteDepartByCollegeName(college.getCollegeName());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        UserCollege userCollege = new UserCollege();
        userCollege.setUserId(sysUser.getId());
        userCollege.setCollegeId(college.getId());
        userCollegeMapper.deleteByUIdAndCid(userCollege);
        this.baseMapper.deleteById(college.getId());
    }
}
