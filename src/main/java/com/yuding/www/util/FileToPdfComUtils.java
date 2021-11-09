package com.yuding.www.util;

import com.aspose.cells.PageSetup;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.words.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件转pdf工具类  在linux 服务器中需要安装字体不然会出现乱码问题
 *
 * @author
 */
@Component
public class FileToPdfComUtils {
    private static final String separator = File.separator;
    private static String sysPath;

    /**
     * 文件存储磁盘路径
     */
    @Value("${pdf.filepath}")
    public void setSysPath(String sysPath) {
    	System.out.println("sysPath:"+sysPath);
        FileToPdfComUtils.sysPath = sysPath;
    }

    /**
     * 文件转换
     *
     * @param source:源文件相对路径
     */
    public static void officeToPdf(String source) {
        Map<String, String> map = filepathToPdfPath(source);
        source = map.get("filepath");
        String target = map.get("pdfPath");
        //读取源文件
        File file = new File(source);
        File newFile = new File(target);
        try {
            File fileParent = newFile.getParentFile();
            if (fileParent != null && !fileParent.exists()) {
                fileParent.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            //取文件后缀
            String fileExt = source.substring(source.lastIndexOf(".") + 1);
            //根据后缀判断文件类型 进行不同文件的转换
            if ("doc".equals(fileExt) || "docx".equals(fileExt) || "wps".equals(fileExt) || "wpt".equals(fileExt)) {
                wordToPdf(source, target);
            }
            if ("xls".equals(fileExt) || "xlsx".equals(fileExt) || "et".equals(fileExt) || "xlsm".equals(fileExt)) {
                excelToPdf(source, target);
            }
            if ("ppt".equals(fileExt) || "pptx".equals(fileExt)) {
                pptToPdf(source, target);
            }
            if ("pdf".equals(fileExt)) {
                try {
                    FileUtils.copyFile(new File(source), new File(target));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /*----------------------------------------验证license-----------------------------------------*/
    /**
     * 验证excel License
     */
    public static boolean getLicense(String type) {
        boolean result = false;
        try {
            //读取license.xml
//            InputStream license = FileToPdfComUtils.class.getClassLoader().getResourceAsStream("license.xml");
            if ("words".equals(type)) {
                com.aspose.words.License aposeLic = new com.aspose.words.License();
//                aposeLic.setLicense(license);
            } else if ("cells".equals(type)) {
                com.aspose.cells.License aposeLic = new com.aspose.cells.License();
//                aposeLic.setLicense(license);
            } else if ("slides".equals(type)) {
                com.aspose.slides.License aposeLic = new com.aspose.slides.License();
//                aposeLic.setLicense(license);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*--------------------------------------不同文件转pdf------------------------------------------*/

    /**
     * excel转pdf
     * @params: source:源文件地址,target:转换后文件路径
     */
    public static void excelToPdf(String source, String target) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense("cells")) {
            return;
        }
        FileOutputStream fileOS = null;
        try {
            Workbook wb = new Workbook(source);
            fileOS = new FileOutputStream(target);
            WorksheetCollection worksheets = wb.getWorksheets();
            for (Object worksheet : worksheets) {
                Worksheet worksheet1 = (Worksheet) worksheet;
                PageSetup pageSetup = worksheet1.getPageSetup();
                pageSetup.setOrientation(2);
                pageSetup.setZoom(100);
                pageSetup.setFitToPagesWide(1);
            }
            wb.save(fileOS, com.aspose.cells.SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOS != null) {
                    fileOS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ppt转pdf
     * @params: source:源文件路径,target:转换后文件路径
     */
    public static void pptToPdf(String source, String target) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense("slides")) {
            return;
        }
        FileOutputStream fileOS = null;
        try {
            Presentation ppt = new Presentation(source);
            fileOS = new FileOutputStream(target);
            ppt.save(fileOS, SaveFormat.Pdf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOS != null) {
                    fileOS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * word转pdf
     * @params: source:源文件地址,target:转换后文件路径
     */
    public static void wordToPdf(String source, String target) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense("words")) {
            return;
        }
        FileOutputStream fileOS = null;
        try {
            Document doc = new Document(source);
            fileOS = new FileOutputStream(target);
            doc.save(fileOS, com.aspose.words.SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOS != null) {
                    fileOS.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将文件路径转为pdf路径
     *
     * @param filePath
     * @return
     */
    public static Map<String, String> filepathToPdfPath(String filePath) {
        Map<String, String> result = new HashMap<>();
        String sysPathTemp = sysPath;
        if (StringUtils.isNotBlank(sysPathTemp) && StringUtils.isNotBlank(filePath)) {
            sysPathTemp = sysPathTemp.replace("\\", separator);
            sysPathTemp = sysPathTemp.replace("/", separator);
            filePath = filePath.replace("\\", separator);
            filePath = filePath.replace("/", separator);
            try {
                //处理系统路径
                String sysPathCopy = "";
                char c = sysPathTemp.charAt(sysPathTemp.length() - 1);
                String mark = String.valueOf(c);
                if (separator.equals(mark)) {
                    sysPathCopy = sysPathTemp.substring(0, sysPathTemp.length() - 1);
                } else {
                    sysPathCopy = sysPathTemp;
                }
                //处理文件路径
                String goalStr = "";
                String[] split = null;
                if (separator.equals("\\")) {
                    split = filePath.split("\\\\");
                } else {
                    split = filePath.split(separator);
                }
                if ("".equals(split[0])) {
                    goalStr = split[1];
                } else {
                    goalStr = split[0];
                    filePath = separator + filePath;
                }

                String replace = filePath;
                int i = replace.lastIndexOf(".");
                String sub = replace.substring(0, i);
                result.put("filepath", sysPathCopy + filePath);
                result.put("pdfPath", sysPathCopy + sub + ".pdf");
            } catch (Exception e) {
                e.printStackTrace();
                result.put("filepath", "");
                result.put("pdfPath", "");
                return result;
            }
        } else {
            result.put("filepath", "");
            result.put("pdfPath", "");
        }
        return result;
    }
}
