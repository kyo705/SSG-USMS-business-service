package com.ssg.usms.business.store.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.ssg.usms.business.config.AwsS3ImgBucketLocalConfig;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.store.dto.ImageDto;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {EmbeddedRedis.class, AwsS3ImgBucketLocalConfig.class})
public class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    S3Mock s3Mock;
    @Value("${aws.s3.image-bucket}")
    private String bucket;

    @DisplayName("이미지 저장 테스트")
    @Test
    public void testSave() throws IOException {

        //given
        amazonS3.createBucket(bucket);

        String filename = UUID.randomUUID().toString().replace("-", "");
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MultipartFile file = new MockMultipartFile(filePath, resource.getInputStream());

        //when
        imageRepository.save(filename, file);

        //then
        S3Object s3Object = amazonS3.getObject(bucket, filename);
        assertThat(resource.getInputStream().readAllBytes()).isEqualTo(s3Object.getObjectContent().readAllBytes());
    }

    @DisplayName("이미지 조회 테스트")
    @Test
    public void testFind() throws IOException, NoSuchMethodException {

        //given
        amazonS3.createBucket(bucket);

        String filename = UUID.randomUUID().toString().replace("-", "");
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
        metadata.setContentLength(resource.contentLength());

        amazonS3.putObject(bucket, filename, resource.getInputStream(), metadata);
        ImageDto imageDto = new ImageDto();
        imageDto.setContentLength(50);

        //when
        ImageDto image = imageRepository.find(filename);

        //then
        assertThat(image.getContent()).isEqualTo(resource.getInputStream().readAllBytes());
    }

    @DisplayName("이미지 존재 유무 확인 테스트")
    @Test
    public void testIsExisting() throws IOException {

        //given
        amazonS3.createBucket(bucket);

        String filename = UUID.randomUUID().toString().replace("-", "");
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        assertThat(imageRepository.isExisting(filename)).isFalse();

        amazonS3.putObject(bucket, filename, resource.getInputStream(), null);

        //when & then
        assertThat(imageRepository.isExisting(filename)).isTrue();
    }

    @AfterEach
    public void shutdownMockS3(){
        s3Mock.stop();
    }
}
