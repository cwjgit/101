package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @创建人 cwj
 * @创建时间 2019/8/30  22:14
 * @描述
 */
@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 本地上传文件
     * @param file
     * @return
     */
    @PostMapping("/image")
    public ResponseEntity<String> upload(MultipartFile file){
        return ResponseEntity.ok(uploadService.upload(file));
    }

    /**
     * 上传图片到阿里云
     * @return
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String ,Object>> uploadOSS(){
        return ResponseEntity.ok(uploadService.uploadOSS());
    }
}
