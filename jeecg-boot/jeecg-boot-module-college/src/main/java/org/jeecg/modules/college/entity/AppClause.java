package org.jeecg.modules.college.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * <p>
 * 条款分类表
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppClause implements Serializable {

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
     * 条款名称
     */
    private String name;

    /**
     * 条款图标
     */
    private String icon;

    /**
     * 路径
     */
    private String url;

    /**
     * 条款排序
     */
    private Double sortNo;

    /**
     * 类型（0：一级条款；1：二级条款 ；3：三级条款）
     */
    @Dict(dicCode = "menu_type")
    private Integer menuType;

    /**
     * 是否叶子节点: 1:是  0:不是
     */
    @TableField(value="is_leaf")
    private boolean leaf;

    /**
     * 是否路由菜单: 0:不是  1:是（默认值1）
     */
    @TableField(value="is_route")
    private boolean route;


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
     * 是否配置条款的数据权限 1是0否 默认0
     */
    private Integer ruleFlag;

    /**
     * 是否隐藏路由菜单: 0否,1是（默认值0）
     */
    private boolean hidden;

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
    private java.lang.String status;

    /**alwaysShow*/
    private boolean alwaysShow;

    /*update_begin author:wuxianquan date:20190908 for:实体增加字段 */
    /** 外链菜单打开方式 0/内部打开 1/外部打开 */
    private boolean internalOrExternal;
    /*update_end author:wuxianquan date:20190908 for:实体增加字段 */

    private String parentName;

    private String TopParentName;
}
