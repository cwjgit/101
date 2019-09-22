package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @创建人 cwj
 * @创建时间 2019/8/27  22:41
 * @描述
 */
@ControllerAdvice
@Slf4j
public class BasicExceptionAdvice {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e){
        ExceptionResult exceptionResult = new ExceptionResult(e);
        return ResponseEntity.status(e.getStatus()).body(exceptionResult);
    }
}
