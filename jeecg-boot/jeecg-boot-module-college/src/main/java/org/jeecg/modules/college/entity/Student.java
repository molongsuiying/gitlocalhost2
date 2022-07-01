package org.jeecg.modules.college.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("zd_student")
public class Student implements Serializable{

    private static final long serialVersionUID = 1L;

    /**id*/
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**学生名称*/
    private String studentName;
    /**学号*/
    private String studentCode;
    /**性别*/
    @Dict(dicCode = "sex")
    private Integer sex;
    /**出生日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthday;
    /**身份证*/
    private String idCard;
    /**身高*/
    private Integer height;
    /**体重*/
    private Integer weight;
    /**手机号*/
    private String mobile;
    /**籍贯*/
    private String nativePlace;
    /**民族*/
    private String nation;
    /**邮箱*/
    private String email;
    /**头像*/
    private String icon;
    /**视频*/
    private String video;
    /**班级ID*/
    private String bunchId;
    /**备注*/
    private String remarks;
    /**地区*/
    private String areaLink;
    /**家庭地址*/
    private String address;
    /**户口  1城市 2农村*/
    private Integer registeredResidence;

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

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM")
    @DateTimeFormat(pattern="yyyy-MM")
    private Date admissionTime;

}
