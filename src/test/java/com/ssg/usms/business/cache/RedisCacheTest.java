package com.ssg.usms.business.cache;

import com.amazonaws.services.s3.AmazonS3;
import com.ssg.usms.business.config.AwsS3TranscodeBucketLocalConfig;
import com.ssg.usms.business.config.EmbeddedRedis;
import com.ssg.usms.business.video.repository.VideoRepository;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {EmbeddedRedis.class, AwsS3TranscodeBucketLocalConfig.class})
public class RedisCacheTest {

    private static final String CACHE_KEY = "replayVideoFileList";
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    CacheManager cacheManager;
    @Value("${aws.s3.transcode-video-bucket}")
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
        cacheManager.getCache(CACHE_KEY).clear();
    }

    @DisplayName("한 번 조회된 데이터들은 캐시에 저장된다.")
    @Test
    public void testSaveCache() {

        //given
        String path = "key";

        amazonS3.putObject(bucket, Paths.get(path, "streamKey-55555.m3u8").toString(), "contents");

        //when
        List<String> filenames = videoRepository.getVideoFilenames(path);

        //then
        System.out.println(filenames);
        assertThat(filenames.size()).isEqualTo(1);

        amazonS3.putObject(bucket, Paths.get(path, "streamKey-6666666.m3u8").toString(), "contents");
        List<String> secondFilenameResult = videoRepository.getVideoFilenames(path);

        assertThat(secondFilenameResult.size()).isEqualTo(1);
    }

    @DisplayName("캐시에 저장된 데이터가 존재하면 해당 데이터를 리턴한다.")
    @Test
    public void testFindCache() {

        //given
        String path = "key";
        String filename = "streamKey-55555.m3u8";

        amazonS3.putObject(bucket, Paths.get(path, filename).toString(), "아무 내용");
        videoRepository.getVideoFilenames(path);

        //when
        List<String> filenames = videoRepository.getVideoFilenames(path);

        //then
        assertThat(filenames.size()).isEqualTo(1);
        assertThat(filenames.get(0)).isEqualTo(filename);
    }

    @DisplayName("캐시에 저장된 데이터가 만료되면 다시 원본 저장소에서 조회한다.")
    @Test
    public void testFindCacheWithExpire() throws InterruptedException {

        //given
        String path = "key";
        amazonS3.putObject(bucket, Paths.get(path, "streamKey-55555.m3u8").toString(), "contents");
        List<String> filenames = videoRepository.getVideoFilenames(path);

        assertThat(filenames.size()).isEqualTo(1);

        amazonS3.putObject(bucket, Paths.get(path, "streamKey-6666666.m3u8").toString(), "contents");
        List<String> secondFilenameResult = videoRepository.getVideoFilenames(path);

        assertThat(secondFilenameResult.size()).isEqualTo(1);

        //when
        Thread.sleep(2000);

        //then
        List<String> thirdFilenameResult = videoRepository.getVideoFilenames(path);
        assertThat(thirdFilenameResult.size()).isEqualTo(2);
    }
}
