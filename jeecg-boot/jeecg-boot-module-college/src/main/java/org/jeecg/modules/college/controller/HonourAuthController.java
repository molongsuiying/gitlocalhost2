package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.HonourAgree;
import org.jeecg.modules.college.entity.HonourAuth;
import org.jeecg.modules.college.entity.HonourAuthItem;
import org.jeecg.modules.college.service.HonourAuthItemService;
import org.jeecg.modules.college.service.HonourAuthService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.AuthVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/honour/auth")
@Slf4j
public class HonourAuthController {

    @Resource
    private HonourAuthService authService;

    @Resource
    private HonourAuthItemService itemService;


    /**
     * 权限类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourAuthList", method = RequestMethod.GET)
    public Result<IPage<HonourAuth>> queryHonourAuthList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourAuth>> result = new Result<>();
        IPage<HonourAuth> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourAuth> pageList = authService.queryList(query);
        long count = authService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 权限类
     * @param
     * @return
     */
    @RequestMapping(value = "/getMyAuth", method = RequestMethod.GET)
    public Result<HonourAuthItem> getMyAuth(@RequestParam(name="honourClass", required = true) Integer honourClass) {
        Result<HonourAuthItem> result = new Result<>();
        HonourAuthItem auth = itemService.findItemByUser(honourClass);
        result.setResult(auth);
        result.setSuccess(true);
        return result;
    }

    /**
     * 权限类
     * @param
     * @return
     */
    @RequestMapping(value = "/getMyAuthList", method = RequestMethod.GET)
    public Result<List<HonourAuthItem>> getMyAuthList() {
        Result<List<HonourAuthItem>> result = new Result<>();
        List<HonourAuthItem> itemList = itemService.list();
        result.setResult(itemList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 权限子类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourAuthItemList", method = RequestMethod.GET)
    public Result<IPage<HonourAuthItem>> queryHonourAuthItemList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {

        Result<IPage<HonourAuthItem>> result = new Result<>();
        IPage<HonourAuthItem> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourAuthItem> pageList = authService.queryItemList(query);
        long count = authService.countItemList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }


    /**
     * 添加
     *
     * @param item
     * @return
     */
    @AutoLog(value = "荣誉权限-添加")
    @ApiOperation(value = "荣誉权限-添加", notes = "荣誉权限-添加")
    @PostMapping(value = "/add")
    public Result<HonourAuth> add(@RequestBody HonourAuthItem item) {
        Result<HonourAuth> result = new Result<>();
        try {
            int num = authService.findByAuthIdAndType(item.getAuthId(),item.getHonourType());
            if(num>0){
                return result.error500("添加失败,该账号已配置该类型权限!");
            }else{
                HonourAuth auth = authService.getById(item.getAuthId());
                item.setUserId(auth.getUserId());
                itemService.save(item);
                result.success("添加成功！");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }



    /**
     * 批量荣誉权限-添加
     *
     * @param vo
     * @return
     */
    @AutoLog(value = "批量荣誉权限-添加")
    @ApiOperation(value = "批量荣誉权限-添加", notes = "批量荣誉权限-添加")
    @PostMapping(value = "/batchSave")
    public Result<HonourAuth> batchSave(@RequestBody AuthVo vo) {
        Result<HonourAuth> result = new Result<>();
        try {
            authService.batchSave(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }


    /**
     * 编辑
     *
     * @param item
     * @return
     */
    @AutoLog(value = "权限表-编辑")
    @ApiOperation(value = "权限表-编辑", notes = "权限表-编辑")
    @PutMapping(value = "/edit")
    public Result<HonourAuthItem> edit(@RequestBody HonourAuthItem item) {
        Result<HonourAuthItem> result = new Result<>();
        HonourAuthItem auth = itemService.getById(item.getId());
        if (auth == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = itemService.updateById(item);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * @功能：删除
     * @param userId
     * @return
     */
    @RequestMapping(value = "/deleteByUserId", method = RequestMethod.DELETE)
    public Result<HonourAuth> deleteByUserId(@RequestParam(name="userId",required=true) String userId) {
        Result<HonourAuth> result = new Result<>();

        try {
            authService.removeByUserId(userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * @功能：删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<HonourAuth> delete(@RequestParam(name="id",required=true) String id) {
        Result<HonourAuth> result = new Result<>();
        boolean ok = authService.removeById(id);
        if(ok) {
            result.success("删除成功!");
        }else{
            result.error500("删除失败!");
        }
        return result;
    }

    /**
     * @功能：删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteItem", method = RequestMethod.DELETE)
    public Result<HonourAuthItem> deleteItem(@RequestParam(name="id",required=true) String id) {
        Result<HonourAuthItem> result = new Result<>();
        itemService.removeById(id);
        result.success("删除成功!");
        return result;
    }
}
