package com.example.backend.controller.file;

import com.example.backend.tool.UploadFileTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String fileName = UploadFileTool.Upload(file);
        return fileName;
    }
}
