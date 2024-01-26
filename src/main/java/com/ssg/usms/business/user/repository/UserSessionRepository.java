package com.ssg.usms.business.user.repository;


public interface UserSessionRepository {

    void deleteSession(String principal);
}
