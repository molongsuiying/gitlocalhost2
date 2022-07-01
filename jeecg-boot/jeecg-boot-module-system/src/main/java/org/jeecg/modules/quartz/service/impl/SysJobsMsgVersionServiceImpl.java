package org.jeecg.modules.quartz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.quartz.entity.SysJobsMsgVersion;
import org.jeecg.modules.quartz.mapper.SysJobsMsgVersionMapper;
import org.jeecg.modules.quartz.service.ISysJobsMsgVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: jeecg-boot
 * @Date: 2019-04-28
 * @Version: V1.1
 */
@Slf4j
@Service
public class SysJobsMsgVersionServiceImpl extends ServiceImpl<SysJobsMsgVersionMapper,SysJobsMsgVersion> implements ISysJobsMsgVersionService {

	@Autowired
	private SysJobsMsgVersionMapper versionMapper;


	@Override
	public SysJobsMsgVersion getCurrentVersion() {
		return versionMapper.getCurrentVersion();
	}

	@Override
	public void updateOtherVersion(String id) {
		versionMapper.updateOtherVersion(id);
	}

	@Override
	public int findByJobVersion(String jobVersion) {
		return versionMapper.findByJobVersion(jobVersion);
	}

	@Override
	public int countVersionById(String jobVersion, String id) {
		return versionMapper.countVersionById(jobVersion, id);
	}
}
