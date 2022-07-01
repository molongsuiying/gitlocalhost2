package org.jeecg.modules.college.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.college.util.Excel;
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
@NoArgsConstructor
@AllArgsConstructor
public class ReportVo {

    /**标题*/
    @Excel(name = "标题")
    private String title;
    /**负责人*/
    @Excel(name = "负责人")
    private String leader;
    /**媒体类别*/
    @Excel(name = "媒体类别")
    private String mediumType;

    /**媒体名称*/
    @Excel(name = "媒体名称")
    private String mediumName;
    /**作者*/
    @Excel(name = "作者署名")
    private String author;
    /**成果类别*/
    @Excel(name = "成果类别")
    private String achievementType;

    /**成果级别*/
    @Excel(name = "成果级别")
    private String achievementLevel;
    /**成果级别备注*/

    /**获取时间*/
    @Excel(name = "授发时间", dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date acquireTime;

    @Excel(name = "团队成员")
    private String teamPersons;

    @Excel(name = "所属专业")
    private String majorName;

    private Integer status;

}
