package com.example.trade.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Each Trade received in MT / MX or similar message format will be parsed and stored in <b>TradeRecord</b>
 * All fileds are mandatory and can not have null value
 */
@Slf4j
@Data
@Builder
public class TradeRecord implements Serializable {
    @NonNull
    private String tradeId;
    private Integer version;
    @NonNull
    private String counterPartyId;
    @NonNull
    private String bookId;
    @NonNull
    private LocalDate maturityDate;
    @NonNull
    private LocalDate createdDate;
    private boolean isExpired;
}
