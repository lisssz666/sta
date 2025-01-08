//package com.ruoyi.project.testcode;
//
//import io.minio.BucketExistsArgs;
//import io.minio.MakeBucketArgs;
//import io.minio.MinioClient;
//import io.minio.PutObjectArgs;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class Test {
//
//    public static void main(String[] args) {
//        try {
//            /**
//             *  模拟上传文件
//             **/
//            String filePath = "/Users/lisenshuai/Desktop/李森帅专用文件夹/java_tool/navicat 16 MacOS_安装即用.rar"; //要上传的文件路径
//            String serverAddress = "http://1.12.77.209:9098"; //minio服务端地址 注意端口与控制台端口不一样
//            String accessKey = "khPKTKcM5LGqEgwifptA";
//            String secretKey = "kqyvcMH1ziuSuM8gJrfepENLADpRxkHSDprilunr";
//            String bucketName = "livzon";
//
//            //1.创建minio客户端
//            MinioClient minioClient = MinioClient.builder()
//                    .endpoint(serverAddress)
//                    .credentials(accessKey, secretKey)
//                    .build();
//            //2.判断存储桶是否存在，不存在则创建
//            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//            if (!exists){
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            }
//
//            //3.创建file对象
//            File file = new File(filePath);
//
//            //4.file转输入流并上传
//            InputStream in = null;
//            try {
//                in = new FileInputStream(file);
//                minioClient.putObject(PutObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object("文件名3.rar")  //文件名也写死了 所以同名文件会覆盖
//                        .stream(in, in.available(), -1)
//                        .contentType("rar")  //文件类型，这里写死了，实际上需要根据文件动态获取
//                        .build()
//                );
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (in != null) {
//                    try {
//                        in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
