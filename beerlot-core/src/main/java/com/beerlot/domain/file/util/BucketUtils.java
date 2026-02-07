package com.beerlot.domain.file.util;


import com.beerlot.domain.file.FileDirectoryType;
import com.beerlot.exception.ErrorMessage;
import com.beerlot.exception.ProcessingException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;

@Component
public class BucketUtils {

    @Value("${spring.cloud.gcp.storage.access-base-url}")
    private String accessBaseUrl;

    @Value("${spring.cloud.gcp.storage.image-bucket-suffix}")
    private String bucketSuffix;

    public String uploadFile(FileDirectoryType fileDirectory, MultipartFile multipartFile) {
        try {
            File file = convertFile(multipartFile);

            String filename = multipartFile.getOriginalFilename();

            String bucketName = fileDirectory.getDirectoryName() + '-' + bucketSuffix;

            String objectName = filename
                    + "-"
                    + new Timestamp(System.currentTimeMillis()).getTime();

            Storage storage = StorageOptions.getDefaultInstance().getService();
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(multipartFile.getContentType())
                    .build();

            storage.create(blobInfo, Files.readAllBytes(file.toPath()));
            file.delete();

            return accessBaseUrl
                    + "/"
                    + bucketName
                    + "/"
                    + objectName;
        } catch (IOException e) {
            throw new ProcessingException(ErrorMessage.FILE__READ_PROCESSING_ERROR.getMessage());
        }
    }

    private File convertFile(MultipartFile file) {
        try {
            File convertedFile = new File(file.getOriginalFilename());

            FileOutputStream outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            outputStream.close();

            return convertedFile;
        } catch(IOException e) {
            throw new ProcessingException(ErrorMessage.FILE__CONVERT_PROCESSING_ERROR.getMessage());
        }
    }
}
