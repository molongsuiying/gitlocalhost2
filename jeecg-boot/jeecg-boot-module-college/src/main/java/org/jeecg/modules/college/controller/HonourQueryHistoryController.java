package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.entity.HonourAppendixHistory;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.service.HonourAppendixHistoryService;
import org.jeecg.modules.college.service.HonourAuthItemService;
import org.jeecg.modules.college.service.HonourAuthService;
import org.jeecg.modules.college.service.HonourQueryHistoryService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.AuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping("/honour/query")
@Slf4j
public class HonourQueryHistoryController {

    @Resource
    private HonourQueryHistoryService historyService;

    @Resource
    private HonourAppendixHistoryService appendixHistoryService;


    /**
     * @功能：查询日志记录
     * @param queryHistory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<HonourQueryHistory>> queryPageList(HonourQueryHistory queryHistory,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
        Result<IPage<HonourQueryHistory>> result = new Result<>();
        QueryWrapper<HonourQueryHistory> queryWrapper = QueryGenerator.initQueryWrapper(queryHistory, req.getParameterMap());
        Page<HonourQueryHistory> page = new Page<>(pageNo, pageSize);
        //日志关键词
        String keyWord = req.getParameter("keyWord");
        if(oConvertUtils.isNotEmpty(keyWord)) {
            queryWrapper.like("query_param",keyWord);
        }

        IPage<HonourQueryHistory> pageList = historyService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    /**
     * @功能：查询日志记录
     * @param appendixHistory
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/downList", method = RequestMethod.GET)
    public Result<IPage<HonourAppendixHistory>> downList(HonourAppendixHistory appendixHistory, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourAppendixHistory>> result = new Result<>();
        QueryWrapper<HonourAppendixHistory> queryWrapper = QueryGenerator.initQueryWrapper(appendixHistory, req.getParameterMap());
        Page<HonourAppendixHistory> page = new Page<>(pageNo, pageSize);
        String keyWord = req.getParameter("keyWord");
        if(oConvertUtils.isNotEmpty(keyWord)) {
            queryWrapper.like("honour_title",keyWord);
        }
        IPage<HonourAppendixHistory> pageList = appendixHistoryService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * @功能：删除单个日志记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<HonourQueryHistory> delete(@RequestParam(name="id",required=true) String id) {
        Result<HonourQueryHistory> result = new Result<>();
        HonourQueryHistory sysLog = historyService.getById(id);
        if(sysLog==null) {
            result.error500("未找到对应实体");
        }else {
            boolean ok = historyService.removeById(id);
            if(ok) {
                result.success("删除成功!");
            }
        }
        return result;
    }

    /**
     * @功能：批量，全部清空日志记录
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<HonourQueryHistory> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<HonourQueryHistory> result = new Result<>();
        if(ids==null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        }else {
            if("allclear".equals(ids)) {
                historyService.removeAll();
                result.success("清除成功!");
            }
            this.historyService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }


}
