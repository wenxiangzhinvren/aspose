package com.yuding.www.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import com.yuding.www.util.FileToPdfComUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @author css team
 * @create 2020/11/17 09:34
 * @description 文件预览接口
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String separator = File.separator;

    /**
     * 文件预览
     * 后端根据原始路径再转换返回
     *
     * @param filePath 原始文件路径和名称,不区分正反斜杠 /realdir/xxx/yyy/zzzz.doc
     * @return
     */
    @GetMapping("/filePreview")
    @ResponseBody
    public void filePreview(@RequestParam String filePath, HttpServletResponse response) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(filePath)) {
            log.error("filePreview：filePath为空！");
            return;
        }
        filePath = filePath.replace("\\", separator);
        filePath = filePath.replace("/", separator);
        String[] fileTypeArr = {"doc", "docx", "wps", "wpt", "xls", "xlsx", "et", "ppt", "pptx", "pdf","xlsm"};
        //判断是否可以转换或预览
        int i = filePath.lastIndexOf(".");
        String fileType = filePath.substring(i + 1);
        boolean flag = false;
        for (String type : fileTypeArr) {
            if (type.equals(fileType)) {
                flag = true;
                break;
            }
        }

        if (flag) {
            //获取文件和pdf绝对路径
            Map<String, String> map = FileToPdfComUtils.filepathToPdfPath(filePath);
            String pdfPath = map.get("pdfPath");

            //如果文件不存在则生成pdf
            File file = new File(pdfPath);
            if (!file.exists()) {
            	FileToPdfComUtils.officeToPdf(filePath);
            }
            try {
                FileCopyUtils.copy(new FileInputStream(pdfPath), response.getOutputStream());
            } catch (IOException e) {
                log.error("filePreview："+e);
            }
        }else{
            log.error("filePreview："+fileType+"文件不支持预览！");
        }
    }
}
