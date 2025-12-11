package org.delcom.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class ApplicationTest {

    @Test
    @DisplayName("Main method should run SpringApplication")
    void mainMethod_ShouldRunSpringApplication() {
        // Mock SpringApplication.run agar tidak benar-benar mengangkat server saat unit test
        try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
            
            mockedSpring.when(() -> SpringApplication.run(Application.class, new String[] {}))
                    .thenReturn(mockContext);

            // Jalankan main method
            assertDoesNotThrow(() -> Application.main(new String[] {}));

            // Verifikasi bahwa SpringApplication.run benar-benar dipanggil
            mockedSpring.verify(() -> SpringApplication.run(Application.class, new String[] {}));
        }
    }

    @Test
    @DisplayName("Application class should load correctly")
    void contextLoads_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            Class<?> clazz = Class.forName("org.delcom.app.Application");
            assertNotNull(clazz);
        });
    }

    @Test
    @DisplayName("Application class should have @SpringBootApplication annotation")
    void todoApplication_ShouldHaveSpringBootAnnotation() {
        assertNotNull(Application.class
                .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    @DisplayName("Application instance can be created")
    void todoApplication_CanBeInstantiated() {
        assertDoesNotThrow(() -> {
            Application app = new Application();
            assertNotNull(app);
        });
    }
}