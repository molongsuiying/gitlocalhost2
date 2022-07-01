package org.jeecg.modules.quartz.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.jeecg.modules.quartz.entity.SysJobsMsgTemplate;
import org.jeecg.modules.quartz.entity.SysJobsMsgVersion;
import org.jeecg.modules.quartz.service.ISysJobsMsgTemplateService;
import org.jeecg.modules.quartz.service.ISysJobsMsgVersionService;
import org.jeecg.modules.quartz.service.ISysJobsService;
import org.jeecg.modules.quartz.util.ScheduleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/sys/task")
@Slf4j
@Api(tags = "动态定时任务接口")
public class TaskController {

    private static Map<String, SysJobs> jobs = Collections.synchronizedMap(new TreeMap<String, SysJobs>()); // 定时任务集合


    @Resource
    private ISysJobsService jobsService;

    @Resource
    private ISysJobsMsgTemplateService templateService;

    @Autowired
    private ISysJobsMsgVersionService versionService;

    private static TaskController taskController;


    @Resource
    private WebSocket webSocket;

    /**
     * @Title: init
     * @Description: 初始化启动定时任务
     */
    @PostConstruct
    void init() {
        taskController = this;
        taskController.jobsService = this.jobsService;
        taskController.webSocket = this.webSocket;
        taskController.templateService = this.templateService;
        List<SysJobs> jobList = jobsService.findBootingJobs();
        for (int i = 0; i < jobList.size(); i++) {
            jobs.put(jobList.get(i).getId(), jobList.get(i));
        }
        new Thread(() -> {
            for (Map.Entry<String, SysJobs> entry : jobs.entrySet()) {
                SysJobs job = entry.getValue();
                try {
                    if (job.getStatus() == 0) {
                        SysJobsMsgTemplate template = templateService.getById(job.getTempMsgId());
                        job.setTemplate(template);
                        ScheduleUtils.add(job,webSocket);
                    }
                } catch (Exception e) {
                    log.error("定时任务初始化异常", e);
                }
            }
        }).start();
    }

    /**
     * 分页列表查询
     *
     * @param sysJobs
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<?> queryPageList(SysJobs sysJobs, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        SysJobsMsgVersion version = versionService.getCurrentVersion();
        sysJobs.setJobVersion(version.getJobVersion());
        QueryWrapper<SysJobs> queryWrapper = QueryGenerator.initQueryWrapper(sysJobs, req.getParameterMap());
        String keyWord = req.getParameter("keyWord");

        if(oConvertUtils.isNotEmpty(keyWord)){
            queryWrapper.like("jobName",keyWord);

        }
        Page<SysJobs> page = new Page<>(pageNo, pageSize);
        IPage<SysJobs> pageList = jobsService.page(page, queryWrapper);
        return Result.ok(pageList);

    }

    /**
     * 添加或修改定时任务
     *
     * @param sysJobs
     * @return
     */
    @RequiresRoles("admin")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<?> add(@RequestBody SysJobs sysJobs) {

        String key = sysJobs.getJobName();
        if (sysJobs.getStatus() == null) {
            sysJobs.setStatus(0);
        }
        try {
            sysJobs.setMethodName("Test");
            SysJobsMsgVersion version = versionService.getCurrentVersion();
            sysJobs.setJobVersion(version.getJobVersion());
            jobsService.save(sysJobs);
            jobs.put(key,sysJobs);
            ScheduleUtils.cancel(sysJobs);
            if (sysJobs.getStatus() == 0) {
                // 开始执行定时任务
                SysJobsMsgTemplate template = templateService.getById(sysJobs.getTempMsgId());
                sysJobs.setTemplate(template);
                ScheduleUtils.add(sysJobs,webSocket);
            }
            return Result.ok("创建定时任务成功");
        }catch (Exception e) {
            log.error("操作失败：", e);
            return Result.error("操作失败，失败原因：" + e);
        }

    }

    /**
     * 添加或修改定时任务
     *
     * @param sysJobs
     * @return
     */
    @RequiresRoles("admin")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<?> edit(@RequestBody SysJobs sysJobs) {

        if (sysJobs.getStatus() == null) {
            sysJobs.setStatus(0);
        }
        try {
            sysJobs.setMethodName("Test");
            jobsService.updateById(sysJobs);
            jobs.put(sysJobs.getId(),sysJobs);
            ScheduleUtils.cancel(sysJobs);
            if (sysJobs.getStatus() == 0) {
                // 开始执行定时任务
                SysJobsMsgTemplate template = templateService.getById(sysJobs.getTempMsgId());
                sysJobs.setTemplate(template);
                ScheduleUtils.add(sysJobs,webSocket);
            }
            return Result.ok("创建定时任务成功");
        }catch (Exception e) {
            log.error("操作失败：", e);
            return Result.error("操作失败，失败原因：" + e);
        }

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
        SysJobs quartzJob = jobsService.getById(id);
        if (quartzJob == null) {
            return Result.error("未找到对应实体");
        }
        try {
            String key = quartzJob.getJobName();
            ScheduleUtils.cancel(quartzJob);
            if (key != null && jobs.containsKey(key)) {
                jobs.remove(quartzJob.getJobName());
            }
            jobsService.removeById(quartzJob.getId());
            return Result.ok("删除成功!");
        } catch (Exception e) {
            log.error("删除定时任务异常：", e);
            return Result.error("删除数据失败，失败原因：" + e);
        }

    }

