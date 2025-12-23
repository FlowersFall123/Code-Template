package com.example.backend.tool;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.example.backend.common.FileUrl;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UploadFileTool {

    // 视频存储路径
    private static final String filePath= FileUrl.FILEPATH;//System.getProperty("user.dir") + File.separator + "files" + File.separator;
    private static final String imagePath= FileUrl.IMAGEPATH;//System.getProperty("user.dir") + File.separator + "images" + File.separator;
    private static final String videoPath= FileUrl.VIDEOPATH;//System.getProperty("user.dir") + File.separator + "videos" + File.separator;
    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    // 定义不同类型的白名单
    private static final List<String> VIDEO_TYPES = Arrays.asList(".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv");
    private static final List<String> IMAGE_TYPES = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");
    private static final List<String> FILE_TYPES = Arrays.asList(".pdf", ".doc", ".docx", ".txt", ".zip", ".rar");


    /**
     * 使用 transferTo+雪花算法 的方式保存视频
     * @param file MultipartFile
     * @return 最终生成的文件名
     */
    public static String Upload(MultipartFile file) throws IOException {
        Map<String, String> map=getFileMeta(file);
        String suffix = map.get("suffix");
        String category = map.get("category");
        String path = Objects.equals(category, "video") ?videoPath: Objects.equals(category, "image") ?imagePath:filePath;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成文件名：使用雪花 ID 更短且有序
        String fileName = snowflake.nextIdStr() + suffix;
        File finalFile = new File(dir, fileName);

        try {
            file.transferTo(finalFile);
        } catch (Exception e) {
            throw new RuntimeException("视频存储失败：" + e.getMessage(), e);
        }

        System.out.println("文件是否存在：" + finalFile.exists());
        System.out.println("文件大小：" + finalFile.length());
        System.out.println("写入路径：" + finalFile.getAbsolutePath());

        return fileName;
    }


    /**
     * 获取文件后缀名及其类别并校验格式
     * @param file
     * @return
     */
    public static Map<String, String> getFileMeta(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空！");
        }

        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        String category;

        if (VIDEO_TYPES.contains(suffix)) {
            category = "video";
        } else if (IMAGE_TYPES.contains(suffix)) {
            category = "image";
        } else if (FILE_TYPES.contains(suffix)) {
            category = "file";
        } else {
            throw new RuntimeException("不支持格式：" + suffix);
        }

        // 使用 Map 封装结果
        Map<String, String> meta = new HashMap<>();
        meta.put("suffix", suffix);
        meta.put("category", category);
        return meta;
    }

    /**
     * 播放视频
     */
    public static ResponseEntity<Resource> PlayVideo(String fileName) {

        File file = new File(videoPath , fileName);
        if (!file.exists()) {
            return null;
        }
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "video/mp4");

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * 删除视频
     */
    public static Long DeleteVideo(String fileName) {

        // 构造完整文件路径
        File file = new File(videoPath , fileName);

        // 不存在直接返回404
        if (!file.exists()) {
            return 0L;//删除失败
        }

        // 尝试删除
        boolean deleted = file.delete();

        if (deleted) {
            return 1L;//删除成功
        } else {
            return 0L;//删除失败
        }
    }
}