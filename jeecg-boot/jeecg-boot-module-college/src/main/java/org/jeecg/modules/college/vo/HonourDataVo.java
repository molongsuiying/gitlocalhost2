package org.jeecg.modules.college.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class HonourDataVo {

    private String honourClass;

    private Date createTime;

    private String time;
}
