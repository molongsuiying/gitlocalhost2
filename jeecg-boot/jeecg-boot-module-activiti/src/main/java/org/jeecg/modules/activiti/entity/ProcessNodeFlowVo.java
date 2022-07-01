package org.jeecg.modules.activiti.entity;

import java.util.Date;
import java.util.List;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
public class ProcessNodeFlowVo {

    /**节点id*/
    private String id;

    /**流程定义id*/
    private String procDefId;

    /**节点名*/
    private String title;

    /**节点类型 0开始 1用户任务 2结束 3排他网关 4并行网关*/
    private Integer type;

    /**是否当前流程**/
    private Boolean isCurrentNode;

    private List<Assignee> assignees;

    /**备注**/
    private String remark;

    /**开始时间**/
    private Date startTime;

    /**创建时间**/
    private Date endTime;

    private String deleteReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getCurrentNode() {
        return isCurrentNode;
    }

    public void setCurrentNode(Boolean currentNode) {
        isCurrentNode = currentNode;
    }

    public List<Assignee> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Assignee> assignees) {
        this.assignees = assignees;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }
}
