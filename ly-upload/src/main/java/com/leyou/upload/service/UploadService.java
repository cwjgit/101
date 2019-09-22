package com.leyou.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/8/30  22:16
 * @描述
 */
public interface UploadService {
    String upload(MultipartFile file);

    Map<String ,Object> uploadOSS();
}
