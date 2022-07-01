package org.jeecg.modules.quartz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.quartz.entity.JobsMsgSendModel;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.quartz.mapper.SysJobsMsgSendMapper;
import org.jeecg.modules.quartz.service.ISysJobsMsgSendService;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.mapper.SysAnnouncementSendMapper;
import org.jeecg.modules.system.model.AnnouncementSendModel;
import org.jeecg.modules.system.service.ISysAnnouncementSendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
@Service
public class SysJobsMsgSendServiceImpl extends ServiceImpl<SysJobsMsgSendMapper, SysJobsMsgSend> implements ISysJobsMsgSendService {

	@Resource
	private SysJobsMsgSendMapper sysJobsMsgSendMapper;
	
	@Override
	public List<String> queryByUserId(String userId) {
		return sysJobsMsgSendMapper.queryByUserId(userId);
	}

	@Override
	public Page<JobsMsgSendModel> getMyMsgSendPage(Page<JobsMsgSendModel> page,
												   JobsMsgSendModel jobsMsgSendModel) {
		 return page.setRecords(sysJobsMsgSendMapper.getMyMsgSendList(page, jobsMsgSendModel));
	}

	@Override
	public List<SysJobsMsgSend> findReadListByPage(Map<String, Object> params) {
		return this.sysJobsMsgSendMapper.findReadListByPage(params);
	}

	@Override
	public int countReadListByPage(Map<String, Object> params) {
		return this.sysJobsMsgSendMapper.countReadListByPage(params);
	}

}
