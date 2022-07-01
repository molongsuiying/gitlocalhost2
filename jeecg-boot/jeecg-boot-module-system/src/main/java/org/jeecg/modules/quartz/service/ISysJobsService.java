package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date: 2019-04-28
 * @Version: V1.1
 */
public interface ISysJobsService extends IService<SysJobs> {

	List<SysJobs> findByJobClassName(String jobClassName);

	List<SysJobs> findBootingJobs();

	void ceaseJobsByVersion(String jobVersion);

}
