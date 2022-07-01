package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.college.entity.Bunch;
import org.jeecg.modules.college.model.TreeModel;
import org.jeecg.modules.college.service.BunchService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/bunch")
@Slf4j
public class BunchController {

    @Autowired
    private BunchService bunchService;

    /**
     * 获取院系列表
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryMyBunchList", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryMyDeparts(HttpServletRequest req) {
        Result<List<TreeModel>> result = new Result<>();
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        List<TreeModel> list = bunchService.queryMyBunches(query);
        result.setSuccess(true);
        result.setResult(list);
        return result;
    }

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Bunch> add(@RequestBody Bunch bunch) {
        Result<Bunch> result = new Result<>();


        try {
            bunchService.save(bunch);
            result.success("添加成功！");
            result.setResult(bunch);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param bunch
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Bunch> edit(@RequestBody Bunch bunch) {
        Result<Bunch> result = new Result<>();
        Bunch old = bunchService.getById(bunch.getId());
        if(old==null) {
            result.error500("未找到对应实体");
        }else {
            bunchService.updateById(bunch);
            result.success("修改成功!");
        }
        return result;
    }
}
