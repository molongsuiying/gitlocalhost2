package org.jeecg.modules.college.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.college.data.HonourTable;
import org.jeecg.modules.college.entity.*;
import org.jeecg.modules.college.service.*;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.util.ExcelUtil;
import org.jeecg.modules.college.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/honour")
@Slf4j
public class HonourController {

    @Resource
    private HonourFileService fileService;
    @Resource
    private HonourReportService reportService;
    @Resource
    private HonourArticleService articleService;
    @Resource
    private HonourAgreeService agreeService;
    @Resource
    private HonourPersonService personService;
    @Resource
    private HonourCertificateService certificateService;
    @Resource
    private HonourAppendixService appendixService;
    @Resource
    private HonourAppendixHistoryService historyService;

    @Resource
    private HonourService honourService;

    @Resource
    private HonourProjectService projectService;

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * 文件类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourFileList", method = RequestMethod.GET)
    public Result<IPage<HonourFile>> queryHonourFileList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourFile>> result = new Result<>();
        IPage<HonourFile> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourFile> pageList = fileService.queryList(query);
        long count = fileService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 证书类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourCertificateList", method = RequestMethod.GET)
    public Result<IPage<HonourCertificate>> queryHonourCertificateList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourCertificate>> result = new Result<>();
        IPage<HonourCertificate> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourCertificate> pageList = certificateService.queryList(query);
        long count = certificateService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 报道类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourReportList", method = RequestMethod.GET)
    public Result<IPage<HonourReport>> queryHonourReportList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                             @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourReport>> result = new Result<>();
        IPage<HonourReport> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourReport> pageList = reportService.queryList(query);
        long count = reportService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 物图类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourArticleList", method = RequestMethod.GET)
    public Result<IPage<HonourArticle>> queryHonourArticleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourArticle>> result = new Result<>();
        IPage<HonourArticle> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourArticle> pageList = articleService.queryList(query);
        long count = articleService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }


    /**
     * 协议类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourAgreeList", method = RequestMethod.GET)
    public Result<IPage<HonourAgree>> queryHonourAgreeList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourAgree>> result = new Result<>();
        IPage<HonourAgree> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourAgree> pageList = agreeService.queryList(query);
        long count = agreeService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 人才类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourPersonList", method = RequestMethod.GET)
    public Result<IPage<HonourPerson>> queryHonourPersonList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                           @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourPerson>> result = new Result<>();
        IPage<HonourPerson> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        List<HonourPerson> pageList = personService.queryList(query);
        long count = personService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }





    /**
     *
     *
     *
     * */
    @RequestMapping(value = "/findDetailById", method = RequestMethod.GET)
    public Result<IPage> checkDuplicateTitle(@Param("id")String id,@Param("title")String title, @Param("honourClass")Integer honourClass) {
        Result<IPage> result = new Result();

        List list = new ArrayList();
        switch (honourClass){
            case 1:
                HonourFile file = fileService.getById(id);
                list.add(file);
                break;
            case 2:
                HonourCertificate cate = certificateService.getById(id);
                list = new ArrayList<>();
                list.add(cate);
                break;
            case 3:
                HonourArticle article = articleService.getById(id);
                list = new ArrayList<>();
                list.add(article);

                break;
            case 4:
                HonourReport report = reportService.getById(id);
                list.add(report);
                break;
            case 5:
                HonourAgree agree = agreeService.getById(id);
                list.add(agree);
                break;
            case 6:
                HonourPerson person = personService.getById(id);
                list.add(person);
                break;
            case 7:
                HonourProject project= projectService.getById(id);
                list.add(project);
                break;
        }
        IPage page = new Page<>(1, 10);
        page.setRecords(list);
        page.setTotal(list.size());
        result.setResult(page);
        result.setMessage("查询成功");
        result.setSuccess(true);

        return result;
    }

    /**
     * @param appendixIds
     * @return
     */
    @RequestMapping(value = "/findAppendixByIds", method = RequestMethod.GET)
    public Result<List<HonourAppendix>> findAppendixByIds(String appendixIds) {
        Result<List<HonourAppendix>> result = new Result<>();

        List<String> ids = new ArrayList<>();

        if (!appendixIds.contains(",")){
            ids.add(appendixIds);
        }else{
            ids = Arrays.asList(appendixIds.split(","));
        }
        List<HonourAppendix> appendix = appendixService.queryListById(ids);
        result.setResult(appendix);
        result.setMessage("插入成功");
        result.setSuccess(true);

        return result;
    }


    /**
     *   添加下载记录
     * @param
     * @return
     */
    @RequestMapping(value = "/saveHistoriesByIds", method = RequestMethod.POST)
    public Result<AppendixVo> saveHistoriesByIds(@RequestBody List<AppendixVo> vos) {
        Result<AppendixVo> result = new Result<>();

        try {
            historyService.saveHistories(vos);
            result.success("保存成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  导出EXCEL
     * @param
     * @return
     */
    @RequestMapping(value = "/exportAllXls", method = RequestMethod.POST)
    public Result<String> exportAllXls(@RequestBody JSONObject jsonObject) {
        Result<String> result = new Result<>();

        String biz = "excel";

        JSONArray fieldsNames = jsonObject.getJSONArray("fieldsNames");
        Map query = JSONObject.parseObject(jsonObject.getJSONObject("queryParam").toString());



        String honourClass = "";
        if ( query.containsKey("honourClass")){
            honourClass = query.get("honourClass").toString();
        }
        query = sysBaseAPI.getQueryMap(query);
        try {
            Format format = new SimpleDateFormat("yyMMdd");
            String time = format.format(new Date());
            String fileName = "荣誉总表"+"-" + time + ".xlsx";
            String ctxPath = uploadpath+ File.separator+biz + File.separator + time +File.separator+ fileName;
            for (int i = 0; i < fieldsNames.size(); i++) {
                String fieldName = fieldsNames.get(i).toString();

                if(StringUtils.isNotBlank(fieldName)){
                    List<String> cells = null;
                    Map<String, Object> fields = new HashMap<>();
                    int hc = i+1;
                    if(StringUtils.isNotBlank(honourClass) && !honourClass.equals(hc+"")){
                        continue;
                    }
                    switch (i){
                        case 0 :
                            List<HonourFile> pageList = fileService.queryList(query);
                            List<FileVo> fileVos = fileService.transVo(pageList);
                            cells = Arrays.asList(fieldName.split(","));

                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<FileVo> util = new ExcelUtil<>(FileVo.class);
                            util.exportExcel(fileVos, fields,"文件类荣誉",ctxPath);

                            break;
                        case 1 :
                            List<HonourCertificate> cerList = certificateService.queryList(query);

                            List<CerVo> cerVos = certificateService.transVo(cerList);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<CerVo>  eu1 = new ExcelUtil<>(CerVo.class);

                            eu1.exportExcelBySheets(cerVos, fields,"证书类荣誉",ctxPath,ctxPath);

                            break;
                        case 2 :
                            List<HonourArticle> articles = articleService.queryList(query);

                            List<ArticleVo> articleVos = articleService.transVo(articles);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<ArticleVo>  eu2 = new ExcelUtil<>(ArticleVo.class);

                            eu2.exportExcelBySheets(articleVos, fields,"物图类荣誉",ctxPath,ctxPath);
                            break;
                        case 3 :
                            List<HonourReport> reports = reportService.queryList(query);

                            List<ReportVo> reportVos = reportService.transVo(reports);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<ReportVo>  eu3 = new ExcelUtil<>(ReportVo.class);

                            eu3.exportExcelBySheets(reportVos, fields,"报道类荣誉",ctxPath,ctxPath);
                            break;
                        case 4 :
                            List<HonourAgree> agrees = agreeService.queryList(query);

                            List<AgreeVo> agreeVos = agreeService.transVo(agrees);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<AgreeVo>  eu4 = new ExcelUtil<>(AgreeVo.class);

                            eu4.exportExcelBySheets(agreeVos, fields,"协议类荣誉",ctxPath,ctxPath);
                            break;
                        case 5 :
                            List<HonourPerson> people = personService.queryList(query);

                            List<PersonVo> personVos = personService.transVo(people);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<PersonVo>  eu5 = new ExcelUtil<>(PersonVo.class);

                            eu5.exportExcelBySheets(personVos, fields,"人才类荣誉",ctxPath,ctxPath);
                            break;
                        case 6 :
                            List<HonourProject> projects = projectService.queryList(query);

                            List<ProjectVo> projectVos = projectService.transVo(projects);

                            cells = Arrays.asList(fieldName.split(","));

                            fields = new HashMap<>();
                            for (int k = 0; k < cells.size(); k++) {
                                fields.put(cells.get(k),null);
                            }
                            ExcelUtil<ProjectVo>  eu6 = new ExcelUtil<>(ProjectVo.class);

                            eu6.exportExcelBySheets(projectVos, fields,"课题类荣誉",ctxPath,ctxPath);
                            break;
                        default :

                    }

                }

            }


            String savePath = biz + File.separator + time + File.separator  + fileName;
            if (savePath.contains("\\")) {
                savePath = savePath.replace("\\", "/");
            }
            result.success("导出EXCEL成功！");
            result.setResult(savePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("导出EXCEL失败");
        }
        return result;
    }


    /**
     * 总类
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryHonourAllList", method = RequestMethod.GET)
    public Result<IPage<HonourEntityVo>> queryHonourAllList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<HonourEntityVo>> result = new Result<>();
        IPage<HonourEntityVo> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);

        query.put("status",2);

        List<HonourEntityVo> pageList = honourService.findHonourAllList(query);
        long count = honourService.countHonourAllList(query);

        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     * 模糊查询
     * @param req
     * @return
     */
    @RequestMapping(value = "/queryValueByTableAndField", method = RequestMethod.GET)
    public Result<Set<String>> queryValueByTableAndField(@RequestParam(name="fields") String fields,
                                                          @RequestParam(name="tableId") int tableId,
                                                          @RequestParam(name="value") String value, HttpServletRequest req) {
        Result<Set<String>> result = new Result<>();

        Map<String,Object> query = new HashMap<>();
        String honourTable = HonourTable.getName(tableId);

        query.put("fields",fields);
        query.put("value",value);
        query.put("table",honourTable);

        List<String> list = honourService.findValuesByHonour(query);

        if("team_persons".equals(fields)){
            List<String> names = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                List<String> n = Arrays.asList(list.get(i).split(","));
                names.addAll(n);
            }
            list = names;
        }

        Set<String> data = new HashSet<>(list);
        result.setResult(data);
        result.setSuccess(true);
        return result;
    }

    /**
     *
     * 提交前判断重复项
     *
     * */
    @RequestMapping(value = "/findDuplicateItemByTitle", method = RequestMethod.GET)
    public Result<Boolean> findDuplicateItemByTitle(@RequestParam(name="fields") String fields,
                                          @RequestParam(name="tableId") int tableId,
                                          @Param("id")String id, @Param("title")String title) {
        Result<Boolean> result = new Result();

        String honourTable = HonourTable.getName(tableId);
        Map<String,Object> query = new HashMap<>();
        query.put("fields",fields);
        query.put("title",title);
        query.put("table",honourTable);
        Boolean flag = honourService.findDuplicateItemByTitle(id,query);
        result.setResult(flag);
        return result;
    }
}
