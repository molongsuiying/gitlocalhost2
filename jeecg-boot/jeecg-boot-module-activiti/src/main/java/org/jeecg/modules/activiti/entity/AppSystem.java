package org.jeecg.modules.activiti.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AppSystem {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 菜单名称
     */
    private String name;


    /**
     * 菜单图标
     */
    private String icon;


    /**
     * 路径
     */
    private String url;

    /**
     * 菜单排序
     */
    private Double sortNo;



    /**
     * 是否缓存页面: 0:不是  1:是（默认值1）
     */
    @TableField(value="keep_alive")
    private boolean keepAlive;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 删除状态 0正常 1已删除
     */
    private Integer delFlag;

    /**
     * 是否配置菜单的数据权限 1是0否 默认0
     */
    private Integer ruleFlag;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**按钮权限状态(0无效1有效)*/
    private String status;

    /**alwaysShow*/
    private boolean alwaysShow;

    /*update_begin author:wuxianquan date:20190908 for:实体增加字段 */
    /** 外链菜单打开方式 0/内部打开 1/外部打开 */
    private boolean internalOrExternal;

    private List<String> role;

}
