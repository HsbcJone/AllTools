package minio;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import io.minio.errors.MinioException;

import java.io.FileOutputStream;
import java.io.InputStream;

public class MinioDownloadExample {
    public static void main(String[] args) {
        try {
            // 创建 MinioClient 实例
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000") // MinIO 服务器地址
                    .credentials("mengxp", "m602012854") // 使用您的 AK 和 SK
                    .build();

            // 下载文件
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("hudi") // 存储桶名称
                            .object("emailTest.jar") // 文件名称
                            .build());

            // 将文件保存到本地
            FileOutputStream fileOutputStream = new FileOutputStream("/opt/minioData/downloaded-hello.txt");
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, bytesRead);
            }

            // 关闭流
            stream.close();
            fileOutputStream.close();

            System.out.println("File downloaded successfully.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
