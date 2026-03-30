package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.LocalUploadProperties;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private LocalUploadProperties localUploadProperties;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        try {
            // 1. 判断文件是否为空
            if (file == null || file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }

            // 2. 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            log.info("上传文件名：{}", originalFilename);

            // 3. 获取后缀名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 4. 生成新的文件名，避免重复
            String fileName = UUID.randomUUID().toString() + extension;

            // 5. 创建目标文件
            File dir = new File(localUploadProperties.getLocalUploadPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(localUploadProperties.getLocalUploadPath() + fileName);

            // 6. 保存文件到本地
            file.transferTo(dest);

            // 7. 返回访问路径
            String url = "http://localhost:8080/upload/" + fileName;

            return Result.success(url);

        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage(), e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}