//package com.study.collect.common.exception;
//
//import com.study.collect.common.model.Response;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.BindException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//// 2. 创建全局异常处理器
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    // 处理参数验证异常
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Response<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
////        List<String> errors = ex.getBindingResult()
////                .getFieldErrors()
////                .stream()
////                .map(FieldError::getDefaultMessage)
////                .collect(Collectors.toList());
////
////        return Response.error("400", String.join(", ", errors));
////    }
////
////    @ExceptionHandler(MethodArgumentNotValidException.class)
////    public Response<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(fieldError ->
//                        String.format("%s: %s",
//                                fieldError.getField(),
//                                fieldError.getDefaultMessage()))
//                .collect(Collectors.toList());
//
//        return Response.error("400", "参数验证失败", errors);
//    }
//
//    // 处理参数绑定异常
//    @ExceptionHandler(BindException.class)
//    public Response<Void> handleBindException(BindException ex) {
//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        return Response.error("400", String.join(", ", errors));
//    }
//
//
//    // 处理其他异常
//    @ExceptionHandler(Exception.class)
//    public Response<Void> handleAllExceptions(Exception ex) {
//        log.error("系统异常", ex);
//        return Response.error("500", "系统异常");
//    }
//}
