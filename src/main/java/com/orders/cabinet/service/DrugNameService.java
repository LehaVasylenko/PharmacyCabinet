package com.orders.cabinet.service;

import com.orders.cabinet.model.api.DrugInfo;
import com.orders.cabinet.model.db.DrugCache;
import com.orders.cabinet.repository.DrugCacheRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DrugNameService {

    final DrugCacheRepository drugCacheRepository;
    final RestTemplate restTemplate;

    @Value("${geoapteka.api.url}")
    String apiUrl;

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
            String url = apiUrl + "/get_item/" + drugId;
            DrugInfo drugInfo = restTemplate.getForObject(url, DrugInfo.class);
            if (drugInfo != null) {
                newCacheEntry = getdrugCache(drugInfo);
                drugCacheRepository.save(newCacheEntry);
            }
            return CompletableFuture.completedFuture(newCacheEntry);
        }
    }

    @Async("taskExecutor")
    public void setDrugNames(List<String> drugsId) {
        List<DrugCache> result = new ArrayList<>();
        List<String> baseList = drugCacheRepository.findAllDrugIds();
        log.info("BaseList size: {}", baseList.size());

        Set<String> baseSet = Set.copyOf(baseList);

        List<String> missingIds = drugsId.stream()
                .filter(id -> !baseSet.contains(id))
                .toList();
        log.info("MissingList size: {}", missingIds.size());

        if (!missingIds.isEmpty()) {
            //perform API call
            List<DrugInfo> drugInfoList = getDrugInfo(missingIds);
            if (!drugInfoList.isEmpty()) {
                for (DrugInfo info: drugInfoList) {
                    result.add(getdrugCache(info));
                }
                //write missing ID's to base
                log.info("Written in base: {}", result.size());
                drugCacheRepository.saveAll(result);
            }
        }
    }

    private DrugCache getdrugCache(DrugInfo info) {
        return DrugCache.builder()
                .drugId(info.getId())
                .drugName(info.getDrugData())
                .drugLink(info.getDrugLink())
                .build();
    }

    public List<DrugInfo> getDrugInfo(List<String> drugIds) {
        try {
            HttpEntity<List<String>> request = new HttpEntity<>(drugIds, createHeaders());

            DrugInfo[] responseArray = restTemplate
                    .postForObject(apiUrl + "/get_item_many", request, DrugInfo[].class);

            if (responseArray != null) {
                log.info("Drug Info response: {}", responseArray.length);
                return Arrays.stream(responseArray).filter(Objects::nonNull).toList();
            }
            else
                return new ArrayList<>();
        } catch (ResourceAccessException e) {
            log.error("Failed to connect to the API", e);
            return new ArrayList<>();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

