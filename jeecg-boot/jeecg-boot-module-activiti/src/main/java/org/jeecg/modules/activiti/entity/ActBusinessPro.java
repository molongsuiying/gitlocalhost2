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
@TableName("act_z_business_pro")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="act_z_business_pro对象", description="工单流程关联表")
public class ActBusinessPro {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name = "流程业务扩展ID", width = 15)
    @ApiModelProperty(value = "流程业务扩展ID")
    private String businessId;
    /**流程实例id*/
    @Excel(name = "流程实例id", width = 15)
    @ApiModelProperty(value = "流程实例id")
    private String procInstId;

    /**流程实例id*/
    @Excel(name = "最新", width = 15)
    @ApiModelProperty(value = "流程实例id")
    private Integer latest;
    /**createBy*/
    @Excel(name = "createBy", width = 15)
    private String createBy;
    /**createTime*/
    @Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
