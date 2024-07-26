package com.redis_loader.loader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@RedisHash
public class PriceList implements Serializable {
    @JsonProperty("id_drug")
    String drugId;
    @JsonProperty("drug_name")
    String drugName;
    @JsonProperty("drug_link")
    String drugLink;
    String quant;
    String price;
    Integer pfactor;
    String errorMessage;
}
