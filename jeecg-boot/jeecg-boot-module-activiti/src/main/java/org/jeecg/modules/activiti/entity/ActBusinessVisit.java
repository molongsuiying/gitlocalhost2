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
@TableName("act_z_business_visit对象")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="act_z_business_visit对象", description="工单流程回访表")
public class ActBusinessVisit {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Long id;

    @Excel(name = "流程业务扩展ID", width = 15)
    @ApiModelProperty(value = "流程业务扩展ID")
    private String businessId;

    @Excel(name = "流程ID", width = 15)
    @ApiModelProperty(value = "流程业务扩展ID")
    private String procInstId;

    /**visitor*/
    @Excel(name = "visitor", width = 15)
    private String visitor;

    /**回访时间*/
    @Excel(name = "回访时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date visitTime;

    /**回访说明*/
    @Excel(name = "visitRemark", width = 15)
    private String visitRemark;

    /**回访满意度*/
    @Excel(name = "visitRank", width = 15)
    private String visitRank;

    @Excel(name = "最新", width = 15)
    @ApiModelProperty(value = "流程实例id")
    private Integer latest;
}
