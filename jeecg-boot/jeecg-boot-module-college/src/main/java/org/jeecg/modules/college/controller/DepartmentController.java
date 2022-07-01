package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.DepartmentService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.DepartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
@RequestMapping("/department")
@Slf4j
public class DepartmentController {


    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取列表数据
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<DepartVo>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<DepartVo>> result = new Result<>();
        IPage<DepartVo> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
        List<DepartVo> pageList = departmentService.queryList(query);
        long count = departmentService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Department> add(@RequestBody Department department) {
        Result<Department> result = new Result<>();

        LambdaQueryWrapper<Department> query = new LambdaQueryWrapper<>();

        Department d = departmentService.getOne(query.eq(Department::getDepartmentCode,department.getDepartmentCode()));

        if(d != null){
            return result.error500("该院系编号已存在!");
        }
        try {
            departmentService.saveDepartment(department);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param department
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Department> edit(@RequestBody Department department) {
        Result<Department> result = new Result<>();
        Department old = departmentService.getById(department.getId());
        if(old==null) {
            result.error500("未找到对应实体");
        }else {
            departmentService.updateById(department);
            result.success("修改成功!");
        }
        return result;
    }

    /**
     * @功能：删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<Department> delete(@RequestParam(name="id",required=true) String id) {
        Result<Department> result = new Result<>();
        boolean ok = departmentService.removeById(id);
        if(ok) {
            result.success("删除成功!");
        }else{
            result.error500("删除失败!");
        }
        return result;
    }

    /**
     * @功能：批量删除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<Department> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<Department> result = new Result<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        }else {
            departmentService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 获取院系列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryMyDeparts", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryMyDeparts(HttpServletRequest req) {
        Result<List<TreeModel>> result = new Result<>();
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        List<TreeModel> list = departmentService.queryMyDeparts(query);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 获取院系列表-学院ID
     * @param req
     * @return
     */
    @RequestMapping(value = "/getDepartmentsByCollegeId", method = RequestMethod.GET)
    public Result<List<DepartVo>> getDepartmentsByCollegeId(HttpServletRequest req) {
        Result<List<DepartVo>> result = new Result<>();
        Map<String,Object> query = CollegeUtil.getParameterMap(req);

        List<DepartVo> pageList = departmentService.queryList(query);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
