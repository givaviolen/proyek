package org.delcom.app.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// PENTING: Ganti @SpringBootTest dengan @ExtendWith(MockitoExtension.class)
// Ini adalah kunci agar tidak perlu load ApplicationContext/Database
@ExtendWith(MockitoExtension.class)
class CustomErrorControllerTest {

    @Mock
    private ErrorAttributes errorAttributes;

    // InjectMocks akan otomatis membuat instance CustomErrorController 
    // dan memasukkan mock ErrorAttributes ke dalamnya
    @InjectMocks
    private CustomErrorController controller;

    @Test
    @DisplayName("Mengembalikan response error dengan status 500 (Default Case)")
    void testHandleErrorReturns500() {
        // 1. Siapkan Mock Data (Map kosong simulasi error tak dikenal)
        Map<String, Object> errorMap = Map.of();

        // 2. Mock behavior ErrorAttributes
        when(errorAttributes.getErrorAttributes(
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        // 3. Siapkan Dummy Request
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        // 4. Eksekusi Method
        // Pastikan method di controller Anda menerima parameter yang sesuai (ServletWebRequest atau HttpServletRequest)
        // Jika method aslinya menerima HttpServletRequest, ubah pemanggilan ini sesuai kode asli Anda.
        ResponseEntity<Map<String, Object>> result = controller.handleError(webRequest);

        // 5. Assertions
        assertEquals(500, result.getStatusCode().value());
        assertEquals("error", result.getBody().get("status"));
        
        // Sesuaikan ekspektasi ini dengan logika "default" di controller Anda
        // Jika Map kosong, biasanya controller akan mengisi nilai default
        assertEquals("Unknown Error", result.getBody().get("error"));
        assertEquals("unknown", result.getBody().get("path"));
    }

    @Test
    @DisplayName("Mengembalikan response error dengan status 404")
    void testHandleErrorReturns404() {
        // 1. Siapkan Mock Data untuk 404
        Map<String, Object> errorMap = Map.of(
                "status", 404,
                "error", "Not Found",
                "path", "/error404"
        );

        // 2. Mock behavior
        when(errorAttributes.getErrorAttributes(
                any(ServletWebRequest.class),
                any(ErrorAttributeOptions.class)))
                .thenReturn(errorMap);

        // 3. Siapkan Dummy Request
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        // 4. Eksekusi Method
        ResponseEntity<Map<String, Object>> result = controller.handleError(webRequest);

        // 5. Assertions
        assertEquals(404, result.getStatusCode().value());
        assertEquals("fail", result.getBody().get("status"));
        assertEquals("Not Found", result.getBody().get("error"));
        assertEquals("/error404", result.getBody().get("path"));
    }
}