package org.jeecg.modules.quartz.controller;

import com.aliyun.oss.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.jeecg.modules.quartz.entity.SysJobsMsgVersion;
import org.jeecg.modules.quartz.service.ISysJobsMsgVersionService;
import org.jeecg.modules.quartz.service.ISysJobsService;
import org.jeecg.modules.quartz.util.ScheduleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/sys/jobsMsgVersion")
@Slf4j
public class SysJobsMsgVersionController {

    @Autowired
    private ISysJobsMsgVersionService versionService;

    @Autowired
    private ISysJobsService jobsService;


    /**
     * 分页列表查询
     * @param sysJobsMsgVersion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysJobsMsgVersion>> queryPageList(SysJobsMsgVersion sysJobsMsgVersion,
                                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                          @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                          HttpServletRequest req) {
        Result<IPage<SysJobsMsgVersion>> result = new Result<>();
        QueryWrapper<SysJobsMsgVersion> queryWrapper = new QueryWrapper<>(sysJobsMsgVersion);
        Page<SysJobsMsgVersion> page = new Page<>(pageNo,pageSize);
        //排序逻辑 处理
        String column = req.getParameter("column");
        String order = req.getParameter("order");
        String code = req.getParameter("keyWord");

        if(!StringUtils.isNullOrEmpty(code)){
            queryWrapper.like("version_code",code);

        }
        if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
            if("asc".equals(order)) {
                queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
            }else {
                queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
            }
        }
        IPage<SysJobsMsgVersion> pageList = versionService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     *  当前版本
     * @return
     */
    @RequestMapping(value = "/currentVersion", method = RequestMethod.GET)
    public Result<SysJobsMsgVersion> currentVersion() {
        Result<SysJobsMsgVersion> result = new Result<>();
        try {
            SysJobsMsgVersion version = versionService.getCurrentVersion();
            result.setResult(version);
            result.success("获取成功!");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *   添加
     * @param version
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysJobsMsgVersion> add(@RequestBody SysJobsMsgVersion version) {
        Result<SysJobsMsgVersion> result = new Result<>();
        try {
            int count = versionService.findByJobVersion(version.getJobVersion());
            if(count>0){
                return result.error500("该版本已存在,请重新输入!");
            }
            version.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
            version.setStatus(CommonConstant.STATUS_DISABLE);
            versionService.save(version);
            if(version.getStatus().equals(CommonConstant.STATUS_NORMAL)){
                versionService.updateOtherVersion(version.getId());
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *   编辑
     * @param version
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysJobsMsgVersion> edit(@RequestBody SysJobsMsgVersion version) {
        Result<SysJobsMsgVersion> result = new Result<>();
        try {
            int count = versionService.countVersionById(version.getJobVersion(),version.getId());
            if (count>0){
                return result.error500("该版本已存在,请重新输入!");
            }
            versionService.updateById(version);
            if(version.getStatus().equals(CommonConstant.STATUS_NORMAL)){
                versionService.updateOtherVersion(version.getId());
            }
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("操作失败");
        }
        return result;
    }




    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @RequiresRoles("admin")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        SysJobsMsgVersion version = versionService.getById(id);
        if (version == null) {
            return Result.error("未找到对应实体");
        }
        try {
            if(version.getStatus().equals(CommonConstant.STATUS_DISABLE)){
                versionService.removeById(version.getId());
            }else{
                return Result.error("无法删除当前版本");
            }
            return Result.ok("删除成功!");
        } catch (Exception e) {
            log.error("删除定时任务异常：", e);
            return Result.error("删除数据失败，失败原因：" + e);
        }

    }
}
