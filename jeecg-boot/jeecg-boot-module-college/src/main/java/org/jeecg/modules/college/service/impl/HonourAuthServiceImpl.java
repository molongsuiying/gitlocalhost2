package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.mapper.HonourAuthItemMapper;
import org.jeecg.modules.college.mapper.HonourAuthMapper;
import org.jeecg.modules.college.mapper.HonourMapper;
import org.jeecg.modules.college.service.HonourAuthService;
import org.jeecg.modules.college.vo.AuthVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class HonourAuthServiceImpl extends ServiceImpl<HonourAuthMapper, HonourAuth> implements HonourAuthService{


    @Resource
    private HonourAuthMapper authMapper;

    @Resource
    private HonourMapper honourMapper;

    @Resource
    private HonourAuthItemMapper itemMapper;

    @Override
    public List<HonourAuth> queryList(Map<String, Object> params) {
        return authMapper.queryList(params);

    }

    @Override
    public long countList(Map<String, Object> params) {
        return authMapper.countList(params);
    }

    @Override
    public List<HonourAuthItem> queryItemList(Map<String, Object> params) {
        return itemMapper.queryList(params);
    }

    @Override
    public long countItemList(Map<String, Object> params) {
        return itemMapper.countList(params);
    }



    @Override
    public int findByAuthIdAndType(String authId, int honourType) {
        return itemMapper.findByAuthIdAndType(authId,honourType);
    }

    @Override
    public void removeByUserId(String userId) {
        authMapper.removeByUserId(userId);
        itemMapper.removeByUserId(userId);
    }


    @Override
    @Transactional
    public void batchSave(AuthVo vo) {
        String nameList = vo.getMultiUser();

        List<String> userNames = Arrays.asList(nameList.split(","));

        List<HonourAuth> authList =new ArrayList<>();
        for (int i = 0; i < userNames.size(); i++) {
            String username = userNames.get(i);
            String userId = honourMapper.getUserIdByUsername(username);
            authMapper.removeByUserId(userId);
            itemMapper.removeByUserId(userId);

            HonourAuth auth = new HonourAuth();
            auth.setShowAuth(vo.getShowAuth());
            auth.setShowDeadline(vo.getShowDeadline());
            auth.setEditAuth(vo.getEditAuth());
            auth.setEditDeadline(vo.getEditDeadline());
            auth.setDownloadAuth(vo.getDownloadAuth());
            auth.setDownloadDeadline(vo.getDownloadDeadline());
            auth.setExamineAuth(vo.getExamineAuth());
            auth.setExamineDeadline(vo.getExamineDeadline());
            auth.setExportAuth(vo.getExportAuth());
            auth.setExportDeadline(vo.getExportDeadline());
            auth.setUserId(userId);
            auth.setAuthType(vo.getAuthType());
            authList.add(auth);
        }
        this.saveBatch(authList);

        for (int i = 0; i < authList.size(); i++) {
            HonourAuth auth = authList.get(i);
            for (int j = 1; j < 8; j++) {
                HonourAuthItem item = new HonourAuthItem();
                BeanUtils.copyProperties(auth,item);
                item.setAuthId(auth.getId());
                item.setId(null);
                item.setHonourType(j);
                itemMapper.insert(item);
            }
        }

    }

    @Override
    public void updateAuthEvery() {
        authMapper.updateExportAuthEveryDay();
        authMapper.updateDownAuthEveryDay();
        itemMapper.updateDownAuthEveryDay();
        itemMapper.updateExportAuthEveryDay();
    }

}
