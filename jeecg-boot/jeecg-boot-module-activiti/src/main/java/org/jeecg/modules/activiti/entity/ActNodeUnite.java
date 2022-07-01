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
@TableName("act_z_node_unite")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="act_z_node_unite对象", description="流程节点联合扩展表")
public class ActNodeUnite {

    /**id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**流程定义id*/
    private String procDefId;
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
    /**节点id*/
    @Excel(name = "节点id", width = 15)
    @ApiModelProperty(value = "节点id")
    private String nodeId;

    /**是否联合支持*/
    @Excel(name = "是否需要联合 0:不需要 1需要", width = 15)
    @ApiModelProperty(value = "是否联合支持")
    private Integer isUnite;

    /**联合类型-部门*/
    @Excel(name = "联合类型-部门", width = 15)
    @ApiModelProperty(value = "联合类型-部门")
    private String uniteDepartId;

    /**联合类型-角色*/
    @Excel(name = "联合类型-角色", width = 15)
    @ApiModelProperty(value = "联合类型-角色")
    private String uniteRoleIds;

    /**联合类型-角色*/
    @Excel(name = "联合类型-用户", width = 15)
    @ApiModelProperty(value = "联合类型-用户")
    private String uniteUserIds;
}
