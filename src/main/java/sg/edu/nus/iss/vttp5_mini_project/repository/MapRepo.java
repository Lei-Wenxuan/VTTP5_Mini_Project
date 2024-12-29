package sg.edu.nus.iss.vttp5_mini_project.repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MapRepo {

    @Autowired
    @Qualifier("mapTemplate")
    private RedisTemplate<String, Object> template;

    public void create(String redisKey, String hashKey, String hashValue) {
        template.opsForHash().put(redisKey, hashKey, hashValue);
    }

    public Object get(String redisKey, String hashKey) {
        return template.opsForHash().get(redisKey, hashKey);
    }

    public void delete(String redisKey, String hashKey) {
        template.opsForHash().delete(redisKey, hashKey);
    }

    public Boolean hasKey(String redisKey, String hashKey) {
        return template.opsForHash().hasKey(redisKey, hashKey);
    }

    public Map<Object, Object> getEntries(String redisKey) {
        return template.opsForHash().entries(redisKey);
    }

    public Set<Object> getKeys(String redisKey) {
        return template.opsForHash().keys(redisKey);
    }

    public List<Object> getValues(String redisKey) {
        return template.opsForHash().values(redisKey);
    }

    public Long size(String redisKey) {
        return template.opsForHash().size(redisKey);
    }
    
    public void increment(String redisKey, String hashKey, Integer incr) {
        template.opsForHash().increment(redisKey, hashKey, incr);
    }

    public void expire(String redisKey, Long expireSeconds) {
        Duration expireDuration = Duration.ofSeconds(expireSeconds);
        template.expire(redisKey, expireDuration);
    }
}