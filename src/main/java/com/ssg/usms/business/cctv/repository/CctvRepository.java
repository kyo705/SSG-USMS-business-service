package com.ssg.usms.business.cctv.repository;

import java.util.List;

public interface CctvRepository {

    Cctv save(Cctv cctv);

    Cctv findById(Long id);

    List<Cctv> findByStoreId(Long storeId, long cctvId, int size);

    Cctv findByStreamKey(String streamKey);

    void update(Cctv cctv);

    void delete(Cctv cctv);
}
