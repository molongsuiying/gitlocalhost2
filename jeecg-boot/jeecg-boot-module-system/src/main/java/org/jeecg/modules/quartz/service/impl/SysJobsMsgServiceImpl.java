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
import org.jeecg.modules.quartz.service.ISysJobsMsgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Description: 系统定时通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Service
public class SysJobsMsgServiceImpl extends ServiceImpl<SysJobsMsgMapper, SysJobsMsg> implements ISysJobsMsgService {

	@Resource
	private SysJobsMsgMapper sysJobsMsgMapper;
	
	@Resource
	private SysJobsMsgSendMapper sysJobsMsgSendMapper;
	
	@Transactional
	@Override
	public void saveMsg(SysJobsMsg sysJobsMsg) {
		if(sysJobsMsg.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
			sysJobsMsgMapper.insert(sysJobsMsg);
		}else {
			// 1.插入通告表记录
			sysJobsMsgMapper.insert(sysJobsMsg);
			// 2.插入用户通告阅读标记表记录
			String userId = sysJobsMsg.getUserIds();
			String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
			String anntId = sysJobsMsg.getId();
			Date refDate = new Date();
			for(int i=0;i<userIds.length;i++) {
				SysJobsMsgSend sysJobsMsgSend = new SysJobsMsgSend();
				sysJobsMsgSend.setAnntId(anntId);
				sysJobsMsgSend.setUserId(userIds[i]);
				sysJobsMsgSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysJobsMsgSend.setReadTime(refDate);
				sysJobsMsgSendMapper.insert(sysJobsMsgSend);
			}
		}
	}
	
	/**
	 * @功能：编辑消息信息
	 */
	@Transactional
	@Override
	public boolean upDateMsg(SysJobsMsg sysJobsMsg) {
		// 1.更新系统信息表数据
		sysJobsMsgMapper.updateById(sysJobsMsg);
		String userId = sysJobsMsg.getUserIds();
		if(oConvertUtils.isNotEmpty(userId)&&sysJobsMsg.getMsgType().equals(CommonConstant.MSG_TYPE_UESR)) {
			// 2.补充新的通知用户数据
			String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
			String anntId = sysJobsMsg.getId();
			Date refDate = new Date();
			for(int i=0;i<userIds.length;i++) {
				LambdaQueryWrapper<SysJobsMsgSend> queryWrapper = new LambdaQueryWrapper<SysJobsMsgSend>();
				queryWrapper.eq(SysJobsMsgSend::getAnntId, anntId);
				queryWrapper.eq(SysJobsMsgSend::getUserId, userIds[i]);
				List<SysJobsMsgSend> announcementSends=sysJobsMsgSendMapper.selectList(queryWrapper);
				if(announcementSends.size()<=0) {
					SysJobsMsgSend sysJobsMsgSend = new SysJobsMsgSend();
					sysJobsMsgSend.setAnntId(anntId);
					sysJobsMsgSend.setUserId(userIds[i]);
					sysJobsMsgSend.setReadFlag(CommonConstant.NO_READ_FLAG);
					sysJobsMsgSend.setReadTime(refDate);
					sysJobsMsgSendMapper.insert(sysJobsMsgSend);
				}
			}
			// 3. 删除多余通知用户数据
			Collection<String> delUserIds = Arrays.asList(userIds);
			LambdaQueryWrapper<SysJobsMsgSend> queryWrapper = new LambdaQueryWrapper<SysJobsMsgSend>();
			queryWrapper.notIn(SysJobsMsgSend::getUserId, delUserIds);
			queryWrapper.eq(SysJobsMsgSend::getAnntId, anntId);
			sysJobsMsgSendMapper.delete(queryWrapper);
		}
		return true;
	}

    @Override
    public SysJobsMsg saveMsgByTemp(SysJobsMsgTemplate template) {
        SysJobsMsg sysJobsMsg = new SysJobsMsg();
        sysJobsMsg.setBusId(template.getBusId());
        sysJobsMsg.setBusType(template.getBusType());
        sysJobsMsg.setMsgAbstract(template.getMsgAbstract());
        sysJobsMsg.setMsgContent(template.getMsgContent());
        sysJobsMsg.setMsgType(template.getMsgType());
        sysJobsMsg.setMsgCategory(template.getMsgCategory());
        sysJobsMsg.setPriority(template.getPriority());
        sysJobsMsg.setSender(template.getSender());
        sysJobsMsg.setSendStatus(template.getSendStatus());
        sysJobsMsg.setSendTime(new Date());
        sysJobsMsg.setTitile(template.getTitile());
        sysJobsMsg.setTemplateId(template.getId());
        sysJobsMsg.setOpenPage(template.getOpenPage());
        sysJobsMsg.setOpenType(template.getOpenType());
        sysJobsMsg.setUserIds(template.getUserIds());
        sysJobsMsg.setStartTime(template.getStartTime());
        sysJobsMsg.setEndTime(template.getEndTime());
        sysJobsMsgMapper.insert(sysJobsMsg);



        return sysJobsMsg;
    }

    // @功能：流程执行完成保存消息通知
	@Override
	public void saveSysMsg(String title, String msgContent) {
		SysJobsMsg sysJobsMsg = new SysJobsMsg();
		sysJobsMsg.setTitile(title);
		sysJobsMsg.setMsgContent(msgContent);
		sysJobsMsg.setSender("JEECG BOOT");
		sysJobsMsg.setPriority(CommonConstant.PRIORITY_L);
		sysJobsMsg.setMsgType(CommonConstant.MSG_TYPE_ALL);
		sysJobsMsg.setSendStatus(CommonConstant.HAS_SEND);
		sysJobsMsg.setSendTime(new Date());
		sysJobsMsg.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		sysJobsMsgMapper.insert(sysJobsMsg);
	}

	@Override
	public Page<SysJobsMsg> querySysCementPageByUserId(Page<SysJobsMsg> page, String userId,String msgCategory) {
		 return page.setRecords(sysJobsMsgMapper.querySysCementListByUserId(page, userId, msgCategory));
	}

}
