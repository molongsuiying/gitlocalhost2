package org.jeecg.modules.activiti.data;

import lombok.Data;

import java.util.Objects;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Data
public class ActProcessData {

    private String departCode;

    private String departName;

    /**
     * 工单总量
     */
    private Integer totalNum;

    /**
     * 待办总量
     */
    private Integer todoNum;

    /**
     * 退回总量
     */
    private Integer refuseNum;

    /**
     * 进行中
     */
    private Integer instNum;

    /**
     * 进行中-正常
     */
    private Integer instNormalNum;

    /**
     * 进行中-重新办理
     */
    private Integer instReNum;

    /**
     * 进行中-逾期
     */
    private Integer instDueNum;

    /**
     * 进行中-逾期-重办
     */
    private Integer instReAndDueNum;

    /**
     * 待回访
     */
    private Integer visitNum;

    /**
     * 已完成
     */
    private Integer doneNum;

    /**
     * 已完成-正常
     */
    private Integer doneNormalNum;

    /**
     * 已完成-重办
     */
    private Integer doneReNum;

    /**
     * 已完成-逾期
     */
    private Integer doneDueNum;


    /**
     * 已完成-逾期-重办
     */
    private Integer doneReAndDueNum;

    private Integer hasChildren;

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

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTodoNum() {
        return todoNum;
    }

    public void setTodoNum(Integer todoNum) {
        this.todoNum = todoNum;
    }

    public Integer getRefuseNum() {
        return refuseNum;
    }

    public void setRefuseNum(Integer refuseNum) {
        this.refuseNum = refuseNum;
    }

    public Integer getInstNum() {
        return instNum;
    }

    public void setInstNum(Integer instNum) {
        this.instNum = instNum;
    }

    public Integer getInstNormalNum() {
        return instNormalNum;
    }

    public void setInstNormalNum(Integer instNormalNum) {
        this.instNormalNum = instNormalNum;
    }

    public Integer getInstReNum() {
        return instReNum;
    }

    public void setInstReNum(Integer instReNum) {
        this.instReNum = instReNum;
    }

    public Integer getInstDueNum() {
        return instDueNum;
    }

    public void setInstDueNum(Integer instDueNum) {
        this.instDueNum = instDueNum;
    }

    public Integer getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Integer visitNum) {
        this.visitNum = visitNum;
    }

    public Integer getDoneNum() {
        return doneNum;
    }

    public void setDoneNum(Integer doneNum) {
        this.doneNum = doneNum;
    }

    public Integer getDoneNormalNum() {
        return doneNormalNum;
    }

    public void setDoneNormalNum(Integer doneNormalNum) {
        this.doneNormalNum = doneNormalNum;
    }

    public Integer getDoneReNum() {
        return doneReNum;
    }

    public void setDoneReNum(Integer doneReNum) {
        this.doneReNum = doneReNum;
    }

    public Integer getDoneDueNum() {
        return doneDueNum;
    }

    public void setDoneDueNum(Integer doneDueNum) {
        this.doneDueNum = doneDueNum;
    }

    public Integer getInstReAndDueNum() {
        return instReAndDueNum;
    }

    public void setInstReAndDueNum(Integer instReAndDueNum) {
        this.instReAndDueNum = instReAndDueNum;
    }

    public Integer getDoneReAndDueNum() {
        return doneReAndDueNum;
    }

    public void setDoneReAndDueNum(Integer doneReAndDueNum) {
        this.doneReAndDueNum = doneReAndDueNum;
    }

    public Integer getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Integer hasChildren) {
        this.hasChildren = hasChildren;
    }

    public ActProcessData() {
    }

    public ActProcessData(String departCode, String departName) {
        this.departCode = departCode;
        this.departName = departName;
        this.totalNum = 0;
        this.todoNum = 0;
        this.refuseNum = 0;
        this.instNum = 0;
        this.instNormalNum = 0;
        this.instReNum = 0;
        this.instDueNum = 0;
        this.instReAndDueNum = 0;
        this.visitNum = 0;
        this.doneNum = 0;
        this.doneNormalNum = 0;
        this.doneReNum = 0;
        this.doneDueNum = 0;
        this.doneReAndDueNum = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActProcessData that = (ActProcessData) o;
        return Objects.equals(departCode, that.departCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(departCode);
    }
}
