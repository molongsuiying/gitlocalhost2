package org.jeecg.modules.activiti.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Data
@TableName("act_z_task_unite")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="act_z_task_unite对象", description="流程节点联合扩展表2")
public class ActTaskUnite {

    /**id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**流程定义id*/
    private String procDefId;

    /**流程实例id*/
    private String procInstId;

    /**委托人*/
    private String owner;

    /**审批人*/
    private String assignee;
    /**createBy*/
    @Excel(name = "createBy", width = 15)
    @ApiModelProperty(value = "createBy")
    private String createBy;
    /**createTime*/
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
    private Date createTime;
    /**delFlag*/
    @Excel(name = "delFlag", width = 15)
    @ApiModelProperty(value = "delFlag")
    private Integer delFlag;
    /**updateBy*/
    @Excel(name = "updateBy", width = 15)
    @ApiModelProperty(value = "updateBy")
    private String updateBy;
    /**updateTime*/
    @Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
    private Date updateTime;
    /**TASK_id*/
    @Excel(name = "TASK_id", width = 15)
    @ApiModelProperty(value = "TASK_id")
    private String taskId;

    /**TASK_KEY*/
    @Excel(name = "TASK_KEY", width = 15)
    @ApiModelProperty(value = "TASK_KEY")
    private String taskKey;

    /**是否已经启动联合*/
    @Excel(name = "是否已经启动联合", width = 15)
    @ApiModelProperty(value = "是否已经启动联合")
    private Integer isStartUnite;


}
