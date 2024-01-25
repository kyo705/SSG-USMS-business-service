package com.ssg.usms.business.cctv;

import com.ssg.usms.business.cctv.repository.Cctv;

import java.util.ArrayList;
import java.util.List;

public class CctvTestSetup {

    public static List<Cctv> getCctvList(Long storeId) {

        Cctv cctv1 = new Cctv();
        cctv1.setId(1L);
        cctv1.setStoreId(storeId);
        cctv1.setName("cctv 1");
        cctv1.setExpired(false);
        cctv1.setStreamKey("스트림키 1");

        Cctv cctv2 = new Cctv();
        cctv2.setId(2L);
        cctv2.setStoreId(storeId);
        cctv2.setName("cctv 2");
        cctv2.setExpired(false);
        cctv2.setStreamKey("스트림키 2");

        Cctv cctv3 = new Cctv();
        cctv3.setId(3L);
        cctv3.setStoreId(storeId);
        cctv3.setName("cctv 3");
        cctv3.setExpired(false);
        cctv3.setStreamKey("스트림키 3");

        Cctv cctv4 = new Cctv();
        cctv4.setId(4L);
        cctv4.setStoreId(storeId);
        cctv4.setName("cctv 4");
        cctv4.setExpired(false);
        cctv4.setStreamKey("스트림키 4");

        Cctv cctv5 = new Cctv();
        cctv5.setId(5L);
        cctv5.setStoreId(storeId);
        cctv5.setName("cctv 5");
        cctv5.setExpired(false);
        cctv5.setStreamKey("스트림키 5");

        List<Cctv> result = new ArrayList<>();
        result.add(cctv1);
        result.add(cctv2);
        result.add(cctv3);
        result.add(cctv4);
        result.add(cctv5);

        return result;
    }
}
