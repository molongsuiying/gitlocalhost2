package org.jeecg.modules.college.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.common.aspect.annotation.Dict;
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
public class HonourEntityVo {

    private String id;

    @Dict(dicCode = "honour_class")
    private String honourClass;

    private String title;

    private String leader;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date acquireTime;

    private String majorId;
    @TableField(exist = false)
    private String majorName;

    private Integer status;

    private String appendixIds;

    private String remarks;

    private String teamPersons;
}
