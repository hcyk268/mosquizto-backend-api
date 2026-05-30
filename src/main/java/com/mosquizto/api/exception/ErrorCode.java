package com.mosquizto.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 - Bad Request (syntactic / payload validation)
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    INVALID_PAYLOAD(HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST(HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST),
    MISSING_HEADER(HttpStatus.BAD_REQUEST),

    // 401 - Unauthorized (authentication)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED),

    // 403 - Forbidden (authorization)
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN),
    JOIN_REQUEST_DENIED(HttpStatus.FORBIDDEN),

    // 404 - Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),

    // 405 - Method Not Allowed
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),

    // 409 - Conflict (duplicate resource / conflicting concurrent state)
    CONFLICT(HttpStatus.CONFLICT),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT),
    ALREADY_JOINED(HttpStatus.CONFLICT),
    JOIN_REQUEST_PENDING(HttpStatus.CONFLICT),
    COLLECTION_ALREADY_IN_FOLDER(HttpStatus.CONFLICT),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT),
    IDEMPOTENCY_IN_PROGRESS(HttpStatus.CONFLICT),
    IDEMPOTENCY_KEY_REUSED(HttpStatus.CONFLICT),

    // 422 - Unprocessable Entity (well-formed request that violates a business rule)
    BUSINESS_RULE_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY),
    SESSION_ALREADY_COMPLETED(HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_SESSION_ITEM(HttpStatus.UNPROCESSABLE_ENTITY),
    JOIN_REQUEST_NOT_PENDING(HttpStatus.UNPROCESSABLE_ENTITY),
    CANNOT_REMOVE_TEACHER(HttpStatus.UNPROCESSABLE_ENTITY),
    MEMBER_NOT_ACTIVE(HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_VERIFICATION_CODE(HttpStatus.UNPROCESSABLE_ENTITY),
    PASSWORD_MISMATCH(HttpStatus.UNPROCESSABLE_ENTITY),

    // 429 - Too Many Requests
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS),

    // 500 - Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
