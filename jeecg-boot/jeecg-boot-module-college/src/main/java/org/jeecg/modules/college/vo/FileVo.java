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
public class FileVo {

    /**标题*/
    @Excel(name = "标题")
    private String title;

    /**负责人*/
    @Excel(name = "负责人")
    private String leader;

    @Excel(name = "作品名称")
    private String workName;

    @Excel(name = "荣誉等级")
    private String honourLevel;

    @Excel(name = "发文字号")
    private String articleNum;

    /**发文机关*/
    @Excel(name = "发文机关")
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

    private String appendixIds;

    private Integer status;
}
