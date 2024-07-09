package com.orders.cabinet.model.db.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CorpDTO {
    @NotEmpty
    String corpId;
    @NotEmpty
    String login;
    @NotEmpty
    String password;
    @NotEmpty
    String corpName;
    @Min(24)
    Integer lifeTime;
}
