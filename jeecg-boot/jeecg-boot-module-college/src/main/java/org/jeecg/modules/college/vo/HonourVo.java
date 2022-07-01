package org.jeecg.modules.college.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class HonourVo {

    private String id;

    private List<String> appendixIds;
}
