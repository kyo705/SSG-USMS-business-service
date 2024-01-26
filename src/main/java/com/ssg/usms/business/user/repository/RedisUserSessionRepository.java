package com.ssg.usms.business.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class RedisUserSessionRepository implements UserSessionRepository{

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public void deleteSession(String principal) {

        List<String> sessionId = sessionRepository.findByPrincipalName(principal)
                .values()
                .stream()
                .filter(session -> !session.isExpired())
                .map(Session::getId)
                .collect(Collectors.toList());

        sessionRepository.deleteById(sessionId.get(0));
    }
}
