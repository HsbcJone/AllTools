package minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MinioUploadFileExample {
    public static void main(String[] args) {
        try {
            // 创建MinioClient实例
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000") // MinIO 服务器地址
                    .credentials("15CZ2WI3OI1YNZ0WPKVP", "4QwQjlopZROYa6zWLDp6buO75CYK2gEuAljlpPF5") // 使用您的 AK 和 SK
                    .build();

            // 本地文件路径
            String localFilePath = "/opt/minioData/flink2.txt"; // 本地文件路径
            File file = new File(localFilePath);
            InputStream inputStream = new FileInputStream(file);

            // 存储桶名称和对象名称
            String bucketName = "hudi";
            String objectName = "flink2.txt"; // 上传到MinIO后的文件名称

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.length(), -1) // 输入流、文件长度、分片大小（-1表示自动选择）
                            .contentType("text/plain") // 文件类型
                            .build());

            inputStream.close();

            System.out.println("File uploaded successfully.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
