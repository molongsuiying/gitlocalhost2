package org.jeecg.modules.quartz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.quartz.entity.SysJobsMsgTemplate;
import org.jeecg.modules.quartz.mapper.SysJobsMsgMapper;
import org.jeecg.modules.quartz.mapper.SysJobsMsgSendMapper;
import org.jeecg.modules.quartz.mapper.SysJobsMsgTemplateMapper;
import org.jeecg.modules.quartz.service.ISysJobsMsgService;
import org.jeecg.modules.quartz.service.ISysJobsMsgTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Description: 系统定时通告模板表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Service
public class SysJobsMsgTemplateServiceImpl extends ServiceImpl<SysJobsMsgTemplateMapper, SysJobsMsgTemplate> implements ISysJobsMsgTemplateService {

	@Resource
	private SysJobsMsgTemplateMapper templateMapper;
	

	@Override
	public void saveTemplate(SysJobsMsgTemplate template) {
		templateMapper.insert(template);
	}

	@Override
	public boolean upDatTemplate(SysJobsMsgTemplate template) {
		// 1.更新系统信息表数据
		templateMapper.updateById(template);
		return true;
	}

	@Override
	public void saveTemplate(String title, String msgContent) {
		SysJobsMsgTemplate template= new SysJobsMsgTemplate();
		template.setTitile(title);
		template.setMsgContent(msgContent);
		template.setSender(" Admin ");
		template.setPriority(CommonConstant.PRIORITY_L);
		template.setMsgType(CommonConstant.MSG_TYPE_ALL);
		template.setSendStatus(CommonConstant.HAS_SEND);
		template.setSendTime(new Date());
		template.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		templateMapper.insert(template);
	}
}
