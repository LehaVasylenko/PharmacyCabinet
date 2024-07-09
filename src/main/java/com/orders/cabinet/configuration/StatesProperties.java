package com.orders.cabinet.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "state")
@Getter
@Setter
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatesProperties {
    String cancel;
    String confirm;
    String comlete;
}
