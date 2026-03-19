package com.ninja.wangjia_backend.controller;

import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.constant.UserConstant;
import com.ninja.wangjia_backend.exception.BusinessException;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;  // 已改为本地存储
    
    String CosFilePath = "test";  // 保留字段，保持兼容性
    
    /**
     * 文件上传（已改为本地存储）
     *
     * @param multipartFile 文件
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file")MultipartFile multipartFile){
        String filename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s", CosFilePath, filename);
        
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(filename, null);
            multipartFile.transferTo(file);
            
            // 使用 CosManager 保存到本地
            cosManager.putObject(filepath, file);
            
            // 返回文件路径
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            log.error("file upload error, filepath: " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传文件失败");
        } finally {
            if (file != null && file.exists()) {
                // 删除临时文件
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("temp file delete warning, filepath: " + filepath);
                }
            }
        }
    }
    
    /**
     * 文件下载（已改为本地下载）
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @GetMapping("/download/")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void downloadFile(String filepath, HttpServletResponse response) throws IOException {
        try {
            // 使用 CosManager 获取本地文件
            File file = cosManager.getObject(filepath);
            
            if (file == null || !file.exists()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文件不存在");
            }
            
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            String fileName = filepath.substring(filepath.lastIndexOf("/") + 1);
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            
            // 写入响应
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }
    }

}
