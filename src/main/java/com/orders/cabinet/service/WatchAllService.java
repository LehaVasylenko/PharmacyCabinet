package com.orders.cabinet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orders.cabinet.model.db.ControllerEntityEntry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Aspect
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WatchAllService {

//    @Autowired
//    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    final ObjectMapper mapper;

    String TOPIC = "controller-events";
    String RB = "response_body";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    @Pointcut("execution(* com.orders.cabinet.controller..*(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> data = new HashMap<>();
        data.put("user", request.getRemoteUser());
        data.put("time", LocalDateTime.now().format(formatter));
        data.put("method", request.getMethod());
        data.put("path", request.getRequestURI());

        for (Object arg : joinPoint.getArgs()) {
            if (arg.getClass().isAnnotationPresent(RequestBody.class)) {
                data.put("request_body", arg);
                break;
            }
        }
        request.setAttribute("requestData", data);
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.getAttribute("requestData");
        if (data != null) {
            if (result instanceof CompletableFuture<?>) {
                CompletableFuture<?> future = (CompletableFuture<?>) result;
                future.whenComplete((resolvedResult, throwable) -> {
                    if (throwable != null) {
                        data.put(RB, throwable.getMessage());
                    } else {
                        data.put(RB, resolvedResult);
                    }
                    log.warn(data.toString());
                });
            } else {
                data.put(RB, result);
            }
            log.warn(data.toString());
        }

    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) request.getAttribute("requestData");
        if (data != null) {
            data.put(RB, ex.getMessage());
//            kafkaTemplate.send(TOPIC, data);
            log.warn(data.toString());
        }
    }


}

