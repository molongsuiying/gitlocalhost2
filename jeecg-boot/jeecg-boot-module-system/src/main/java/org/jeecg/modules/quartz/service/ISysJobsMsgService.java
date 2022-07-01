package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.message.entity.SysMessageTemplate;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgTemplate;
import org.jeecg.modules.system.entity.SysAnnouncement;

/**
 * @Description: 系统通告表
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysJobsMsgService extends IService<SysJobsMsg> {

	public void saveMsg(SysJobsMsg sysJobsMsg);

	public boolean upDateMsg(SysJobsMsg sysJobsMsg);

	public SysJobsMsg saveMsgByTemp(SysJobsMsgTemplate template);

	public void saveSysMsg(String title, String msgContent);

	public Page<SysJobsMsg> querySysCementPageByUserId(Page<SysJobsMsg> page, String userId, String msgCategory);


}
