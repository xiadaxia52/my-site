package cn.luischen.oss;

import io.minio.*;
import io.minio.messages.DeleteObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * Minio 工具类
 *
 * @author DaiBo
 */
@Service
public class MinioService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String baseBucketName;

    public MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getBaseBucketName() {
        return baseBucketName;
    }

    public void makeBucket(String bucketName) throws Exception {
        if (!getMinioClient().bucketExists((BucketExistsArgs) ((BucketExistsArgs.Builder) BucketExistsArgs.builder().bucket(bucketName)).build())) {
            getMinioClient().makeBucket((MakeBucketArgs) ((io.minio.MakeBucketArgs.Builder) MakeBucketArgs.builder().bucket(bucketName)).build());
            getMinioClient().setBucketPolicy((SetBucketPolicyArgs) ((io.minio.SetBucketPolicyArgs.Builder) SetBucketPolicyArgs.builder().bucket(bucketName)).config(getPolicyType(bucketName, PolicyType.READ)).build());
        }

    }

    /**
     * 上传文件
     *
     * @param bucketName 桶名
     * @param file       文件
     * @return MinioFile
     * @throws Exception 异常
     */
    public MinioFile putFile(String bucketName, MultipartFile file) throws Exception {
        return this.putFileInputStream(bucketName, file.getOriginalFilename(), file.getInputStream(), "application/octet-stream");
    }

    public MinioFile putFileInputStream(String bucketName, String fileName, InputStream stream, String contentType) throws Exception {
        this.makeBucket(bucketName);
        String originalName = fileName;
        fileName = fileName(fileName);
        getMinioClient().putObject((PutObjectArgs) ((io.minio.PutObjectArgs.Builder) ((io.minio.PutObjectArgs.Builder) PutObjectArgs.builder().bucket(bucketName)).object(fileName)).stream(stream, (long) stream.available(), -1L).contentType(contentType).build());
        MinioFile file = new MinioFile();
        file.setOriginalName(originalName);
        file.setName(fileName);
        file.setDomain(this.getOssHost(bucketName));
        file.setLink(this.fileLink(bucketName, fileName));
        return file;
    }

    public void removeFile(String bucketName, String fileName) throws Exception {
        getMinioClient().removeObject((RemoveObjectArgs) ((io.minio.RemoveObjectArgs.Builder) ((io.minio.RemoveObjectArgs.Builder) RemoveObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
    }

    public void removeFiles(String bucketName, List<String> fileNames) {
        try {
            Stream<DeleteObject> stream = fileNames.stream().map(DeleteObject::new);
            MinioClient var10000 = getMinioClient();
            io.minio.RemoveObjectsArgs.Builder var10001 = (io.minio.RemoveObjectsArgs.Builder) RemoveObjectsArgs.builder().bucket(bucketName);
            stream.getClass();
            var10000.removeObjects((RemoveObjectsArgs) var10001.objects(stream::iterator).build());
        } catch (Throwable var4) {
            throw var4;
        }
    }

    public String getOssHost(String bucketName) {
        return endpoint + "/" + bucketName;
    }

    public String fileLink(String bucketName, String fileName) {
        try {
            return endpoint.concat("/").concat(bucketName).concat("/").concat(fileName);
        } catch (Throwable var4) {
            throw var4;
        }
    }

    public String fileName(String originalFilename) {
        return "upload/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/" + randomUUID() + "." + getFileExtension(originalFilename);
    }

    public static String randomUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return (new UUID(random.nextLong(), random.nextLong())).toString().replace("-", "");
    }

    public static String getFileExtension(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        } else {
            String fileName = (new File(fullName)).getName();
            int dotIndex = fileName.lastIndexOf(46);
            return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
        }
    }

    public static String getPolicyType(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");
        switch (policyType) {
            case WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            case READ_WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucket\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            default:
                builder.append("                \"s3:GetBucketLocation\"\n");
        }

        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n");
        builder.append("        },\n");
        if (PolicyType.READ.equals(policyType)) {
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Deny\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n");
            builder.append("        },\n");
        }

        builder.append("        {\n");
        builder.append("            \"Action\": ");
        switch (policyType) {
            case WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            case READ_WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:GetObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
        }

        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    public enum PolicyType {
        READ("read", "只读"),
        WRITE("write", "只写"),
        READ_WRITE("read_write", "读写");

        private final String type;
        private final String policy;

        public String getType() {
            return this.type;
        }

        public String getPolicy() {
            return this.policy;
        }

        private PolicyType(final String type, final String policy) {
            this.type = type;
            this.policy = policy;
        }
    }
}
