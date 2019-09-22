package com.leyou.upload.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.OSSConfig;
import com.leyou.upload.config.OSSProperties;
import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @创建人 cwj
 * @创建时间 2019/8/30  22:17
 * @描述
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg", "image/bmp");

    /**
     * 本地上传文件
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        //判断类型
        //判断文件后缀是否是指定格式
        if(!suffixes.contains(file.getContentType())){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //判断文件内容 是否是图片
        BufferedImage image = null;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        if(image == null){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        //更改文件名字
        String fildName = UUID.randomUUID().toString() + file.getOriginalFilename();
        //创建文件夹
        File dir = new File("E:\\developtools\\nginx-1.14.0\\html");
        //上传操作
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir,fildName));
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }
        return "http://image.leyou.com/"+fildName;
    }

    @Autowired
    private OSSProperties prop;
    @Autowired
    private OSS client;

    @Override
    public Map<String, Object> uploadOSS() {
        try {
            long expireTime = prop.getExpireTime();
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir());

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<>();
            respMap.put("accessId", prop.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost());
            respMap.put("expire", expireEndTime);
            return respMap;
        }catch (Exception e){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
