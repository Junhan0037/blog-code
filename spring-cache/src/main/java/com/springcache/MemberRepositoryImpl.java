package com.springcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private static Logger logger = LoggerFactory.getLogger(MemberRepositoryImpl.class);

    @Override
    public Member findByNameNoCache(String name) {
        slowQuery(2000);
        return new Member(0, name + "@gmail.com", name);
    }

    @Override
    @Cacheable(value = "findMemberCache", key = "#name") // ehcache.xml에서 지정한 findMemberCache 캐시를 사용하겠다는 의미
    public Member findByNameCache(String name) {
        slowQuery(2000);
        return new Member(0, name + "@gmail.com", name);
    }

    @Override
    @CacheEvict(value = "findMemberCache", key = "#name") // 해당 캐시 내용을 지우겠다는 의미
    public void refresh(String name) {
        logger.info(name + "의 Cache Clear!");
    }

    // 빅쿼리를 돌리다는 과정
    private void slowQuery(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
