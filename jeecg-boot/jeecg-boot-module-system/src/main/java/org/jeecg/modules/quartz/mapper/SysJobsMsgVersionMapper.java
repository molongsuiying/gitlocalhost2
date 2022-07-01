package org.jeecg.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.jeecg.modules.quartz.entity.SysJobsMsgVersion;

import java.util.List;

/**
 * @Description: 定时任务版本管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysJobsMsgVersionMapper extends BaseMapper<SysJobsMsgVersion> {

	SysJobsMsgVersion getCurrentVersion();

	void updateOtherVersion(String id);

	int findByJobVersion(String jobVersion);

	int countVersionById(@Param("jobVersion") String jobVersion,@Param("id")String id);
}
