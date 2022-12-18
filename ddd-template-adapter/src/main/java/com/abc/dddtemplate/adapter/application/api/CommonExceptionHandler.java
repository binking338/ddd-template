package com.abc.dddtemplate.adapter.application.api;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.share.CodeEnum;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.share.exception.KnownException;
import com.abc.dddtemplate.share.exception.WarnException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.event.Level;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;

/**
 * 公共的全局异常处理器
 *
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class CommonExceptionHandler {

    public String getRequestInfo(HttpServletRequest request) {
        RequestMsg requestMsg = new RequestMsg();
        Map<String, String[]> param = request.getParameterMap();
        String url = request.getRequestURI();
        requestMsg.setParams(param);
        requestMsg.setUrl(url);
        return JSON.toJSONString(requestMsg);
    }

    @Data
    @JsonPropertyOrder({"url", "params"})
    public static class RequestMsg {
        private String url;
        private Map<String, String[]> params;
    }

    /**
     * 参数校验异常
     *
     * @param e exception
     * @return ResponseData
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(HttpServletRequest request, ConstraintViolationException e) {
        String requestInfo = getRequestInfo(request);
        log.warn(String.format("参数校验异常:%s request:%s ", e.getMessage(), requestInfo), e);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String message = constraintViolation.getMessage();
            return ResponseData.fail(message);
        }
        return ResponseData.fail(e.getLocalizedMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(MissingServletRequestParameterException e) {
        log.warn(String.format("缺少请求参数:%s", e.getMessage()), e);
        String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
        return ResponseData.fail(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(MethodArgumentTypeMismatchException e) {
        log.warn(String.format("请求参数格式错误:%s", e.getMessage()), e);
        String message = String.format("请求参数格式错误: %s", e.getName());
        return ResponseData.fail(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(MethodArgumentNotValidException e) {
        log.warn(String.format("参数验证失败:%s", e.getMessage()), e);
        return ResponseData.fail(CodeEnum.PARAM_INVALIDATE.getCode(), "参数不正确");
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(BindException e) {
        log.warn(String.format("参数绑定失败:%s", e.getMessage()), e);
        return ResponseData.fail(CodeEnum.PARAM_INVALIDATE.getCode(), "参数不正确");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseData<Object> handleError(NoHandlerFoundException e) {
        log.warn(String.format("404没找到请求:%s", e.getMessage()), e);
        return ResponseData.fail(CodeEnum.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> handleError(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error(String.format("消息不能读取:%s request:%s", e.getMessage(), getRequestInfo(request)), e);
        return ResponseData.fail(CodeEnum.MESSAGE_NOT_READ);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseData<Object> handleError(HttpRequestMethodNotSupportedException e) {
        log.warn(String.format("不支持当前请求方法:%s", e.getMessage()), e);
        return ResponseData.fail(CodeEnum.METHOD_NOT_SUPPORTED);
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Object> headerParamException(HttpServletRequest request, MissingRequestHeaderException e) {
        log.warn(String.format("缺少header参数:%s request:%s",  e.getHeaderName(), getRequestInfo(request)), e);
        return ResponseData.fail("缺少header参数");
    }

    @ExceptionHandler(value = ErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData<Object> errorException(ErrorException be) {
        log.error("发生业务错误: ", be);
        return ResponseData.fail(be);
    }

    @ExceptionHandler(value = WarnException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Object> warnException(WarnException be) {
        log.warn("发生业务警告: ", be);
        return ResponseData.fail(be);
    }

    @ExceptionHandler(value = KnownException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Object> knownException(KnownException be) {
        if (Level.ERROR.toString().equalsIgnoreCase(be.getLevel())) {
            log.error("发生业务错误: ", be);
        } else if (Level.WARN.toString().equalsIgnoreCase(be.getLevel())) {
            log.warn("发生业务警告: ", be);
        } else if (log.isDebugEnabled()) {
            log.debug("业务失败返回: ", be);
        }
        return ResponseData.fail(be);
    }

    @ExceptionHandler(value = ClientAbortException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData<Object> clientAbortException(ClientAbortException ce) {
        log.warn("客户端中断异常: ", ce);
        return ResponseData.fail("断开的连接:Broken pipe");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData<Object> handleError(HttpServletRequest request, Throwable e) {
        log.error(String.format("发生未知异常:%s request:%s", e.getMessage(), getRequestInfo(request)), e);
        return ResponseData.fail(CodeEnum.ERROR);
    }
}