package com.gargujjwal.military_asset_management.advice;

import com.gargujjwal.military_asset_management.dto.ErrorResponse;
import com.gargujjwal.military_asset_management.exception.ConflictingResourceException;
import com.gargujjwal.military_asset_management.exception.InvalidRequestException;
import com.gargujjwal.military_asset_management.exception.InvalidTokenException;
import com.gargujjwal.military_asset_management.exception.InventoryNotEnoughException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.exception.TokenGenerationException;
import com.gargujjwal.military_asset_management.exception.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j(topic = "API_RESPONSE_ADVICE")
@RestControllerAdvice
public class ErrorResponseAdvice extends ResponseEntityExceptionHandler {

  private static final String GENERIC_ERROR_MESSAGE =
      "An internal error occurred. Please contact support.";

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String supportedmethods =
        Arrays.stream(ex.getSupportedMethods()).reduce((a, b) -> a + ", " + b).orElse("");
    var error =
        new ErrorResponse(
            "Request method not supported",
            List.of(
                ex.getMethod() + " is not supported",
                "Only " + supportedmethods + " are supported"));
    return buildResponseEntity(error, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String supportedMediaTypes =
        ex.getSupportedMediaTypes().stream()
            .reduce(
                new StringBuilder(),
                (sb, mt) -> sb.append(mt.getType()).append(", "),
                StringBuilder::append)
            .toString();

    var error =
        new ErrorResponse(
            "Media type not supported",
            List.of(
                ex.getContentType().getType() + " is not supported",
                "Only " + supportedMediaTypes + " are supported"));
    return buildResponseEntity(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var error = new ErrorResponse("Media type not acceptable", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.NOT_ACCEPTABLE);
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(
      MissingPathVariableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var error =
        new ErrorResponse(
            "Missing path variable", List.of(missingResourceMsg(ex.getVariableName())));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var error =
        new ErrorResponse(
            "Missing request parameter", List.of(missingResourceMsg(ex.getParameterName())));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var error =
        new ErrorResponse(
            "Missing request part", List.of(missingResourceMsg(ex.getRequestPartName())));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(
      ServletRequestBindingException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.warn(ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    List<String> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .toList();
    var error = new ErrorResponse("Request Validation Failed", validationErrors);
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    List<String> validationErrors =
        ex.getParameterValidationResults().stream()
            .map(
                res ->
                    res.getMethodParameter().getParameterName()
                        + ": "
                        + res.getResolvableErrors().stream()
                            .map(err -> err.getDefaultMessage())
                            .reduce(
                                new StringBuilder(),
                                (sb, msg) -> sb.append(msg).append(", "),
                                StringBuilder::append)
                            .toString())
            .toList();
    var error = new ErrorResponse("Request Validation Failed", validationErrors);
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var error = new ErrorResponse("Invalid url or endpoint");
    return buildResponseEntity(error, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleNoResourceFoundException(
      NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var error = new ErrorResponse("No resource found for request");
    return buildResponseEntity(error, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
      AsyncRequestTimeoutException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("Async request timeout: {}", ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Override
  protected ResponseEntity<Object> handleErrorResponseException(
      ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error("Error response exception: {}", ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var error =
        new ErrorResponse(
            "Max upload size exceeded",
            List.of(ex.getMaxUploadSize() + "bytes is the maximum upload size"));
    return buildResponseEntity(error, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(
      ConversionNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("Conversion not supported: {}", ex.getMessage());
    var error =
        new ErrorResponse(
            "Conversion not supported",
            List.of(
                ex.getPropertyName()
                    + " was provided with a value that cannot be converted to "
                    + ex.getRequiredType().getTypeName()));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var error =
        new ErrorResponse(
            "Bad Request",
            List.of(
                ex.getPropertyName()
                    + " was provided with a value that could not be converted to "
                    + ex.getRequiredType().getTypeName()));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("HTTP message not readable: {}", ex.getMessage());
    var error = new ErrorResponse("HTTP message not readable");
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(
      HttpMessageNotWritableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.error("HTTP message not writable: {}", ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodValidationException(
      MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    List<String> validationErrors =
        ex.getParameterValidationResults().stream()
            .map(
                res ->
                    res.getMethodParameter().getParameterName()
                        + ": "
                        + res.getResolvableErrors().stream()
                            .map(err -> err.getDefaultMessage())
                            .reduce(
                                new StringBuilder(),
                                (sb, msg) -> sb.append(msg).append(", "),
                                StringBuilder::append)
                            .toString())
            .toList();
    var error = new ErrorResponse("Method validation error", validationErrors);
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {
    log.error("Internal server error: {}", ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.valueOf(statusCode.value()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  protected ResponseEntity<Object> handleResourceNotFoundException(
      ResourceNotFoundException ex, WebRequest request) {
    var error = new ErrorResponse("Resource not found", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidTokenException.class)
  protected ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException ex) {
    var error = new ErrorResponse("Token provided is invalid");
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenGenerationException.class)
  protected ResponseEntity<Object> handleGenericException(TokenGenerationException ex) {
    log.error("Couldn't generate token: {}", ex.getMessage());
    var error = new ErrorResponse(GENERIC_ERROR_MESSAGE);
    return buildResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ConflictingResourceException.class)
  protected ResponseEntity<Object> handleConflictingResourceException(
      ConflictingResourceException ex) {
    var error = new ErrorResponse("Resource already exists", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UnauthorizedException.class)
  protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
    var error = new ErrorResponse("Unauthorized", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InvalidRequestException.class)
  protected ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
    var error = new ErrorResponse("Bad Request", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException ex) {
    var error =
        new ErrorResponse(
            "Constraint violation",
            ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList());
    return buildResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InventoryNotEnoughException.class)
  protected ResponseEntity<Object> handleInventoryNotEnoughException(
      InventoryNotEnoughException ex) {
    var error = new ErrorResponse("Error while executing transaction", List.of(ex.getMessage()));
    return buildResponseEntity(error, HttpStatus.FORBIDDEN);
  }

  private ResponseEntity<Object> buildResponseEntity(ErrorResponse error, HttpStatus status) {
    return new ResponseEntity<>(error, status);
  }

  private static String missingResourceMsg(String resource) {
    return resource + " was needed but not provided";
  }
}
