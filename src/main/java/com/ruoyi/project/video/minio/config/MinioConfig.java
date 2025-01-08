package com.ruoyi.project.video.minio.config;

import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
public class MinioConfig implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);


    @Value(value = "${minio.bucket}")
    private String bucket;

    @Value(value = "${minio.host}")
    private String host;

    @Value(value = "${minio.url}")
    private String url;

    @Value(value = "${minio.access-key}")
    private String accessKey;

    @Value(value = "${minio.secret-key}")
    private String secretKey;

    private MinioClient minioClient;


    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.hasText(url, "Minio url 为空");
        Assert.hasText(accessKey, "Minio accessKey为空");
        Assert.hasText(secretKey, "Minio secretKey为空");
        this.minioClient = new MinioClient(this.host, this.accessKey, this.secretKey);

    }


    /**
     * 上传
     */
    /*public String putObject(MultipartFile multipartFile) throws Exception {
        // bucket 不存在，创建
        String bucket = this.bucket;
        if (!minioClient.bucketExists(this.bucket)) {
            minioClient.makeBucket(this.bucket);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 上传文件的名称
            String fileName = "league/game241107/" +multipartFile.getOriginalFilename();
            // PutObjectOptions，上传配置(文件大小，内存中文件分片大小)
            PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
            // 文件的ContentType
            putObjectOptions.setContentType(multipartFile.getContentType());
            minioClient.putObject(this.bucket, fileName, inputStream, putObjectOptions);
            // 返回访问路径
            return this.url + UriUtils.encode(fileName, StandardCharsets.UTF_8);
        }
    }*/

    /**
     * 上传
     */
    public String putObject(MultipartFile multipartFile,Integer leagueId,Integer gameId ,Integer temp) throws Exception {
        log.info("leagueId= "+leagueId);
        log.info("gameId= "+gameId);
        log.info("multipartFile= "+ multipartFile);
        System.out.print("文件： "+multipartFile);
        // 检查所有参数是否为空
        if (multipartFile == null || multipartFile.isEmpty() || leagueId == null || gameId == null) {
            throw new IllegalArgumentException("file、leagueId、gameId参数不能空");
        }
        // bucket 不存在，创建
        if (!minioClient.bucketExists(this.bucket)) {
            minioClient.makeBucket(this.bucket);
        }
        String targetDirectory =null;
        //temp ==1 合并视频上传,要单独放在一个文件夹
        if (temp !=null && temp ==1){
            targetDirectory = "league" + leagueId + "/game" + gameId + "/merger/";
        }else {
            targetDirectory = "league" + leagueId + "/game" + gameId + "/video/";
        }
        String fileName = targetDirectory + multipartFile.getOriginalFilename();
        minioClient.putObject(
                PutObjectArgs.builder().bucket(this.bucket).object(targetDirectory).stream(
                        new ByteArrayInputStream(new byte[] {}), 0, -1)
                        .build());
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // PutObjectOptions，上传配置(文件大小，内存中文件分片大小)
            PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
            // 文件的ContentType
            putObjectOptions.setContentType(multipartFile.getContentType());
            minioClient.putObject(this.bucket, fileName, inputStream, putObjectOptions);
            // 返回访问路径
            return this.url + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        }
    }

    /**
     * 文件下载
     */
    public void download(Integer leagueId,Integer gameId, String fileName, HttpServletResponse response) {
        // 从链接中得到文件名
        InputStream inputStream;
        try {
            MinioClient minioClient = new MinioClient(host, accessKey, secretKey);
            // 检查目录是否存在
            boolean isExist = minioClient.bucketExists(bucket);
            if (!isExist) {
                System.out.println("Bucket does not exist.");
                return;
            }
            // 列出目录下的所有文件
            String directoryName ="league" + leagueId + "/game" + gameId + "/video/";;
            Iterable<Result<Item>> results = minioClient.listObjects(bucket, directoryName);
            boolean fileFound = false;
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.objectName().equals(directoryName + fileName)) {
                    fileFound = true;
                    break;
                }
            }
            if (!fileFound) {
                System.out.println("File not found in the specified directory.");
                return;
            }

            // 获取文件的元数据和输入流
            ObjectStat stat = minioClient.statObject(bucket, directoryName + fileName);
            inputStream = minioClient.getObject(bucket, directoryName + fileName);

            // 设置响应头信息
            response.setContentType(stat.contentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 将文件内容写入响应输出流
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                response.getOutputStream().write(buffer, 0, length);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("有异常：" + e);
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     * @throws Exception
     */
    public List<String> listBucketNames()
            throws Exception {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 查看所有桶
     *
     * @return
     * @throws Exception
     */
    public List<Bucket> listBuckets()
            throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean bucketExists(String bucketName) throws Exception {
        boolean flag = minioClient.bucketExists(bucketName);
        if (flag) {
            return true;
        }
        return false;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean makeBucket(String bucketName)
            throws Exception {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(bucketName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除桶
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public boolean removeBucket(String bucketName)
            throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClient.removeBucket(bucketName);
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws Exception
     */
    public Iterable<Result<Item>> listObjects(String bucketName) throws Exception {
        Iterable<Result<Item>> myObjects = null;
        try {
            myObjects = minioClient.listObjects(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error listing objects in bucket", e);
        }
        return myObjects;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws Exception
     */
    public List<String> listObjectNames(String bucketName) throws Exception {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }

        return listObjectNames;
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @throws Exception
     */
    public boolean removeObject(String bucketName, String objectName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            List<String> objectList = listObjectNames(bucketName);
            for (String s : objectList) {
                if(s.equals(objectName)){
                    minioClient.removeObject(bucketName, objectName);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws Exception
     */
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            url = minioClient.getObjectUrl(bucketName, objectName);
        }
        return url;

    }

}
