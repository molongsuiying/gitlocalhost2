package org.jeecg.modules.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.JobsMsgSendModel;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.model.AnnouncementSendModel;

import java.util.List;
import java.util.Map;

/**
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
public interface SysJobsMsgSendMapper extends BaseMapper<SysJobsMsgSend> {

	public List<String> queryByUserId(@Param("userId") String userId);

	/**
	 * @功能：获取我的消息
	 * @param jobsMsgSendModel
	 * @return
	 */
	public List<JobsMsgSendModel> getMyMsgSendList(Page<JobsMsgSendModel> page, @Param("jobsMsgSendModel") JobsMsgSendModel jobsMsgSendModel);

	List<SysJobsMsgSend> findReadListByPage(Map<String,Object> params);

	int countReadListByPage(Map<String,Object> params);
}
