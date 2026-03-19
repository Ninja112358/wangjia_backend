package com.ninja.wangjia_backend.manager;

// import com.ninja.wangjia_backend.config.CosClientConfig;  // 已禁用
// import com.qcloud.cos.COSClient;  // 已禁用
// import com.qcloud.cos.model.COSObject;  // 已禁用
// import com.qcloud.cos.model.GetObjectRequest;  // 已禁用
// import com.qcloud.cos.model.PutObjectRequest;  // 已禁用
// import com.qcloud.cos.model.PutObjectResult;  // 已禁用
import org.springframework.stereotype.Component;

// import javax.annotation.Resource;  // 已禁用
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 文件管理器（已改为本地存储）
 * 原 COS 功能已禁用，改用本地文件系统
 */
@Component
public class CosManager {
    // @Resource
    // private CosClientConfig cosClientConfig;  // 已禁用
    // @Resource
    // private COSClient cosClient;  // 已禁用
    
    // 本地存储路径
    private static final String LOCAL_STORAGE_PATH = "./uploads/";

    /**
     * 上传文件到本地（替代原有的 COS 上传）
     *
     * @param key  文件路径
     * @param file 文件
     */
    public void putObject(String key, File file) throws IOException {
        // 创建目标目录
        Path targetDir = Paths.get(LOCAL_STORAGE_PATH + key.substring(0, key.lastIndexOf("/")));
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        
        // 复制文件
        Path targetPath = Paths.get(LOCAL_STORAGE_PATH + key);
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * 从本地获取文件（替代原有的 COS 下载）
     *
     * @param key 文件路径
     * @return 文件对象
     */
    public File getObject(String key) {
        Path filePath = Paths.get(LOCAL_STORAGE_PATH + key);
        if (Files.exists(filePath)) {
            return filePath.toFile();
        }
        return null;
    }
}
