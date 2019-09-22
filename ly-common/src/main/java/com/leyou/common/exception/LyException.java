package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @创建人 cwj
 * @创建时间 2019/8/27  22:34
 * @描述
 */
@Data
public class LyException extends RuntimeException {


    //状态码
    private Integer status;

    public LyException(Integer status,String message ) {
        super(message);
        this.status = status;
    }

    public LyException(Integer status,String message, Throwable cause ) {
        super(message, cause);
        this.status = status;
    }

    public LyException(ExceptionEnum em) {
        super(em.getMessage());
        this.status = em.getStatus();
    }
}
