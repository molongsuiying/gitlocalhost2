package org.jeecg.modules.college.util;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */


import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jeecg.modules.college.vo.ExcelVo;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel的工具类
 */
public class ExcelUtil<T> {

    private final static String excel2003L =".xls";    //2003- 版本的excel
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel

    /**
     * 工作薄
     */
    private Workbook wb;

    /**
     * 工作表
     */
    private Sheet sheet;

    /**
     * 需要导出的数据
     */
    private List<T> exportList;

    /**
     * 对象的class对象
     */
    private Class<T> clazz;

    /**
     * 被选中需要导出的字段名称
     */
    private Map<String, Object> checkedFieldsName;

    /**
     * 被选中需要导出的字段对象
     */
    private List<Field> checkedFields;

    /**
     * 包含需要字典转换的字段对象
     */
    private List<Field> fieldsContainDict;

    /**
     * 对象中的字典值
     */
    private Map<String, Map<String, String>> dicts;

    private ExcelUtil(){
    }

    public ExcelUtil(Class<T> clazz){
        this.clazz = clazz;
    }


    /**
     * Excel导入
     */
    public static  List<List<Object>> getListByExcel(InputStream in, String fileName) throws Exception{
        List<List<Object>> list = null;
        //创建Excel工作薄
        Workbook work = getWorkbook(in,fileName);
        if(null == work){
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;
        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if(sheet==null){continue;}
            //遍历当前sheet中的所有行
            //包涵头部，所以要小于等于最后一列数,这里也可以在初始值加上头部行数，以便跳过头部
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                //读取一行
                row = sheet.getRow(j);
                //去掉空行和表头
                if(row==null||row.getFirstCellNum()==j){continue;}
                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    if(null == cell){
                        li.add("");
                    }else{
                        li.add(getCellValue(cell));
                    }

                }
                list.add(li);
            }
        }
        return list;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     */
    public static  Workbook getWorkbook(InputStream inStr,String fileName) throws Exception{
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if(excel2003L.equals(fileType)){
            wb = new HSSFWorkbook(inStr);  //2003-
        }else if(excel2007U.equals(fileType)){
            wb = new XSSFWorkbook(inStr);  //2007+
        }else{
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     */
    public static  Object getCellValue(Cell cell){
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化字符类型的数字
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if("General".equals(cell.getCellStyle().getDataFormatString())){
                    value = df.format(cell.getNumericCellValue());
                }else if("m/d/yy".equals(cell.getCellStyle().getDataFormatString())){
                    value = sdf.format(cell.getDateCellValue());
                }else{
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }


    /**
     *
     * @param list
     * @param sheetName
     * @param fieldsName
     */
    public void exportExcel(List<T> list, Map<String, Object> fieldsName, String sheetName,String fileUrl){
        // 初始化数据
        init(list, sheetName, fieldsName);

        // 转换字典值
        try {
            convertDict();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // sheet第一行加入名称数据
        createTopRow();

        // sheet其他行，添加目标数据
        try {
            createOtherRow();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        String path = fileUrl.substring(0,fileUrl.lastIndexOf("\\")+1);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
        // 导出wb
        try(OutputStream outFile = new FileOutputStream(fileUrl)){
            wb.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param list
     * @param sheetName
     * @param fieldsName
     */
    public void exportExcelBySheets(List<T> list, Map<String, Object> fieldsName, String sheetName,String fileUrl,String filePath){
        // 初始化数据

        try {
            batchInit(list, sheetName, fieldsName,filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 转换字典值
        try {

            convertDict();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // sheet第一行加入名称数据
        createTopRow();

        // sheet其他行，添加目标数据
        try {
            createOtherRow();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // 导出wb
        try(OutputStream outFile = new FileOutputStream(fileUrl)){
            wb.write(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 添加导出数据
     */
    private void createOtherRow() throws IllegalAccessException {
        for (int rowNum = 1; rowNum <= exportList.size(); rowNum++) {
            Row row = sheet.createRow(rowNum);
            T t = exportList.get(rowNum - 1);

            for (int colNum = 0; colNum < checkedFields.size(); colNum++) {
                Cell cell = row.createCell(colNum);
                Field field = checkedFields.get(colNum);
                field.setAccessible(true);

                // 单元格设置值
                addCell(cell, field, t);
            }
        }
    }

    /**
     * 单元格中添加数据
     *
     * @param cell  单元格
     * @param field 字段
     * @param t     list中的一条数据
     */
    private void addCell(Cell cell, Field field, T t) throws IllegalAccessException {
        Class<?> fieldType = field.getType();

        if(field.get(t) == null){
            cell.setCellValue("");
        }else{
            if (String.class == fieldType) {
                cell.setCellValue((String) field.get(t));
            } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                cell.setCellValue((Integer) field.get(t));
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                cell.setCellValue((Long) field.get(t));
            } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                cell.setCellValue((Double) field.get(t));
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                cell.setCellValue((Float) field.get(t));
            } else if (Date.class == fieldType) {
                String dateFormat = field.getAnnotation(Excel.class).dateFormat();
                cell.setCellValue(dateFormat((Date) field.get(t), dateFormat));
            }
        }


    }

    /**
     * 时间格式转换
     * @param date 日期
     * @param dateFormat 日期格式
     * @return
     */
    private String dateFormat(Date date, String dateFormat) {
        if (dateFormat == null || "".equals(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    /**
     * sheet第一行加入名称数据
     */
    private void createTopRow() {
        Row row = sheet.createRow(0);
        Map<String, CellStyle> styles = createStyles(wb);

        for (int index = 0; index < checkedFields.size(); index++) {
            Cell cell = row.createCell(index);
            cell.setCellValue(checkedFields.get(index).getAnnotation(Excel.class).name());
            System.out.println(styles.get("header"));
            cell.setCellStyle(styles.get("header"));
        }
    }

    /**
     * 转换字典值
     *     将数据中字典值转化为对应的值(注:字典值应为String格式)
     */
    private void convertDict() throws IllegalAccessException {
        for (Field field : fieldsContainDict) {
            Excel annotation = field.getAnnotation(Excel.class);
            String dictKey = annotation.dictKey();
            field.setAccessible(true);
            for (T t : exportList) {
                // 获取字段值
                String o = (String) field.get(t);
                field.set(t, dicts.get(dictKey).get(o));
            }
        }
    }

    /**
     * 将数据导出Excel
     *
     * @param list 需要导出的数据
     * @param sheetName 工作表名称
     */
    public void exportExcel(List<T> list, String sheetName,String url){
        exportExcel(list, null, sheetName,url);
    }

    /**
     * 将数据导出Excel
     *
     * @param list 需要导出的数据
     */
    public void exportExcel(List<T> list,String url) {
        exportExcel(list, null, "sheet",url);
    }

    /**
     * 初始化
     */
    public void init(List<T> list ,String sheetName,  Map<String, Object> fieldsName){
        this.checkedFieldsName = fieldsName;

        this.exportList = list;

        // 初始化导出数据
        initExportList();

        // 初始化工作薄
        initWorkbook();

        // 初始化工作表
        initSheet(sheetName);

        // 初始化checkedFields, fieldsContainDict
        initFields();

        // 根据注解生成生成字典
        generateObjDict();
    }

    /**
     * 初始化
     */
    public void batchInit(List<T> list ,String sheetName,  Map<String, Object> fieldsName,String filePath) throws IOException {
        this.checkedFieldsName = fieldsName;

        this.exportList = list;

        // 初始化导出数据
        initExportList();

        initExistWorkbook(filePath);

        // 初始化工作表
        initSheet(sheetName);

        // 初始化checkedFields, fieldsContainDict
        initFields();

        // 根据注解生成生成字典
        generateObjDict();
    }


    /**
     * 初始化导出数据
     */
    private void initExportList(){
        // 防止导出过程中出现空指针
        if(Objects.isNull(this.exportList)) {
            this.exportList = new ArrayList<>();
        }
    }

    /**
     * 初始化工作簿
     */
    private void initWorkbook(){
        this.wb = new SXSSFWorkbook();
    }

    private void initExistWorkbook(String path) throws IOException {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            this.wb = new SXSSFWorkbook();
        }else{
            FileInputStream inputStream2 =  new FileInputStream(file);
            XSSFWorkbook xssfSheets = new XSSFWorkbook(inputStream2);
            this.wb = new SXSSFWorkbook(xssfSheets);
        }

    }

    /**
     * 初始化工作表
     */
    private void initSheet(String sheetName){
        this.sheet = wb.createSheet(sheetName);
    }

    /**
     * 初始化checkedFields, fieldsContainDict
     *     fieldsContainDict含有字典表达式的字段
     *     checkedFields用户选中的字段
     *        1.如果checkedFieldsName没有定义(未自定义导出字段),所有字段全部导出
     *        2.如果checkedFieldsName进行了定义,根据定义字段进行导出
     */
    private void initFields(){
        // 获取对象所有字段对象
        Field[] fields = clazz.getDeclaredFields();

        // 过滤出checkedFields
        this.checkedFields = Arrays.
                asList(fields).
                stream().
                filter(item -> {
                    if(!Objects.isNull(this.checkedFieldsName)) {
                        if (item.isAnnotationPresent(Excel.class)) {
                            return checkedFieldsName.containsKey(item.getName());
                        }
                    } else {
                        return item.isAnnotationPresent(Excel.class);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 过滤出fieldsContainDict
        this.fieldsContainDict = Arrays
                .asList(clazz.getDeclaredFields())
                .stream()
                .filter(item -> item.getAnnotation(Excel.class)!=null  && !"".equals(item.getAnnotation(Excel.class).dictExp()))
                .collect(Collectors.toList());
    }

    /**
     * 通过扫描字段注解生成字典数据
     */
    private void generateObjDict(){
        if(fieldsContainDict.size() == 0) {
            return;
        }

        if(dicts == null) {
            dicts = new HashMap<>(); //  Map<String, List<Map<String, String>>>
        }

        for (Field field : fieldsContainDict) {
            String dictKey = field.getAnnotation(Excel.class).dictKey();
            String exps = field.getAnnotation(Excel.class).dictExp();
            String[] exp = exps.split(",");

            Map<String, String> keyV = new HashMap<>();

            dicts.put(dictKey, keyV);

            for (String s : exp) {
                String[] out = s.split("=");
                keyV.put(out[0], out[1]);
            }

            System.out.println("字典值:"+ dicts);
        }
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb)
    {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        // 数据格式
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        styles.put("data", style);

        // 表头格式
        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    /**
     * 生成随机名称,防止文件复写
     * @return
     */
    private String generateFileName(){
        return "D:\\" + UUID.randomUUID().toString().replace("-", "") + ".xlsx";
    }


    public static void main(String[] args) {
        String fileUrl = "D:\\opt\\upFiles\\excel\\78b9d4b8113c4d0ca4423b737166cbd9.xlsx";
        String path = fileUrl.substring(0,fileUrl.lastIndexOf("\\")+1);
        System.out.println(fileUrl);
        System.out.println(path);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
    }
}