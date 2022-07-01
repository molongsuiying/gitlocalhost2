package org.jeecg.modules.activiti.entity;

import lombok.Data;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;
import java.util.Map;

/*节点*/
@Data
public class ProcessNodeVo {

    /**节点id*/
    private String id;
    /**流程定义id*/
    private String procDefId;
    /**节点名*/
    private String title;
    /**节点类型 0开始 1用户任务 2结束 3排他网关 4并行网关*/
    private Integer type;
    /**关联角色*/
    private List<Role> roles;
    /**关联用户*/
    private List<LoginUser> users;
    /**关联部门*/
    private List<Department> departments;
    /**选操作人的部门负责人*/
    private Boolean chooseDepHeader = false;
    /**选操作人*/
    private Boolean chooseSponsor = false;
    /**选发起人的部门相关角色*/
    private Boolean chooseDepRole = false;
    /**选部门下的所有人员*/
    private Boolean chooseDepUser = false;
    /**选自定义配置*/
    private Boolean chooseCustomized = false;
    /**选自定义配置*/
    private Boolean autoDepartFlag = false;
    /**节点展开 前端所需*/
    private Boolean expand = true;
    /**是否联合办理*/
    private Integer unite;
    /**联合办理数据*/
    private Map<String,Object> uniteMap;
}
