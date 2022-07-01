package org.jeecg.modules.quartz.job;

import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.system.controller.TimerController;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SampleJob implements Job {
	@Resource
	private TimerController timerController;
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
			timerController.timer();
			log.info(String.format(" Jeecg-Boot 普通定时任务 SampleJob !  时间:" + DateUtils.getTimestamp()));
	}
}
