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
public class ActCenterData {

    private String departCode;

    private String departName;

    private String userId;

    private String username;

    private String roleName;

    private String roleCode;

    //带分配工单量
    private int todoNum;

    //已分配工单量
    private int doneNum;

    //主动退还工单量
    private int refuseNum;

    //被退还工单量
    private int refusedNum;

    //平均分配时长
    private int averageTime;

    //及时分配的工单量
    private int passNum;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public int getTodoNum() {
        return todoNum;
    }

    public void setTodoNum(int todoNum) {
        this.todoNum = todoNum;
    }

    public int getDoneNum() {
        return doneNum;
    }

    public void setDoneNum(int doneNum) {
        this.doneNum = doneNum;
    }

    public int getRefuseNum() {
        return refuseNum;
    }

    public void setRefuseNum(int refuseNum) {
        this.refuseNum = refuseNum;
    }

    public int getRefusedNum() {
        return refusedNum;
    }

    public void setRefusedNum(int refusedNum) {
        this.refusedNum = refusedNum;
    }

    public int getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    public int getPassNum() {
        return passNum;
    }

    public void setPassNum(int passNum) {
        this.passNum = passNum;
    }


}
