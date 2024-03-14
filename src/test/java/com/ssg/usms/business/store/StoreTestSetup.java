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
                                .user(2L)
                                .storeId(6L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .user(1L)
                                .storeState(StoreState.APPROVAL)
                                .storeId(1L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .user(1L)
                                .storeState(StoreState.APPROVAL)
                                .businessLicenseCode("123-45-67890")
                                .storeId(0L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .storeState(StoreState.DISAPPROVAL)
                                .businessLicenseCode("123-45-67890")
                                .storeId(0L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .user(2L)
                                .businessLicenseCode("123-45-67890")
                                .storeId(0L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .storeState(StoreState.READY)
                                .storeId(0L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .businessLicenseCode("123-45-67890")
                                .storeId(0L)
                                .size(5)
                                .build()
                ) ,
                Arguments.of(
                        HttpRequestRetrievingStoreDto
                                .builder()
                                .storeId(0L)
                                .size(5)
                                .build()
                )
        );
    }

    public static Stream<Arguments> getStoreStateAndComment() {

        return Stream.of(
                Arguments.of(StoreState.APPROVAL, "매장 승인 완료"),
                Arguments.of(StoreState.DISAPPROVAL, "사업자 등록증 사본 이미지가 더 선명해야 합니다."),
                Arguments.of(StoreState.STOPPED, "이상 상태 발견.. 관리자 검토 중입니다.")
        );
    }
}
