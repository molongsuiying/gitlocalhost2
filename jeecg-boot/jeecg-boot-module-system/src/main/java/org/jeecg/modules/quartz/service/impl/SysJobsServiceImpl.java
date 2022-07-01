package org.jeecg.modules.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.jeecg.modules.quartz.mapper.QuartzJobMapper;
import org.jeecg.modules.quartz.mapper.SysJobsMapper;
import org.jeecg.modules.quartz.service.IQuartzJobService;
import org.jeecg.modules.quartz.service.ISysJobsService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date: 2019-04-28
 * @Version: V1.1
 */
@Slf4j
@Service
public class SysJobsServiceImpl extends ServiceImpl<SysJobsMapper,SysJobs> implements ISysJobsService {

	@Autowired
	private SysJobsMapper sysJobsMapper;


	@Override
	public List<SysJobs> findByJobClassName(String jobClassName) {
		return sysJobsMapper.findByJobClassName(jobClassName);
	}


	@Override
	public List<SysJobs> findBootingJobs() {
		return this.baseMapper.findBootingJobs();
	}

	@Override
	public void ceaseJobsByVersion(String jobVersion) {
		this.baseMapper.ceaseJobsByVersion(jobVersion);
	}


}
