package com.ssg.usms.business.cache;

import com.amazonaws.services.s3.AmazonS3;
import com.ssg.usms.business.config.AwsS3TranscodeBucketLocalConfig;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.store.dto.ImageDto;
import com.ssg.usms.business.store.repository.ImageRepository;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ssg.usms.business.config.CacheConfiguration.IMG_FILE_CACHE_KEY;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {EmbeddedRedis.class, AwsS3TranscodeBucketLocalConfig.class})
public class ImgRedisCacheTest {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    CacheManager cacheManager;
    @Value("${aws.s3.image-bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    String region;
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    S3Mock s3Mock;

    @BeforeEach
    public void setup() {
        amazonS3.createBucket(bucket);
    }

    @AfterEach
    public void after(){

        s3Mock.stop();
        amazonS3.deleteBucket(bucket);
        cacheManager.getCache(IMG_FILE_CACHE_KEY).clear();
    }

    @DisplayName("이미지 파일 조회시 해당 값이 캐시에 저장되어 활용된다.")
    @Test
    public void testSaveCache() throws IOException {

        //given
        String key = "key";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        MultipartFile file = new MockMultipartFile(filePath, resource.getInputStream());

        assertThat(cacheManager.getCache(IMG_FILE_CACHE_KEY).get(key)).isNull();

        //when
        imageRepository.save(key, file);

        //then
        assertThat(cacheManager.getCache(IMG_FILE_CACHE_KEY).get(key)).isNotNull();
    }

    @DisplayName("이미지 파일 조회시 해당 값이 캐시에 저장되어 활용된다.")
    @Test
    public void testFindCache() throws IOException {

        //given
        String key = "key";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);

        amazonS3.putObject(bucket,key, resource.getFile());

        //when
        ImageDto image = imageRepository.find(key);

        //then
        ImageDto cacheImage = (ImageDto) cacheManager.getCache(IMG_FILE_CACHE_KEY).get(key).get();
        assertThat(image.getContent()).isEqualTo(cacheImage.getContent());
    }

    @DisplayName("캐시에 저장된 이미지가 ttl이 지나면 초기화 된다.")
    @Test
    public void testCacheWithTtl() throws IOException, InterruptedException {

        //given
        String key = "key";
        String filePath = "beach.jpg";
        ClassPathResource resource = new ClassPathResource(filePath);
        amazonS3.putObject(bucket,key, resource.getFile());

        ImageDto image = imageRepository.find(key);

        String file2Path = "astronomy.jpg";
        ClassPathResource resource2 = new ClassPathResource(file2Path);
        amazonS3.putObject(bucket, key, resource2.getFile());

        ImageDto cacheImage = imageRepository.find(key);

        assertThat(image.getContent()).isEqualTo(cacheImage.getContent());

        //when
        Thread.sleep(2000L);

        //then
        ImageDto newImage = imageRepository.find(key);

        assertThat(image.getContent()).isNotEqualTo(newImage.getContent());
    }
}
