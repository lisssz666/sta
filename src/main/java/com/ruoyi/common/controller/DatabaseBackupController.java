package com.ruoyi.common.controller;

import com.ruoyi.common.task.DatabaseBackupTask;
import com.ruoyi.framework.web.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库备份控制器
 */
@RestController
@RequestMapping("/backup")
public class DatabaseBackupController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupController.class);

    @Autowired
    private DatabaseBackupTask backupTask;

    /**
     * 手动触发数据库备份
     */
    @PostMapping("/execute")
    public AjaxResult executeBackup() {
        try {
            backupTask.manualBackup();
            logger.info("手动触发数据库备份");
            return AjaxResult.success("备份任务已提交，将在后台执行");
        } catch (Exception e) {
            logger.error("手动触发备份失败", e);
            return AjaxResult.error("备份任务提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取备份文件列表
     */
    @RequestMapping("/list")
    public AjaxResult listBackups() {
        try {
            // 从配置或默认路径获取备份目录
            String backupPath = System.getProperty("backup.path", "./backup");
            File backupDir = new File(backupPath);
            
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                return AjaxResult.success(new ArrayList<>());
            }

            File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
            if (backupFiles == null || backupFiles.length == 0) {
                return AjaxResult.success(new ArrayList<>());
            }

            List<BackupInfo> backups = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (File file : backupFiles) {
                BackupInfo info = new BackupInfo();
                info.setFileName(file.getName());
                info.setFilePath(file.getAbsolutePath());
                info.setFileSize(formatFileSize(file.length()));
                info.setCreateTime(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(file.lastModified()),
                    java.time.ZoneId.systemDefault()
                ).format(formatter));
                backups.add(info);
            }

            // 按时间倒序排序
            backups.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
            
            return AjaxResult.success(backups);
        } catch (Exception e) {
            logger.error("获取备份列表失败", e);
            return AjaxResult.error("获取备份列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除指定备份文件
     */
    @PostMapping("/delete")
    public AjaxResult deleteBackup(String fileName) {
        try {
            String backupPath = System.getProperty("backup.path", "./backup");
            File backupFile = new File(backupPath, fileName);
            
            if (!backupFile.exists()) {
                return AjaxResult.error("备份文件不存在");
            }

            if (backupFile.delete()) {
                logger.info("删除备份文件: {}", fileName);
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            logger.error("删除备份失败", e);
            return AjaxResult.error("删除备份失败: " + e.getMessage());
        }
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 备份文件信息
     */
    public static class BackupInfo {
        private String fileName;
        private String filePath;
        private String fileSize;
        private String createTime;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
