package org.jeecg.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.system.entity.SysAnnouncement;

import java.util.List;

/**
 * @Description: 定时通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface SysJobsMsgMapper extends BaseMapper<SysJobsMsg> {

	
	List<SysJobsMsg> querySysCementListByUserId(Page<SysJobsMsg> page, @Param("userId") String userId, @Param("msgCategory") String msgCategory);

}
