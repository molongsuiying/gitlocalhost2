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
 * @description: 及时接受扩展表
 */

@Data
@TableName("ACT_HI_TASKINST_SUPERVISE")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ACT_HI_TASKINST_SUPERVISE", description="流程业务扩展表-督办记录表")
public class ActHiTaskSupervise {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**流程实例id*/
    @Excel(name = "taskId", width = 15)
    @ApiModelProperty(value = "taskId")
    private String taskId;

    @Excel(name = "流程业务扩展ID", width = 15)
    @ApiModelProperty(value = "流程业务扩展ID")
    private String businessId;

    /**createBy*/
    @Excel(name = "createBy", width = 15)
    private String createBy;

    /**createBy*/
    @Excel(name = "superviseUserId", width = 15)
    private String superviseUserId;

    /**createTime*/
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**oldDueTime*/
    @Excel(name = "oldDueTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date oldDueTime;

    /**nowDueTime*/
    @Excel(name = "nowDueTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date nowDueTime;
}
