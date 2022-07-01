package org.jeecg.modules.activiti.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("act_z_business_extra")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="act_z_business_extra对象", description="流程业务扩展数据表")
public class ActBusinessExtra {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    /**createBy*/
    @Excel(name = "createBy", width = 15)
    private String createBy;
    /**createTime*/
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**delFlag*/
    @Excel(name = "delFlag", width = 15)
    private Integer delFlag;
    /**updateBy*/
    @Excel(name = "updateBy", width = 15)
    private String updateBy;
    /**updateTime*/
    @Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**流程定义id*/
    @Excel(name = "流程业务扩展ID", width = 15)
    @ApiModelProperty(value = "流程业务扩展ID")
    private String businessId;
    /**流程实例id*/
    @Excel(name = "流程实例id", width = 15)
    @ApiModelProperty(value = "流程实例id")
    private String procInstId;

    /**节点ID*/
    @Excel(name = "节点Key", width = 15)
    @ApiModelProperty(value = "节点Key")
    private String taskKey;

    /**部门code*/
    @Excel(name = "部门code", width = 15)
    @ApiModelProperty(value = "部门code")
    private String departCode;

    /**用户ids*/
    @Excel(name = "用户ids", width = 15)
    private String userIds;

    /**延期标记 状态 0默认 1申请过延期*/
    @Excel(name = "延期标记 0默认 1申请过延期", width = 15)
    private Integer postponeFlag;

    /**重办标记 0默认 1重办工单*/
    @Excel(name = "重办标记 0默认 1重办工单", width = 15)
    private Integer registerFlag;

    /**退回标记 0正常 1退回*/
    @Excel(name = "0未接收 1已接收", width = 15)
    private Integer procStatus;

    /**当前工单顺序*/
    @Excel(name = "当前工单顺序", width = 15)
    private Integer procSort;

    /**是否火速工单 0正常 1是*/
    @Excel(name = "是否火速工单 0正常 1是", width = 15)
    private Integer speedStatus;
}
