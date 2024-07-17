package com.orders.cabinet.service;

import com.orders.cabinet.exception.NoSuchShopException;
import com.orders.cabinet.exception.PasswordMissmatchException;
import com.orders.cabinet.mapper.ShopInfoCachRepositoryMapper;
import com.orders.cabinet.model.db.ShopInfoCache;
import com.orders.cabinet.model.db.Shops;
import com.orders.cabinet.model.db.dto.ShopInfoCacheDTO;
import com.orders.cabinet.model.login.LoginDTO;
import com.orders.cabinet.repository.ShopInfoCacheRepository;
import com.orders.cabinet.repository.ShopRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableScheduling
public class LoginService {
    PasswordEncoder passwordEncoder;
    UserService userService;
    ShopInfoCacheRepository shopInfoCacheRepository;
    ShopRepository shopRepository;

    @Async
    public CompletableFuture<ShopInfoCacheDTO> authentication(LoginDTO loginDto) {
        UserDetails userDetails = null;
        try {
            userDetails = userService.loadUserByUsername(loginDto.getShopId());
        } catch (UsernameNotFoundException ex) {
            return CompletableFuture.failedFuture(ex);
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword())) {
            return CompletableFuture.failedFuture(new PasswordMissmatchException("Wrong password!"));
        }

        Optional<ShopInfoCache> byId = shopInfoCacheRepository.findById(loginDto.getShopId());
        if (byId.isEmpty())
            return CompletableFuture.failedFuture(new NoSuchShopException("No such shop " + loginDto.getShopId() + " in Geoapteka DB!"));
        else {
            shopRepository.updateLoggedIn(loginDto.getShopId(), true);
            return CompletableFuture.completedFuture(ShopInfoCachRepositoryMapper.INSTANCE.toDto(byId.get()));
        }
    }

    @Async
    public CompletableFuture<String> logOut(String shopId) {
        try {
            doLogOut(shopId);
            return CompletableFuture.completedFuture("Bye");
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private void doLogOut(String shopId) {
        Optional<Shops> byId = shopRepository.findById(shopId);
        if (byId.isEmpty()) throw new NoSuchShopException("No shop with ID " + shopId);
        else {
            Shops shops = byId.get();
            if (!shops.isLogged()) throw new IllegalStateException("Shop " + shopId + " already logged out");
            else shopRepository.updateLoggedIn(shopId, false);
        }
    }

    @Scheduled(cron="${scheduled.cron}")
    public void autoLogOut() {
        List<Shops> allByLoggedTrue = shopRepository.findAllByLoggedTrue();
        if (!allByLoggedTrue.isEmpty()) {
            for (Shops shop : allByLoggedTrue) {
                checkIfShopOpen(shop);
            }
        }
    }

    @Async
    private void checkIfShopOpen(Shops shop) {
        Optional<ShopInfoCache> byId = shopInfoCacheRepository.findById(shop.getShopId());
        if (byId.isPresent())
            if (!isPharmacyOpen(parseSingleSchedule(byId.get().getOpenHours()))) {
                try {
                    doLogOut(shop.getShopId());
                    log.info("{} auto log out", shop.getShopId());
                } catch (Exception e) {
                    log.error("{} auto log out error: {}", shop.getShopId(), e.getMessage());
                }
            }
    }

    private Map<String, Map<String, String>> parseSingleSchedule(String schedule) {
        String patternString = "([A-Za-z, -]+) (\\d{2}:\\d{2})-(\\d{2}:\\d{2})";
        Pattern pattern = Pattern.compile(patternString);

        Map<String, Map<String, String>> scheduleMap = new HashMap<>();
        List<String> daysOfWeek = Arrays.asList("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su");

        Matcher matcher = pattern.matcher(schedule);

        while (matcher.find()) {
            String days = matcher.group(1).replace(" ", "");
            String start = matcher.group(2);
            String end = matcher.group(3);

            for (String day : days.split(",")) {
                if (day.contains("-")) {
                    String[] dayRange = day.split("-");
                    int startIndex = daysOfWeek.indexOf(dayRange[0]);
                    int endIndex = daysOfWeek.indexOf(dayRange[1]);

                    for (int i = startIndex; i <= endIndex; i++) {
                        scheduleMap.put(daysOfWeek.get(i), createScheduleMap(start, end));
                    }
                } else {
                    scheduleMap.put(day, createScheduleMap(start, end));
                }
            }
        }

        return scheduleMap;
    }

    private Map<String, String> createScheduleMap(String start, String end) {
        Map<String, String> timeMap = new HashMap<>();
        timeMap.put("start", start);
        if (end.equals("00:00")) timeMap.put("end", "23:59");
        else timeMap.put("end", end);
        return timeMap;
    }

    private boolean isPharmacyOpen(Map<String, Map<String, String>> schedule) {
        LocalDate date = LocalDate.now();
        LocalTime now = LocalTime.now();

        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).substring(0, 2);
        Map<String, String> todaySchedule = schedule.get(day);

        if (todaySchedule == null) {
            return false;
        }

        LocalTime start = LocalTime.parse(todaySchedule.get("start"));
        LocalTime end = LocalTime.parse(todaySchedule.get("end"));
        if (start.equals(end)) return true;

        return !now.isBefore(start) && !now.isAfter(end);
    }
}
