package org.jeecg.modules.activiti.data;

import lombok.Data;


/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description: 回访组统计
 */
@Data
public class ActVisitData {

    private String userId;

    //用户名
    private String username;

    //回访工单量
    private int visitBusinessNum;

    //回访次数
    private int visitNum;

    //重新回访次数

    private int reVisitNum;

    //平均回访时长
    private int avgTime;

    //回访及格率 回访及时的工单/已回访的工单量
    private int passNum;


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

    public int getVisitBusinessNum() {
        return visitBusinessNum;
    }

    public void setVisitBusinessNum(int visitBusinessNum) {
        this.visitBusinessNum = visitBusinessNum;
    }

    public int getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(int visitNum) {
        this.visitNum = visitNum;
    }

    public int getReVisitNum() {
        return reVisitNum;
    }

    public void setReVisitNum(int reVisitNum) {
        this.reVisitNum = reVisitNum;
    }

    public int getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(int avgTime) {
        this.avgTime = avgTime;
    }

    public int getPassNum() {
        return passNum;
    }

    public void setPassNum(int passNum) {
        this.passNum = passNum;
    }

}
