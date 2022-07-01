package org.jeecg.modules.college.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class ArticleVo {

    /**标题*/
    @Excel(name = "标题")
    private String title;

    /**负责人*/
    @Excel(name = "负责人")
    private String leader;

    /**物图类别*/
    @Excel(name = "物图类别")
    private String articleType;

    /**发文机关*/
    @Excel(name = "开展部门")
    private String authorityType;

    /**成果类别*/
    @Excel(name = "成果类别")
    private String achievementType;

    /**成果级别*/
    @Excel(name = "成果级别")
    private String achievementLevel;

    /**发表时间*/
    @Excel(name = "发表时间", dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date acquireTime;

    @Excel(name = "团队成员")
    private String teamPersons;

    @Excel(name = "所属专业")
    private String majorName;

    private Integer status;
}
