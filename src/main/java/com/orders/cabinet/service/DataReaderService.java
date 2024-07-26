package com.orders.cabinet.service;

import com.redis_loader.loader.model.PriceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataReaderService {

    @Autowired
    private RedisTemplate<String, List<PriceList>> redisTemplate;

    public List<PriceList> readData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