    /**
     * 暂停定时任务
     *
     * @param jobId
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping(value = "/pause")
    @ApiOperation(value = "暂停定时任务")
    public Result<Object> pauseJob(@RequestParam(name = "jobId", required = true) String jobId) {
        SysJobs sysJobs = null;
        sysJobs = jobsService.getOne(new LambdaQueryWrapper<SysJobs>().eq(SysJobs::getId, jobId));
        if (sysJobs == null) {
            return Result.error("定时任务不存在！");
        }
        sysJobs.setStatus(CommonConstant.STATUS_DISABLE);
        jobsService.updateById(sysJobs);
        jobs.put(sysJobs.getId(),sysJobs);
        ScheduleUtils.cancel(sysJobs);
        return Result.ok("暂停定时任务成功");
    }

    /**
     * 恢复定时任务
     *
     * @param jobId
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping(value = "/resume")
    @ApiOperation(value = "恢复定时任务")
    public Result<Object> resumeJob(@RequestParam(name = "jobId", required = true) String jobId) {
        SysJobs sysJobs = jobsService.getOne(new LambdaQueryWrapper<SysJobs>().eq(SysJobs::getId, jobId));
        if (sysJobs == null) {
            return Result.error("定时任务不存在！");
        }
        try {
            sysJobs.setStatus(CommonConstant.STATUS_NORMAL);
            jobsService.updateById(sysJobs);
            jobs.put(sysJobs.getId(),sysJobs);
            ScheduleUtils.cancel(sysJobs);
            if (sysJobs.getStatus() == 0) {
                // 开始执行定时任务
                SysJobsMsgTemplate template = templateService.getById(sysJobs.getTempMsgId());
                sysJobs.setTemplate(template);
                ScheduleUtils.add(sysJobs,webSocket);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("定时任务 立即执行失败>>"+e.getMessage());
            return Result.error("执行失败!");
        }
        //scheduler.resumeJob(JobKey.jobKey(job.getJobClassName().trim()));
        return Result.ok("恢复定时任务成功");
    }

    /**
     * 立即执行
     * @param id
     * @return
     */
    @RequiresRoles("admin")
    @GetMapping("/execute")
    public Result<?> execute(@RequestParam(name = "id", required = true) String id) {
        SysJobs sysJobs = jobsService.getById(id);
        if (sysJobs == null) {
            return Result.error("未找到对应实体");
        }
        try {
            sysJobs.setStatus(CommonConstant.STATUS_NORMAL);
            jobsService.updateById(sysJobs);
            jobs.put(sysJobs.getId(),sysJobs);
            ScheduleUtils.cancel(sysJobs);
            if (sysJobs.getStatus() == 0) {
                // 开始执行定时任务
                SysJobsMsgTemplate template = templateService.getById(sysJobs.getTempMsgId());
                sysJobs.setTemplate(template);
                ScheduleUtils.add(sysJobs,webSocket);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("定时任务 立即执行失败>>"+e.getMessage());
            return Result.error("执行失败!");
        }
        return Result.ok("执行成功!");
    }

    /**
     *   切换版本
     * @param version
     * @return
     */
    @RequestMapping(value = "/changeVersion", method = RequestMethod.POST)
    public Result<SysJobsMsgVersion> changeVersion(@RequestBody SysJobsMsgVersion version) {
        Result<SysJobsMsgVersion> result = new Result<>();
        try {
            SysJobsMsgVersion currentVersion = versionService.getCurrentVersion();

            List<SysJobs> jobList = jobsService.findBootingJobs();

            for (int i = 0; i < jobList.size(); i++) {
                SysJobs j = jobList.get(i);
                String key = j.getJobName();
                ScheduleUtils.cancel(j);
                if (key != null && jobs.containsKey(key)) {
                    jobs.remove(j.getJobName());
                }
            }
            jobsService.ceaseJobsByVersion(currentVersion.getJobVersion());

            version.setStatus(CommonConstant.STATUS_NORMAL);
            versionService.updateById(version);
            if(version.getStatus().equals(CommonConstant.STATUS_NORMAL)){
                versionService.updateOtherVersion(version.getId());
            }
            result.success("切换版本成功！");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            result.error500("切换版本失败");
        }
        return result;
    }

}
