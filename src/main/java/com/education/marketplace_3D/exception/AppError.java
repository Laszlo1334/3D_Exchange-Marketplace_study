package com.education.marketplace_3D.exception;

import java.time.LocalDateTime;

public record AppError(int statusCode, String message, LocalDateTime timestamp) {

}
