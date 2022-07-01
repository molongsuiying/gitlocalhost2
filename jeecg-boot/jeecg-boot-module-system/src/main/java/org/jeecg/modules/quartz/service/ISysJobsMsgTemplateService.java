package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgTemplate;

/**
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysJobsMsgTemplateService extends IService<SysJobsMsgTemplate> {

	public void saveTemplate(SysJobsMsgTemplate template);

	public boolean upDatTemplate(SysJobsMsgTemplate template);

	public void saveTemplate(String title, String msgContent);


}
