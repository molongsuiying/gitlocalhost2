package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
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
public interface ISysJobsMsgSendService extends IService<SysJobsMsgSend> {

	public List<String> queryByUserId(String userId);
	
	/**
	 * @功能：获取我的消息
	 * @param jobsMsgSendModel
	 * @return
	 */
	public Page<JobsMsgSendModel> getMyMsgSendPage(Page<JobsMsgSendModel> page, JobsMsgSendModel jobsMsgSendModel);

	List<SysJobsMsgSend> findReadListByPage(Map<String,Object> params);

	int countReadListByPage(Map<String,Object> params);

}
