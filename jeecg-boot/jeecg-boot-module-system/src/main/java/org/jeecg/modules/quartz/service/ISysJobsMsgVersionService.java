package org.jeecg.modules.quartz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.quartz.entity.SysJobsMsgVersion;

/**
 * @Description:
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
public interface ISysJobsMsgVersionService extends IService<SysJobsMsgVersion> {

	SysJobsMsgVersion getCurrentVersion();

	void updateOtherVersion(String id);

	int findByJobVersion(String jobVersion);

	int countVersionById(String jobVersion,String id);
}
