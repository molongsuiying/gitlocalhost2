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
@TableName("zd_honour_file")
public class HonourFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    /**标题*/
    private String title;
    /**发文字号*/
    private String articleNum;
    /**发文机关*/
    @Dict(dicCode = "certificate_authority")
    private Integer authorityType;
    /**发文机关备注*/
    private String authorityTxt;
    /**成果类别*/
    @Dict(dicCode = "achievement_type")
    private Integer achievementType;
    /**成果类别备注*/
    private String achievementTypeTxt;
    /**成果级别*/
    @Dict(dicCode = "achievement_level")
    private Integer achievementLevel;
    /**成果级别备注*/
    private String achievementLevelTxt;
    /**荣誉级别*/
    @Dict(dicCode = "honour_level")
    private Integer honourLevel;
    /**作品名称*/
    private String workName;
    /**专业ID*/
    private String majorId;
    /**附件IDS*/
    private String appendixIds;
    /**获取时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date acquireTime;
    /**状态 1 审核中 2通过 -1驳回 0 未提交*/
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
