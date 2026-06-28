package com.piano.management.controller;

import com.piano.management.common.Result;
import com.piano.management.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadControllerTest {

    private FileUploadController controller;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        controller = new FileUploadController();
        ReflectionTestUtils.setField(controller, "uploadPath", tempDir.toString());
    }

    @Test
    void uploadWithoutSuffixShouldReturnBusinessException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar", "text/plain", "hello".getBytes());

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.upload(file));
        assertEquals(500, ex.getCode());
    }

    @Test
    void uploadJpgShouldSucceedAndWriteFile() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "binary-content".getBytes());

        Result<String> result = controller.upload(file);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().startsWith("/uploads/"));
        try (Stream<Path> files = Files.list(tempDir)) {
            assertTrue(files.findAny().isPresent());
        }
    }
}
