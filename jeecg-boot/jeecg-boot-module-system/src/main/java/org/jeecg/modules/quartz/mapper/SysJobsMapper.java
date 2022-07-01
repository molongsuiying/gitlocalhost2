package org.jeecg.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.entity.SysJobs;

import java.util.List;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysJobsMapper extends BaseMapper<SysJobs> {

	public List<SysJobs> findByJobClassName(@Param("jobClassName") String jobClassName);

	public List<SysJobs> findBootingJobs();

	void ceaseJobsByVersion(@Param("jobVersion")String jobVersion);
}
