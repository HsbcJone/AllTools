package minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MinioUploadContentExample {
    public static void main(String[] args) {
        try {
            // 创建 MinioClient 实例
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000") // MinIO 服务器地址
                    .credentials("mengxp", "m602012854") // 使用您的 AK 和 SK
                    .build();

            // 要上传的文件内容
            String fileContent = "Hello, MinIO!";
            InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("hudi") // 存储桶名称
                            .object("hello.txt") // 文件名称
                            .stream(inputStream, inputStream.available(), -1) // 文件流
                            .contentType("text/plain") // 文件类型
                            .build()
            );

            System.out.println("File uploaded successfully.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
