package org.jeecg.modules.activiti.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.activiti.entity.AppPermission;
import org.jeecg.modules.activiti.entity.AppSystem;

import java.util.List;

public interface AppSystemService extends IService<AppSystem> {
    /**真实删除*/
    public  void deleteSystem(String id) throws JeecgBootException;
    /**逻辑删除*/
    public void deleteSystemLogical(String id) throws JeecgBootException;

    public void addSystem(AppSystem appSystem) throws JeecgBootException;

    public void editSystem(AppSystem appSystem) throws JeecgBootException;

    //查询所有信息
    public List<AppSystem> selectName() throws JeecgBootException;
    //查询除自己之外的所有信息
    public List<AppSystem> selectNameAndUrl(String id) throws JeecgBootException;

}
