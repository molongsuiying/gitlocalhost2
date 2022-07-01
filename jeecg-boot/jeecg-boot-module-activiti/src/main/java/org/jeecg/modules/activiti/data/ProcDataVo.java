package org.jeecg.modules.activiti.data;

import lombok.Data;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Data
public class ProcDataVo {

    private String departId;

    private String departCode;

    private String departName;

    private String parentId;

    private String userId;

    private String businessId;

    private int status;

    private int visitFlag;

    private int postponeFlag;

    private int reFlag;

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVisitFlag() {
        return visitFlag;
    }

    public void setVisitFlag(int visitFlag) {
        this.visitFlag = visitFlag;
    }

    public int getPostponeFlag() {
        return postponeFlag;
    }

    public void setPostponeFlag(int postponeFlag) {
        this.postponeFlag = postponeFlag;
    }

    public int getReFlag() {
        return reFlag;
    }

    public void setReFlag(int reFlag) {
        this.reFlag = reFlag;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

}
