package com.example.ec.common.web;

import com.example.ec.common.BusinessException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 例外をエラーレスポンス（ErrorBody）へ変換する。
 * メッセージ本文はMessageSourceから日本語で取り出す。
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private final MessageSource messageSource;

    public ApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** 業務エラー。種別に応じて400、404、409にする。 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorBody> handleBusinessException(BusinessException e) {
        HttpStatus status = switch (e.getKind()) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
        };
        return error(status, e.getMessageId(), e.getArgs());
    }

    /** 未定義のパス、またはパスに対応しないメソッド。 */
    @ExceptionHandler({
            NoResourceFoundException.class,
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorBody> handleNotFound(Exception e) {
        return error(HttpStatus.NOT_FOUND, "E0002");
    }

    /** 読めない本文、型の合わない引数など、リクエストの形式の誤り。 */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ErrorBody> handleBadRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "E0003");
    }

    /** その他の例外。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleException(Exception e) {
        log.error("Unexpected error", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "E0001");
    }

    private ResponseEntity<ErrorBody> error(HttpStatus status, String messageId, Object... args) {
        String message = messageSource.getMessage(messageId, args, Locale.JAPAN);
        List<String> argTexts = Arrays.stream(args).map(String::valueOf).toList();
        return ResponseEntity.status(status).body(new ErrorBody(messageId, message, argTexts));
    }
}
