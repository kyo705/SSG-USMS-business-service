package com.ssg.usms.business.store;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.dto.HttpRequestRetrievingStoreDto;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class StoreTestSetup {

    public static Stream<Arguments> getHttpRequestRetrievingStoreDto() {

        return Stream.of(
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .userId(1L)
                                .offset(1)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .userId(1L)
                                .storeState(StoreState.APPROVAL)
                                .offset(1)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .userId(1L)
                                .storeState(StoreState.APPROVAL)
                                .businessLicenseCode("123-45-67890")
                                .offset(0)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .storeState(StoreState.DISAPPROVAL)
                                .businessLicenseCode("123-45-67890")
                                .offset(0)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .userId(2L)
                                .businessLicenseCode("123-45-67890")
                                .offset(0)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .storeState(StoreState.READY)
                                .offset(0)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .businessLicenseCode("123-45-67890")
                                .offset(0)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .offset(0)
                                .size(5)
                                .build()
                )
        );
    }
}
