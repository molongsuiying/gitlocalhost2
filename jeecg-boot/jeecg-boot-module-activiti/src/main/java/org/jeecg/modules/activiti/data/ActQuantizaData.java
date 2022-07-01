package org.jeecg.modules.activiti.data;

import lombok.Data;

import java.util.Objects;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description: 量化统计实例
 */
@Data
public class ActQuantizaData {

    //部门CODE
    private String departCode;

    //部门名称
    private String departName;

    //处理工单量
    private int handleNum;

    //总工单量
    private int totalNum;

    //待接收工单
    private int todoNum;

    //及时接单量
    private int awayNum;

    //未及时接单量
    private int unAwayNum;

    //下派量
    private int underNum;

    //退回量
    private int refuseNum;

    //督办量
    private int superviseNum;

    //延期量
    private int postponeNum;

    //逾期量
    private int dueNum;

    //重办量
    private int reNum;

    //平均时长
    private int averageTime;

    //平均接收时长
    private int averageAwayTime;

    private int normalNum;

    private int doneNum;


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

    public int getHandleNum() {
        return handleNum;
    }

    public void setHandleNum(int handleNum) {
        this.handleNum = handleNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getAwayNum() {
        return awayNum;
    }

    public void setAwayNum(int awayNum) {
        this.awayNum = awayNum;
    }

    public int getUnAwayNum() {
        return unAwayNum;
    }

    public void setUnAwayNum(int unAwayNum) {
        this.unAwayNum = unAwayNum;
    }

    public int getUnderNum() {
        return underNum;
    }

    public void setUnderNum(int underNum) {
        this.underNum = underNum;
    }

    public int getSuperviseNum() {
        return superviseNum;
    }

    public void setSuperviseNum(int superviseNum) {
        this.superviseNum = superviseNum;
    }

    public int getPostponeNum() {
        return postponeNum;
    }

    public void setPostponeNum(int postponeNum) {
        this.postponeNum = postponeNum;
    }

    public int getDueNum() {
        return dueNum;
    }

    public void setDueNum(int dueNum) {
        this.dueNum = dueNum;
    }

    public int getReNum() {
        return reNum;
    }

    public void setReNum(int reNum) {
        this.reNum = reNum;
    }

    public int getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    public int getAverageAwayTime() {
        return averageAwayTime;
    }

    public void setAverageAwayTime(int averageAwayTime) {
        this.averageAwayTime = averageAwayTime;
    }

    public int getRefuseNum() {
        return refuseNum;
    }

    public void setRefuseNum(int refuseNum) {
        this.refuseNum = refuseNum;
    }

    public int getTodoNum() {
        return todoNum;
    }

    public void setTodoNum(int todoNum) {
        this.todoNum = todoNum;
    }

    public ActQuantizaData() {
    }

    public int getNormalNum() {
        return normalNum;
    }

    public void setNormalNum(int normalNum) {
        this.normalNum = normalNum;
    }

    public int getDoneNum() {
        return doneNum;
    }

    public void setDoneNum(int doneNum) {
        this.doneNum = doneNum;
    }

    public ActQuantizaData(String departCode, String departName) {
        this.departCode = departCode;
        this.departName = departName;
        this.handleNum = 0;
        this.totalNum =  0;
        this.todoNum = 0;
        this.awayNum =  0;
        this.unAwayNum =  0;
        this.underNum =  0;
        this.refuseNum = 0;
        this.superviseNum =  0;
        this.postponeNum =  0;
        this.dueNum =  0;
        this.reNum =  0;
        this.averageTime =  0;
        this.averageAwayTime =  0;
        this.normalNum = 0;
        this.doneNum = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActQuantizaData that = (ActQuantizaData) o;
        return Objects.equals(departCode, that.departCode) &&
                Objects.equals(departName, that.departName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(departCode, departName);
    }
}
