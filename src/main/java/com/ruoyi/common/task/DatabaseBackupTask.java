package com.ruoyi.common.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据库定时备份任务
 * 每5天执行一次备份
 */
@Component
public class DatabaseBackupTask {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupTask.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.path:./backup}")
    private String backupPath;

    @Value("${backup.retention.days:30}")
    private int retentionDays;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 每5天执行一次数据库备份
     * cron表达式: 0 0 2 * * ?  每天凌晨2点执行
     * 配合日期判断实现每5天备份一次
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledBackup() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfMonth() % 5 == 0) {
            executorService.submit(this::performBackup);
        }
    }

    /**
     * 手动触发备份
     */
    public void manualBackup() {
        executorService.submit(this::performBackup);
    }

    /**
     * 执行数据库备份
     */
    private void performBackup() {
        Connection connection = null;
        try {
            String dbName = parseDatabaseName(dbUrl);

            File backupDir = new File(backupPath);
            if (!backupDir.exists()) {
                boolean created = backupDir.mkdirs();
                if (created) {
                    logger.info("创建备份目录: {}", backupPath);
                }
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = String.format("%s_%s.sql", dbName, timestamp);
            String backupFilePath = new File(backupDir, backupFileName).getAbsolutePath();

            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            
            boolean success = backupDatabase(connection, dbName, backupFilePath);

            if (success) {
                logger.info("数据库备份成功: {}", backupFilePath);
                cleanupOldBackups(backupDir);
            } else {
                logger.error("数据库备份失败");
            }
        } catch (Exception e) {
            logger.error("数据库备份异常", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败", e);
                }
            }
        }
    }

    /**
     * 使用JDBC备份数据库
     */
    private boolean backupDatabase(Connection connection, String dbName, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "UTF-8"))) {
            
            // 写入备份信息头
            writer.write("-- ===========================================");
            writer.newLine();
            writer.write("-- 数据库备份文件");
            writer.newLine();
            writer.write("-- 数据库: " + dbName);
            writer.newLine();
            writer.write("-- 备份时间: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.newLine();
            writer.write("-- ===========================================");
            writer.newLine();
            writer.newLine();

            // 设置当前数据库
            writer.write("USE `" + dbName + "`;");
            writer.newLine();
            writer.newLine();

            // 获取所有表名
            List<String> tableNames = getTableNames(connection, dbName);
            
            // 备份每个表
            for (String tableName : tableNames) {
                logger.info("备份表: {}", tableName);
                
                // 写入DROP TABLE语句
                writer.write("-- 删除表（如果存在）");
                writer.newLine();
                writer.write("DROP TABLE IF EXISTS `" + tableName + "`;");
                writer.newLine();
                writer.newLine();

                // 写入CREATE TABLE语句
                String createTableSql = getCreateTableSql(connection, tableName);
                writer.write(createTableSql);
                writer.newLine();
                writer.newLine();

                // 写入INSERT语句
                backupTableData(connection, writer, tableName);
                writer.newLine();
            }

            writer.flush();
            return true;

        } catch (Exception e) {
            logger.error("JDBC备份数据库失败", e);
            return false;
        }
    }

    /**
     * 获取数据库中所有表名
     */
    private List<String> getTableNames(Connection connection, String dbName) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getTables(dbName, null, "%", new String[]{"TABLE"});
        
        while (rs.next()) {
            tableNames.add(rs.getString("TABLE_NAME"));
        }
        rs.close();
        
        return tableNames;
    }

    /**
     * 获取表的CREATE TABLE语句
     */
    private String getCreateTableSql(Connection connection, String tableName) throws SQLException {
        String sql = "SHOW CREATE TABLE `" + tableName + "`";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getString(2) + ";";
            }
        }
        return "";
    }

    /**
     * 备份表数据
     */
    private void backupTableData(Connection connection, BufferedWriter writer, String tableName) 
            throws SQLException, IOException {
        
        String selectSql = "SELECT * FROM `" + tableName + "`";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            if (!rs.isBeforeFirst()) {
                return; // 表为空，跳过
            }

            writer.write("-- 插入数据");
            writer.newLine();
            
            List<String> batchValues = new ArrayList<>();
            int batchSize = 1000;
            int count = 0;

            while (rs.next()) {
                StringBuilder values = new StringBuilder("(");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        values.append(", ");
                    }
                    
                    Object value = rs.getObject(i);
                    if (value == null) {
                        values.append("NULL");
                    } else if (value instanceof String || value instanceof Date 
                            || value instanceof Timestamp || value instanceof Time) {
                        String strValue = value.toString();
                        strValue = strValue.replace("\\", "\\\\");
                        strValue = strValue.replace("'", "\\'");
                        strValue = strValue.replace("\"", "\\\"");
                        values.append("'").append(strValue).append("'");
                    } else {
                        values.append(value);
                    }
                }
                values.append(")");
                batchValues.add(values.toString());
                count++;

                if (batchValues.size() >= batchSize) {
                    writeInsertStatement(writer, tableName, batchValues);
                    batchValues.clear();
                }
            }

            if (!batchValues.isEmpty()) {
                writeInsertStatement(writer, tableName, batchValues);
            }
            
            logger.debug("表 {} 备份 {} 条数据", tableName, count);
        }
    }

    /**
     * 写入INSERT语句
     */
    private void writeInsertStatement(BufferedWriter writer, String tableName, List<String> values) 
            throws IOException {
        
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO `" + tableName + "` VALUES ");
        
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append(values.get(i));
        }
        sql.append(";");
        
        writer.write(sql.toString());
        writer.newLine();
    }

    /**
     * 清理过期备份文件
     */
    private void cleanupOldBackups(File backupDir) {
        try {
            File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));
            if (backupFiles == null || backupFiles.length == 0) {
                return;
            }

            long retentionMillis = (long) retentionDays * 24 * 60 * 60 * 1000;
            long now = System.currentTimeMillis();

            int deletedCount = 0;
            for (File file : backupFiles) {
                if (now - file.lastModified() > retentionMillis) {
                    if (file.delete()) {
                        deletedCount++;
                        logger.info("删除过期备份: {}", file.getName());
                    }
                }
            }

            if (deletedCount > 0) {
                logger.info("共清理 {} 个过期备份文件", deletedCount);
            }
        } catch (Exception e) {
            logger.error("清理过期备份失败", e);
        }
    }

    /**
     * 从JDBC URL中解析数据库名
     */
    private String parseDatabaseName(String url) {
        int start = url.indexOf("/", url.indexOf("//") + 2);
        int end = url.indexOf("?", start);
        if (end == -1) {
            end = url.length();
        }
        return url.substring(start + 1, end);
    }
}
