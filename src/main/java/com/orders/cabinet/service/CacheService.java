package com.orders.cabinet.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.orders.cabinet.event.Timed;
import com.orders.cabinet.model.api.PriceList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CacheService {

    Cache<String, List<PriceList>> cache;

    public CacheService() {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    public void cachePriceList(String shopId, List<PriceList> priceLists) {
        cache.put(shopId, priceLists);

        MemoryUsage heapMemoryUsage = ManagementFactory
                .getMemoryMXBean()
                .getHeapMemoryUsage();
        long l = (heapMemoryUsage.getMax() - heapMemoryUsage.getUsed()) / (1024 * 1024);
        log.info("Memory: {} MB", l);
    }

    @Timed
    public List<PriceList> getCachedPriceList(String shopId) {
        return cache.getIfPresent(shopId);
    }
}

