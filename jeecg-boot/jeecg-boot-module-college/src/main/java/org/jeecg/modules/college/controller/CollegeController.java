package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.entity.UserCollege;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.CollegeService;
import org.jeecg.modules.college.service.DepartmentService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.DepartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
@RestController
@RequestMapping("/college")
@Slf4j
@Transactional
@Api(tags="学院")
public class CollegeController {


    @Autowired
    private CollegeService collegeService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private DepartmentService departmentService;


    /**
     * 获取列表数据
     * @param college
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<College>> queryPageList(College college, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<College>> result = new Result<>();
        QueryWrapper<College> queryWrapper = QueryGenerator.initQueryWrapper(college, req.getParameterMap());
        String keyWord = req.getParameter("keyWord");
        if(StringUtils.isNotEmpty(keyWord)) {
            queryWrapper.like("college_name",keyWord).or().like("college_code",keyWord);
        }

        Page<College> page = new Page<>(pageNo, pageSize);
        IPage<College> pageList = collegeService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
    public Result<College> add(@RequestBody College college) {
        Result<College> result = new Result<>();

        LambdaQueryWrapper<College> query = new LambdaQueryWrapper<>();

        College c = collegeService.getOne(query.eq(College::getCollegeCode,college.getCollegeCode()));

        if(c != null){
            return result.error500("该学院代码已存在!");
        }
        try {
            collegeService.save(college);
            collegeService.saveUserCollege(college);
            sysBaseAPI.saveDepartByCollege(college.getCollegeName());
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param college
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<College> edit(@RequestBody College college) {
        Result<College> result = new Result<>();
        College old = collegeService.getById(college.getId());
        if(old==null) {
            result.error500("未找到对应实体");
        }else {
            collegeService.updateById(college);
            result.success("修改成功!");
        }
        return result;
    }


    /**
     * 获取学院列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryCollegeNames", method = RequestMethod.GET)
    public Result<List<CommonVo>> queryPageList(HttpServletRequest req) {
        Result<List<CommonVo>> result = new Result<>();
        List<CommonVo> list = collegeService.findMyCollegeNames();
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 获取学院列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryMyColleges", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryMyColleges(HttpServletRequest req) {
        Result<List<TreeModel>> result = new Result<>();
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        List<TreeModel> list = collegeService.queryMyColleges(query);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * @功能：删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value= {CacheConstant.SYS_DEPARTS_CACHE,CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries=true)
    public Result<College> delete(@RequestParam(name="id",required=true) String id) {
        Result<College> result = new Result<>();
        Map<String,Object> query = new HashMap<>();
        query.put("collegeId",id);
        List<DepartVo> pageList = departmentService.queryList(query);
        if(pageList.size()>0){
            result.error500("删除失败!存在院系");
        }else{
            collegeService.removeCollege(id);
            result.success("删除成功!");
        }

        return result;
    }
}
