package com.leyou;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

/**
 * @创建人 cwj
 * @创建时间 2019/9/13  16:05
 * @描述
 */
public class AuthTest {
    private String privateFilePath = "E:\\ssh\\id_rsa";
    private String publicFilePath = "E:\\ssh\\id_rsa.pub";

    @Test
    public void createKey() throws Exception {
        RsaUtils.generateKey(publicFilePath,privateFilePath,"hello",2048);
    }

    @Test
    public void createJwt() throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(123L);
        userInfo.setUsername("张三");
        String jwt = JwtUtils.generateTokenExpireInSeconds(userInfo, RsaUtils.getPrivateKey(privateFilePath), 30);
        System.out.println(jwt);
        Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(jwt, RsaUtils.getPublicKey(publicFilePath), UserInfo.class);
        System.out.println(infoFromToken);
    }
}
