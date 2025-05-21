package com.gargujjwal.military_asset_management.advice;

import com.gargujjwal.military_asset_management.dto.ErrorResponse;
import com.gargujjwal.military_asset_management.dto.SuccessResponse;
import java.time.LocalDateTime;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    // Don’t wrap if already a SuccessResponse or ErrorResponse
    if (body instanceof SuccessResponse<?>
        || body instanceof ErrorResponse
        || body instanceof ProblemDetail
        || body instanceof RequestEntity<?>) {
      return body;
    }

    // Skip wrapping for swagger docs, actuator, etc.
    String path = request.getURI().getPath();
    if (path.startsWith("/swagger")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/actuator")) {
      return body;
    }

    // Auto-wrap in SuccessResponse
    return new SuccessResponse<>(body, LocalDateTime.now());
  }

  @Override
  public boolean supports(
      MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }
}
