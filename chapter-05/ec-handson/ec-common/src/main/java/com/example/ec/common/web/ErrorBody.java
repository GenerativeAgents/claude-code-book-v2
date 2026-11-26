package com.example.ec.common.web;

import java.util.List;

/** エラーレスポンスの本文。 */
public record ErrorBody(String messageId, String message, List<String> args) {
}
