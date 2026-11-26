package com.example.ec.common;

import java.util.Arrays;

/**
 * 業務エラーを表す例外。
 * メッセージIDと埋め込み引数を持ち、ApiExceptionHandlerが種別に応じたHTTPステータスとメッセージ本文へ変換する。
 */
public class BusinessException extends RuntimeException {

    /** 業務エラーの種別。 */
    public enum Kind {
        VALIDATION,
        NOT_FOUND,
        CONFLICT
    }

    private final String messageId;
    private final Object[] args;
    private final Kind kind;

    /** 入力チェックエラー（種別VALIDATION）を作る。 */
    public BusinessException(String messageId, Object... args) {
        this(Kind.VALIDATION, messageId, args);
    }

    private BusinessException(Kind kind, String messageId, Object[] args) {
        super(messageId + " " + Arrays.toString(args));
        this.kind = kind;
        this.messageId = messageId;
        this.args = args == null ? new Object[0] : args.clone();
    }

    /** 対象が存在しないエラー（種別NOT_FOUND）を作る。 */
    public static BusinessException notFound(String messageId, Object... args) {
        return new BusinessException(Kind.NOT_FOUND, messageId, args);
    }

    /** 状態が競合しているエラー（種別CONFLICT）を作る。 */
    public static BusinessException conflict(String messageId, Object... args) {
        return new BusinessException(Kind.CONFLICT, messageId, args);
    }

    public String getMessageId() {
        return messageId;
    }

    public Object[] getArgs() {
        return args.clone();
    }

    public Kind getKind() {
        return kind;
    }
}
