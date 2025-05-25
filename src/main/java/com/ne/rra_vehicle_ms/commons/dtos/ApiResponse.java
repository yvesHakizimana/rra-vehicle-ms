package com.ne.rra_vehicle_ms.commons.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public ApiResponse(boolean success, String message, T data){
        this(success, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String  message, T data){
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data){
        return new ApiResponse<>(false, message, data);
    }
}
