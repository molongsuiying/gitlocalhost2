package org.jeecg.modules.college.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.Honour;
import org.jeecg.modules.college.entity.HonourFile;
import org.jeecg.modules.college.entity.HonourQueryHistory;
import org.jeecg.modules.college.mapper.*;
import org.jeecg.modules.college.service.HonourService;
import org.jeecg.modules.college.util.DateUtil;
import org.jeecg.modules.college.vo.CommonVo;
import org.jeecg.modules.college.vo.HonourDataVo;
import org.jeecg.modules.college.vo.HonourEntityVo;
import org.jeecg.modules.college.vo.RankVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourServiceImpl extends ServiceImpl<HonourMapper, Honour> implements HonourService{

    @Resource
    private HonourMapper honourMapper;

    @Resource
    private HonourFileMapper fileMapper;

    @Resource
    private HonourCertificateMapper certificateMapper;

    @Resource
    private HonourArticleMapper articleMapper;

    @Resource
    private HonourReportMapper reportMapper;

    @Resource
    private HonourAgreeMapper agreeMapper;

    @Resource
    private HonourPersonMapper personMapper;

    @Resource
    private HonourProjectMapper projectMapper;

    @Autowired
    private HonourQueryHistoryMapper historyMapper;

    @Autowired
    private ISysBaseAPI sysBaseAPI;


    @Override
    public List<HonourEntityVo> findHonourAllList(Map<String, Object> params) {


        List<HonourEntityVo> vos = honourMapper.findHonourAllList(params);


        params.remove("order");
        params.remove("column");
        params.remove("field");

        HonourQueryHistory history = new HonourQueryHistory();
        history.setHonourType(CommonConstant.HONOUR_CLASS_ALL);
        history.setQueryParam(params.toString());
        historyMapper.insert(history);
        return vos;
    }

    @Override
    public List<String> findValuesByHonour(Map<String, Object> params) {
        return this.baseMapper.findValuesByHonour(params);
    }

    @Override
    public long countHonourAllList(Map<String, Object> params) {
        return honourMapper.countHonourAllList(params);
    }

    @Override
    public List<RankVo> countRankList() {
        List<CommonVo> commonVos = honourMapper.queryDictMap("honour_class");

        List<RankVo> rankVos = new ArrayList<>();

        for (int i = 0; i < commonVos.size(); i++) {
            CommonVo commonVo = commonVos.get(i);
            RankVo vo = getCount(commonVo);
            rankVos.add(vo);
        }
        //根据总量排序
        rankVos = rankVos.stream().sorted(Comparator.comparing(RankVo::getTotal)).collect(Collectors.toList());
        Collections.reverse(rankVos);
        return rankVos;
    }

    @Override
    public JSONObject findDataVoList(Integer type) {
        List<HonourDataVo> dataVos;
        JSONObject object = new JSONObject();
        Map<String,Object> params = new HashMap<>();
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (type == 1){
            //当天
            Date beginTime = DateUtil.getDayFirst();
            Date endTime = DateUtil.getDayLast();
            params.put("beginTime",simpleDateFormat.format(beginTime));
            params.put("endTime",simpleDateFormat.format(endTime));
            dataVos = honourMapper.findDataVoList(params);
            SimpleDateFormat sdf=new SimpleDateFormat("HH时");

            for (int i = 0; i < dataVos.size(); i++) {
                String createTime = sdf.format(dataVos.get(i).getCreateTime());
                dataVos.get(i).setTime(createTime);
            }
            Map<String,List<HonourDataVo>> result = dataVos.parallelStream().collect(Collectors.groupingBy(HonourDataVo::getTime));
            for (Map.Entry<String, List<HonourDataVo>> entry : result.entrySet()) {
                object.put(entry.getKey(),entry.getValue().size());
            }
        }else if (type == 2){
            //当月
            Date beginTime = DateUtil.getCurrMonthFirst();
            Date endTime = DateUtil.getCurrMonthLast();
            params.put("beginTime",simpleDateFormat.format(beginTime));
            params.put("endTime",simpleDateFormat.format(endTime));
            dataVos = honourMapper.findDataVoList(params);
            SimpleDateFormat sdf=new SimpleDateFormat("dd日");

            for (int i = 0; i < dataVos.size(); i++) {
                String createTime = sdf.format(dataVos.get(i).getCreateTime());
                dataVos.get(i).setTime(createTime);
            }
            Map<String,List<HonourDataVo>> result = dataVos.parallelStream().collect(Collectors.groupingBy(HonourDataVo::getTime));
            for (Map.Entry<String, List<HonourDataVo>> entry : result.entrySet()) {
                object.put(entry.getKey(),entry.getValue().size());
            }
        }else{
            //当年
            Date beginTime = DateUtil.getCurrYearFirst();
            params.put("beginTime",simpleDateFormat.format(beginTime));
            dataVos = honourMapper.findDataVoList(params);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月");

            for (int i = 0; i < dataVos.size(); i++) {
                String createTime = sdf.format(dataVos.get(i).getCreateTime());
                dataVos.get(i).setTime(createTime);
            }
            Map<String,List<HonourDataVo>> result = dataVos.parallelStream().collect(Collectors.groupingBy(HonourDataVo::getTime));
            for (Map.Entry<String, List<HonourDataVo>> entry : result.entrySet()) {
                object.put(entry.getKey(),entry.getValue().size());
            }
        }


        return object;
    }

    @Override
    public Boolean findDuplicateItemByTitle(String id, Map<String, Object> params) {


        List<String> departIds = sysBaseAPI.getMyDepartIds();
        params.put("departIds",departIds);

        List<HonourEntityVo> vos = this.baseMapper.findDuplicateItemByTitle(params);
        if(StringUtils.isBlank(id) && vos.size() > 0){
            return true;
        }
        if(StringUtils.isNotBlank(id)){
            vos = vos.stream().filter(vo -> !id.equals(vo.getId())).collect(Collectors.toList());
            if (vos.size()>0){
                return true;
            }
        }

        return false;
    }


    private RankVo getCount(CommonVo commonVo){
        RankVo vo = new RankVo();
        vo.setName(commonVo.getText());
        long count = 0;
        switch (commonVo.getId()){
            case "1":
                count = fileMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "2":
                count = certificateMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "3":
                count = articleMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "4":
                count = reportMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "5":
                count = agreeMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "6":
                count = personMapper.selectCount(Wrappers.emptyWrapper());
                break;
            case "7":
                count = projectMapper.selectCount(Wrappers.emptyWrapper());
                break;
            default:
                break;
        }
        vo.setTotal(count);
        return vo;
    }
}
