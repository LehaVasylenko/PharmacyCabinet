package com.orders.cabinet.service;

import com.orders.cabinet.model.api.DrugInfo;
import com.orders.cabinet.model.db.DrugCache;
import com.orders.cabinet.repository.DrugCacheRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DrugNameService {

    DrugCacheRepository drugCacheRepository;
    RestTemplate restTemplate;

    @Async("taskExecutor")
    public CompletableFuture<String> getDrugName(String drugId) {
        Optional<DrugCache> cachedDrug = drugCacheRepository.findById(drugId);
        if (cachedDrug.isPresent()) {
            return CompletableFuture.completedFuture(cachedDrug.get().getDrugName());
        } else {
            String url = "https://api.geoapteka.com.ua/get_item/" + drugId;
            DrugInfo drugInfo = restTemplate.getForObject(url, DrugInfo.class);
            String drugName = (drugInfo != null) ? drugInfo.getDrugData() : "Unknown Drug";
            if (drugInfo != null) {
                DrugCache newCacheEntry = DrugCache.builder()
                        .drugId(drugId)
                        .drugName(drugName)
                        .build();
                drugCacheRepository.save(newCacheEntry);
            }
            return CompletableFuture.completedFuture(drugName);
        }
    }
}

