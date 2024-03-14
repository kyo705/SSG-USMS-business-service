package com.ssg.usms.business.store.repository;

import com.ssg.usms.business.store.constant.StoreState;
import com.ssg.usms.business.store.exception.NotExistingStoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ssg.usms.business.store.repository.StoreSpecifications.*;

@Repository
@RequiredArgsConstructor
public class JpaStoreRepository implements StoreRepository {

    private final SpringDataJpaStoreRepository springDataJpaStoreRepository;

    @Override
    public Store save(Store store) {
        return springDataJpaStoreRepository.save(store);
    }

    @Override
    public Store findById(Long id) {
        return springDataJpaStoreRepository.findById(id).orElseThrow(NotExistingStoreException::new);
    }

    @Override
    public List<Store> findAll(Long userId, String businessCode, StoreState state, long storeId, int size) {

        Specification<Store> specification = Specification.where(null);
        if(userId != null) {
            specification = specification.and(hasUserId(userId));
        }
        if(businessCode != null) {
            specification = specification.and(hasBusinessLicenseCode(businessCode));
        }
        if(state != null) {
            specification = specification.and(hasUserState(state));
        }
        specification = specification.and(greaterThanStoreId(storeId));

        return springDataJpaStoreRepository.findAll(specification, PageRequest.of(0, size)).getContent();
    }

    @Override
    public List<Store> findByUserId(Long userId, long storeId, int size) {

        return springDataJpaStoreRepository.findByUserIdAndIdGreaterThan(userId, storeId, PageRequest.of(0, size));
    }

    @Override
    public List<Store> findByRegion(String region) {

        return springDataJpaStoreRepository.findByStoreAddressLike(region);
    }

    @Override
    public void update(Store store) {

    }

    @Override
    public void delete(Store store) {
        springDataJpaStoreRepository.delete(store);
    }
}
