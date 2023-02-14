package com.example.trade.schedule;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Collectors;

import com.example.trade.store.TradeStoreDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Store should automatically update the expire flag if in a store the trade crosses the maturity
 * date.
 */

@Component
@Slf4j
public class ScheduleTaskForExpiredTrades {

    @Autowired
    TradeStoreDB tradeStoreDB;

    @Scheduled(cron = "*/5 * * * * ?")
    public void cronJobSch() {
        log.info("Updating Expired Trades");
        tradeStoreDB.getTradeMap().values().stream()
                .filter(s -> s.getMaturityDate().isBefore(LocalDate.now()))
                .forEach(tradeRecord -> {
            tradeStoreDB.getTrade(tradeRecord.getTradeId()+"-"+tradeRecord.getVersion()).setExpired(true);
        });
    }
}
