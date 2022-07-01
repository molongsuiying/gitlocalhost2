package org.jeecg.modules.activiti.entity;

import lombok.Data;

import java.util.Date;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Data
public class ActApplyUserFormVo {

    private String tableName;

    private String tableId;

    private int visitFlag;

    private String visitRemark;

    private String routeName;

    private int complainFlag;

    private String complainRemark;

    private String complainRank;

    private Date complainTime;

    private Date visitTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getVisitFlag() {
        return visitFlag;
    }

    public void setVisitFlag(int visitFlag) {
        this.visitFlag = visitFlag;
    }

    public String getVisitRemark() {
        return visitRemark;
    }

    public void setVisitRemark(String visitRemark) {
        this.visitRemark = visitRemark;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getComplainFlag() {
        return complainFlag;
    }

    public void setComplainFlag(int complainFlag) {
        this.complainFlag = complainFlag;
    }

    public String getComplainRemark() {
        return complainRemark;
    }

    public void setComplainRemark(String complainRemark) {
        this.complainRemark = complainRemark;
    }

    public Date getComplainTime() {
        return complainTime;
    }

    public void setComplainTime(Date complainTime) {
        this.complainTime = complainTime;
    }

    public String getComplainRank() {
        return complainRank;
    }

    public void setComplainRank(String complainRank) {
        this.complainRank = complainRank;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }
}
