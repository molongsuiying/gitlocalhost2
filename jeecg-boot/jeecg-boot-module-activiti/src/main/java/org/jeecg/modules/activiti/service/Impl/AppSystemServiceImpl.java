package org.jeecg.modules.activiti.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.activiti.entity.AppPermission;
import org.jeecg.modules.activiti.entity.AppSystem;
import org.jeecg.modules.activiti.mapper.AppSystemMapper;
import org.jeecg.modules.activiti.service.AppSystemService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AppSystemServiceImpl extends ServiceImpl<AppSystemMapper, AppSystem> implements AppSystemService {
    
    @Resource
    private AppSystemMapper appSystemMapper;
    /**
     * 真实删除
     *
     * @param id
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public void deleteSystem(String id) throws JeecgBootException {
        AppSystem appSystem = this.getById(id);
        if(appSystem==null) {
            throw new JeecgBootException("未找到系统信息");
        }
        appSystemMapper.deleteById(id);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public void deleteSystemLogical(String id) throws JeecgBootException {
        AppSystem appSystem = this.getById(id);
        if(appSystem==null) {
            throw new JeecgBootException("未找到菜单信息");
        }
        appSystem.setDelFlag(1);
        this.updateById(appSystem);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public void addSystem(AppSystem appSystem) throws JeecgBootException {
        appSystem.setCreateTime(new Date());
        appSystem.setDelFlag(0);
        this.save(appSystem);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public void editSystem(AppSystem appSystem) throws JeecgBootException {
        AppSystem p = this.getById(appSystem.getId());
        if(p==null) {
            throw new JeecgBootException("未找到系统信息");
        }else {
            appSystem.setUpdateTime(new Date());
            this.updateById(appSystem);
        }
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public List<AppSystem> selectName() throws JeecgBootException {
        return appSystemMapper.selectNameAndUrl();
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true)
    public List<AppSystem> selectNameAndUrl(String id) throws JeecgBootException {
        return appSystemMapper.selectUrl(id);
    }

}
