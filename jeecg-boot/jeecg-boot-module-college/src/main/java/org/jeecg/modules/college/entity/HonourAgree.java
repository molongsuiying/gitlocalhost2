package org.jeecg.modules.college.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("zd_honour_agree")
public class HonourAgree implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    /**标题*/
    private String title;
    /**合作类别*/
    @Dict(dicCode = "cooperation_type")
    private Integer cooperationType;
    /**合作类别 其他备注*/
    private String 	cooperationTypeTxt;
    /**合作单位*/
    private String cooperationUnit;
    /**专业ID*/
    private String majorId;
    /**附件IDS*/
    private String appendixIds;
    /**获取时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date acquireTime;
    /**状态 0 未提交 1审核中 2已通过 -1驳回*/
    private Integer status;
    /**删除状态（0，正常，1已删除）*/
    @Dict(dicCode = "del_flag")
    private String delFlag;
    /**创建人*/
    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**更新人*/
    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**排序*/
    private Integer sort;
    /**是否公开 0默认公开  1:不公开*/
    private Integer openShow;
    @TableField(exist = false)
    private String majorName;
    /**备注 驳回*/
    private String remarks;

    /**负责人*/
    private String leader;
    /**团队成员*/
    private String teamPersons;
}
