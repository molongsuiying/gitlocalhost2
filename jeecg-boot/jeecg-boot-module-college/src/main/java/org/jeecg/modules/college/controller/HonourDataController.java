package org.jeecg.modules.college.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.service.HonourAppendixHistoryService;
import org.jeecg.modules.college.service.HonourQueryHistoryService;
import org.jeecg.modules.college.service.HonourService;
import org.jeecg.modules.college.util.DateUtil;
import org.jeecg.modules.college.vo.HonourDataVo;
import org.jeecg.modules.college.vo.RankVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/honour/data")
@Slf4j
public class HonourDataController {

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private HonourQueryHistoryService historyService;

    @Autowired
    private HonourAppendixHistoryService appendixHistoryService;

    @Autowired
    private HonourService honourService;




    /**
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryDataList", method = RequestMethod.GET)
    public Result<JSONObject> queryDataList(HttpServletRequest req) {
        Result<JSONObject> result = new Result<>();

        JSONObject object = new JSONObject();
        //登录记录
        long loginCount = sysBaseAPI.countLoginNum();
        object.put("loginCount",loginCount);
        //查询记录
        long queryCount = historyService.count();
        object.put("queryCount",queryCount);
        //下载记录
        long downCount = appendixHistoryService.count();
        object.put("downCount",downCount);

        Date beginTime = DateUtil.getCurrMonthFirst();
        Date endTime = DateUtil.getCurrMonthLast();
        Map<String,Object> params = new HashMap<>();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("beginTime",simpleDateFormat.format(beginTime));
        params.put("endTime",simpleDateFormat.format(endTime));

        JSONObject queryDataVoList = historyService.countByTime(params);
        object.put("queryCountList",queryDataVoList);
        JSONObject downDataVoList = appendixHistoryService.countByTime(params);
        object.put("downDataVoList",downDataVoList);


        result.setResult(object);
        result.setSuccess(true);
        return result;
    }

    /**
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryRankList", method = RequestMethod.GET)
    public Result<List<RankVo>> queryRankList(HttpServletRequest req) {
        Result<List<RankVo>> result = new Result<>();

        //各类型总量
        List<RankVo> voList = honourService.countRankList();

        result.setResult(voList);
        result.setSuccess(true);
        return result;
    }


    /**
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryDataVoList", method = RequestMethod.GET)
    public Result<JSONObject> queryDataVoList(@RequestParam(name="type", required = false) Integer type, HttpServletRequest req) {
        Result<JSONObject> result = new Result<>();

        if (type == null){
            type = 0;
        }
        //各类型总量
        JSONObject object = honourService.findDataVoList(type);
        result.setResult(object);
        result.setSuccess(true);
        return result;
    }
}
