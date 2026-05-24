package com.group19.dto;

/**
 * Generic service-layer operation result wrapper (DTO).
 * <p>
 * A parameterised data transfer object used to uniformly encapsulate the outcome
 * of service-layer method calls. It holds a success/failure flag, a result
 * message, and a generic data payload. Static factory methods
 * {@link #success(Object, String)} and {@link #failure(String)} simplify
 * construction of successful and failed results. All fields are immutable
 * ({@code final}).
 * </p>
 *
 * @param <T> type of the carried data payload
 * @author Group 19
 */
public class ServiceResult<T> {

    /** Whether the operation was successful */
    private final boolean success;

    /** Descriptive message of the operation result */
    private final String message;

    /** Data payload (non-null on success, {@code null} on failure) */
    private final T data;

    /**
     * Private constructor; instances are created via static factory methods.
     *
     * @param success whether the operation succeeded
     * @param message result message
     * @param data    data payload
     */
    private ServiceResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a successful result.
     *
     * @param data    data payload
     * @param message success message
     * @param <T>     data type
     * @return a successful ServiceResult instance
     */
    public static <T> ServiceResult<T> success(T data, String message) {
        return new ServiceResult<>(true, message, data);
    }

    /**
     * Creates a failed result.
     *
     * @param message failure message
     * @param <T>     data type
     * @return a failed ServiceResult instance (data is {@code null})
     */
    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<>(false, message, null);
    }

    /**
     * Returns whether the operation was successful.
     *
     * @return {@code true} if the operation succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the result message.
     *
     * @return result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the data payload.
     *
     * @return data payload (may be {@code null} on failure)
     */
    public T getData() {
        return data;
    }
}
