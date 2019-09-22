package com.leyou.common.vo;

import com.leyou.common.exception.LyException;
import lombok.Data;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @创建人 cwj
 * @创建时间 2019/8/28  10:04
 * @描述
 */
@Data
public class ExceptionResult {

    private int status;
    private String message;
    private String timestamp;

    public ExceptionResult(LyException e){
        this.status = e.getStatus();
        this.message = e.getMessage();
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }
}
