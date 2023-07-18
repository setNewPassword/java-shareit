package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionHandlerTest {
    private final ErrorHandler handler = new ErrorHandler();


    @Test
    public void illegalArgumentExceptionTest() {
        IllegalArgumentException e = new IllegalArgumentException("Illegal argument!");
        ErrorResponse errorResponse = handler.handleIllegalArgumentException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void httpMessageNotReadableTest() {
        HttpMessageNotReadableException e = new HttpMessageNotReadableException("Получен некорректный JSON.");
        ErrorResponse errorResponse = handler.handleHttpMessageNotReadable(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void entityNotFoundExceptionTest() {
        EntityNotFoundException e = new UserNotFoundException("Not found!");
        ErrorResponse errorResponse = handler.handleNotFoundException(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), e.getMessage());
    }

    @Test
    public void httpRequestMethodNotSupportedExceptionTest() {
        HttpRequestMethodNotSupportedException e = new HttpRequestMethodNotSupportedException(
                "Отсутствует реализация метода.");
        ErrorResponse errorResponse = handler.handleHttpRequestMethodNotSupportedEx(e);
        assertNotNull(errorResponse);
        assertEquals(errorResponse.getError(), "Отсутствует реализация метода.");
    }

}
