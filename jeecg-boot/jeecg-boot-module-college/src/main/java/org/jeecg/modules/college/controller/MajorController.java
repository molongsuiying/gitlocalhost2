package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.entity.College;
import org.jeecg.modules.college.entity.Department;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.DepartmentService;
import org.jeecg.modules.college.service.MajorService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.MajorVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
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
@RequestMapping("/major")
@Slf4j
public class MajorController {

    @Autowired
    private MajorService majorService;

    /**
     * 获取列表数据
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<MajorVo>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<MajorVo>> result = new Result<>();
        IPage<MajorVo> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
        List<MajorVo> pageList = majorService.queryList(query);
        long count = majorService.countList(query);
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
    public Result<Major> add(@RequestBody Major major) {
        Result<Major> result = new Result<>();

        LambdaQueryWrapper<Major> query = new LambdaQueryWrapper<>();

        Major m = majorService.getOne(query.eq(Major::getMajorCode,major.getMajorCode()).eq(Major::getDepartmentId,major.getDepartmentId()));

        if(m != null){
            return result.error500("该院系编号已存在!");
        }
        try {
            majorService.save(major);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param major
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Major> edit(@RequestBody Major major) {
        Result<Major> result = new Result<>();
        Major old = majorService.getById(major.getId());
        if(old==null) {
            result.error500("未找到对应实体");
        }else {
            majorService.updateById(major);
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
    public Result<Major> delete(@RequestParam(name="id",required=true) String id) {
        Result<Major> result = new Result<>();
        boolean ok = majorService.removeById(id);
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
    public Result<Major> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<Major> result = new Result<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.error500("参数不识别！");
        }else {
            majorService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 获取院系列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryMyMajorList", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryMyDeparts(HttpServletRequest req) {
        Result<List<TreeModel>> result = new Result<>();
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        List<TreeModel> list = majorService.queryMyMajors(query);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     * 查询数据 查出所有院系+专业,并以树结构数据格式响应给前端
     *
     * @return
     */
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryTreeList() {
        Result<List<TreeModel>> result = new Result<>();
        try {

            List<TreeModel> list = majorService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }
}
