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
/**
 * Service for retrieving and caching drug names.
 *
 * <p>This service provides methods to fetch drug information either from a cache or an external API and updates
 * the cache accordingly.</p>
 *
 * @author Vasylenko Oleksii
 * @company Proxima Research International
 * @version 1.0
 * @since 2024-07-19
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DrugNameService {

    DrugCacheRepository drugCacheRepository;
    RestTemplate restTemplate;

    /**
     * Retrieves the drug information based on the given drug ID.
     *
     * <p>This method checks if the drug information is available in the cache. If not, it fetches the information
     * from a https://api.geoapteka.com.ua/get_item and updates the cache with the retrieved data.</p>
     *
     * @param drugId The ID of the drug to retrieve information for.
     * @return A {@link CompletableFuture} containing the {@link DrugCache} entry with the drug information, or null
     *         if the drug information could not be retrieved.
     */
    @Async("taskExecutor")
    public CompletableFuture<DrugCache> getDrugName(String drugId) {
        DrugCache newCacheEntry = null;
        Optional<DrugCache> cachedDrug = drugCacheRepository.findById(drugId);
        if (cachedDrug.isPresent()) {
            return CompletableFuture.completedFuture(cachedDrug.get());
        } else {
            String url = "https://api.geoapteka.com.ua/get_item/" + drugId;
            DrugInfo drugInfo = restTemplate.getForObject(url, DrugInfo.class);
            String drugName = (drugInfo != null) ? drugInfo.getDrugData() : "Unknown Drug";
            String drugLink = (drugInfo != null) ? drugInfo.getDrugLink() : "";
            if (drugInfo != null) {
                newCacheEntry = DrugCache.builder()
                        .drugId(drugId)
                        .drugName(drugName)
                        .drugLink(drugLink)
                        .build();
                drugCacheRepository.save(newCacheEntry);
            }
            return CompletableFuture.completedFuture(newCacheEntry);
        }
    }
}

