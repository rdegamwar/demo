package com.example.trade.store;

import com.example.trade.dto.TradeRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Slf4j
@Data
public class TradeStoreDB {

    private final Map<String, TradeRecord> tradeMap = new ConcurrentSkipListMap<>();

    public void persistRecord(TradeRecord tradeRecord) throws TradeProcessingException {
        log.info("Trade Record Received {}", tradeRecord);
        var key = tradeRecord.getTradeId() + "-" + tradeRecord.getVersion();
        log.info("Extracted Key {}", key);
        tradeMap.put(key, tradeRecord);
        log.info("Trade Map Size {}", tradeMap.size());
    }

    public TradeRecord getTrade(String id) {
        return tradeMap.get(id);
    }

    /**
     * exposed only for testing and demo purpose
     */
    public void clearAllData() {
        tradeMap.clear();
    }

    /**
     * exposed only for testing and demo purpose
     */
    public int count() {
        return tradeMap.size();
    }

    public List<TradeRecord> getAllTrades() {
        return tradeMap.values().stream().toList();
    }
}
