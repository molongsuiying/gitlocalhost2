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
public class PersonVo {

    /**标题*/
    @Excel(name = "标题")
    private String title;

    /**负责人*/
    @Excel(name = "负责人")
    private String leader;

    /**证书类型*/
    @Excel(name = "个人证书")
    private String certificateType;
    /**授发单位*/
    @Excel(name = "授发单位")
    private String acquireUnit;

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
