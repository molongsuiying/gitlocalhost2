package org.jeecg.modules.activiti.entity;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.activiti.engine.history.HistoricTaskInstance;
import org.jeecg.common.system.vo.LoginUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pmc
 */
@Data
public class HistoricTaskVo {

    private String id;

    private String name;

    private String key;

    private String description;

    private String executionId;

    private String assignee;

    private String owner;

    private String procDefId;

    private String procInstId;

    private String applyer;

    private String category;

    private Integer priority;

    private String deleteReason;

    private String comment;

    private Long duration;

    private Long workTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    private String processName;

    private String routeName;

    private String businessKey;

    private String tableId;
    private String tableName;

    private List<Assignee> assignees;
    private String title;
    private Integer status;
    private Integer result;
    private Map dataMap;

    private List<LoginUser> users;

    public HistoricTaskVo(HistoricTaskInstance task){
        this.id = task.getId();
        this.name = task.getName();
        this.key = task.getTaskDefinitionKey();
        this.description = task.getDescription();
        this.executionId = task.getExecutionId();
        this.assignee = task.getAssignee();
        this.owner = task.getOwner();
        this.procDefId = task.getProcessDefinitionId();
        this.procInstId = task.getProcessInstanceId();
        this.priority = task.getPriority();
        this.category = task.getCategory();
        this.deleteReason = getMyDeleteReason(task.getDeleteReason());
        this.duration = task.getDurationInMillis();
        this.workTime = task.getWorkTimeInMillis();
        this.createTime = task.getCreateTime();
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
        this.dueTime = task.getDueDate();
    }

    public String getMyDeleteReason(String deleteReason){

        if(StrUtil.isBlank(deleteReason)){
            return "";
        }
        if(ActivitiConstant.PASSED_FLAG.equals(deleteReason)){
            deleteReason = "????????????";
        }else if(ActivitiConstant.BACKED_FLAG.equals(deleteReason)){
            deleteReason = "????????????";
        }else if(ActivitiConstant.URGENT_FLAG.equals(deleteReason)){
            deleteReason = "??????";
        }else if(ActivitiConstant.SUPERVISE_FLAG.equals(deleteReason)){
            deleteReason = "??????";
        }else if(ActivitiConstant.UNITE_FLAG.equals(deleteReason)){
            deleteReason = "????????????";
        }else if(ActivitiConstant.EXECUTOR_unite_agree.equals(deleteReason)){
            deleteReason = "????????????-??????";
        }else if(ActivitiConstant.EXECUTOR_postpone.equals(deleteReason)){
            deleteReason = "????????????";
        }else if(deleteReason.contains(ActivitiConstant.DELETE_PRE)){
            String reason = "";
            if(ActivitiConstant.DELETE_PRE.equals(deleteReason)){
                reason = "?????????";
            }else if(deleteReason.length()>8){
                reason = deleteReason.substring(8);
            }
            deleteReason = "????????????-??????"+reason;
        }else if(deleteReason.contains(ActivitiConstant.CANCEL_PRE)){
            String reason = "";
            if(ActivitiConstant.CANCEL_PRE.equals(deleteReason)){
                reason = "?????????";
            }else if(deleteReason.length()>9){
                reason = deleteReason.substring(9);
            }
            deleteReason = "???????????????-??????"+reason;
        }else if(deleteReason.contains(ActivitiConstant.UNITE_FLAG)){
            String reason = "";
            if(ActivitiConstant.UNITE_FLAG.equals(deleteReason)){
                reason = "?????????";
            }else if(deleteReason.length()>9){
                reason = deleteReason.substring(9);
            }
            deleteReason = "????????????"+reason;
        }else {
            deleteReason="?????????-??????"+deleteReason;
        }
        return deleteReason;
    }
}
