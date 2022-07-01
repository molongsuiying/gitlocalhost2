package org.jeecg.common.util;

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import lombok.SneakyThrows;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
public class AsposeUtil {

    /**
     * 加载license 用于破解 不生成水印
     *
     * @author LCheng
     * @date 2020/12/25 13:51
     */
    @SneakyThrows
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = AsposeUtil.class.getClassLoader().getResourceAsStream("License.xml");
            License license = new License();
            license.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * word转pdf
     *
     * @param inPath word文件保存的路径
     * @param outPath  转换后pdf文件保存的路径
     * @author LCheng
     * @date 2020/12/25 13:51
     */
    @SneakyThrows
    public static boolean doc2pdf(String inPath, String outPath) {
        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return false;
        }
        FileOutputStream os = null;

        File file = new File(outPath); // 新建一个空白pdf文档
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        os = new FileOutputStream(file);
        Document doc = new Document(inPath); // Address是将要被转化的word文档
        doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void main(String[] args) {
        doc2pdf("s:\\教育教学成果管理系统(讨论稿).docx","S:\\TEST.pdf");
    }
}
