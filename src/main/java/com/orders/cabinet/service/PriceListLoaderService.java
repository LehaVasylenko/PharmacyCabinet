package com.orders.cabinet.service;

import com.orders.cabinet.mapper.ShopMapper;
import com.orders.cabinet.model.api.PriceList;
import com.orders.cabinet.model.db.dto.ShopsDTO;
import com.orders.cabinet.repository.DrugCacheRepository;
import com.orders.cabinet.repository.ShopRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceListLoaderService {

    final RestTemplate restTemplate;
    final DrugCacheRepository drugCacheRepository;
    final DrugNameService drugNameService;
    final ShopRepository shopRepository;
    final ShopMapper shopMapper;
    final CacheService cacheService;

    @Value("${geoapteka.api.url}")
    String geoaptUrl;

    @Scheduled(cron = "${scheduled.cron.pricelist.cache}")
    public void mainMethod() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(this::startLoad);
        future.exceptionally(ex -> {
            log.error("Error during loading data", ex);
            return null;
        });
    }

    @PostConstruct
    public void init() {
        mainMethod();
    }

    private void startLoad() {
        List<ShopsDTO> shops = loadLoggedShops();
        List<CompletableFuture<Void>> futures = shops.stream()
                .map(shop -> CompletableFuture.runAsync(() -> {
                    try {
                        setCacheForPriceListByShop(shop.getShopId());
                    } catch (ExecutionException | InterruptedException e) {
                        log.error("Error setting cache for shop {}", shop.getShopId(), e);
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("Error during batch processing of shops", ex);
                    return null;
                });
    }

    @Async
    public void setCacheForPriceListByShop(String addressId) throws ExecutionException, InterruptedException {
        List<PriceList> propsByShop = getPropsByShop(addressId);
        List<String> drugIds = propsByShop.stream()
                .map(PriceList::getDrugId)
                .toList();

        drugNameService.setDrugNames(drugIds);

        propsByShop.forEach(priceList -> {
            drugCacheRepository.findById(priceList.getDrugId())
                    .ifPresentOrElse(drugCache -> {
                        priceList.setDrugName(drugCache.getDrugName());
                        priceList.setDrugLink(drugCache.getDrugLink());
                    }, () -> {
                        priceList.setDrugName("Some drug");
                        priceList.setDrugLink("Some link");
                    });
        });

        cacheService.cachePriceList(addressId, propsByShop);
    }

    public List<ShopsDTO> loadLoggedShops() {
        return shopRepository.findAllByLoggedTrue()
                .stream()
                .map(shopMapper::toDto)
                .toList();
    }

    public List<PriceList> getPropsByShop(String shopId) {
        String apiUrl = new StringBuilder().append(geoaptUrl).append("/get_props_by_shop/").append(shopId).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("Fetching props for shop: {}", shopId);

        ResponseEntity<List<PriceList>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        List<PriceList> responseBody = response.getBody();
        if (responseBody != null) {
            log.info("{} -> {}", response.getStatusCode(), responseBody.size());
            return responseBody;
        } else {
            log.warn("No data returned for shop: {}", shopId);
            return List.of();
        }
    }
}
