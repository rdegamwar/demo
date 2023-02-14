package com.example.trade.service;

import com.example.trade.aspect.TrackExecutionTime;
import com.example.trade.dto.TradeRecord;
import com.example.trade.store.TradeProcessingException;
import com.example.trade.store.TradeStoreDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TradeService {

    public static final String DATE_MESSAGE = "Maturity Date is not valid";
    public static final String VERSION_MESSAGE = "Incorrect version of trade received";
    @Autowired
    TradeStoreDB tradeStoreDB;

    @TrackExecutionTime
    public void validateAndSaveTrade(TradeRecord tradeRecord) throws TradeProcessingException {
        try {
            log.info("Trade Record Received {}", tradeRecord);
            var key = tradeRecord.getTradeId() + "-" + tradeRecord.getVersion();
            log.info("Extracted Key {}", key);
            if (tradeRecord.getMaturityDate().isBefore(LocalDate.now())) {
                log.info("Throwing Exception {}", key);
                throw new TradeProcessingException(DATE_MESSAGE);
            }
            var maxValue = tradeStoreDB.getTradeMap().keySet().stream()
                    .filter(s -> s.contains(tradeRecord.getTradeId()))
                    .map(s -> s.substring(s.indexOf("-")+1))
                    .map(Integer::valueOf)
                    .collect(Collectors.summarizingInt(Integer::intValue)).getMax();
            log.info("Max value of version {}",maxValue );
            if (maxValue>tradeRecord.getVersion()){
                log.info("Throwing Exception {}", key);
                throw new TradeProcessingException(VERSION_MESSAGE);
            }
            tradeStoreDB.persistRecord(tradeRecord);
        } catch (TradeProcessingException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @TrackExecutionTime
    public List<TradeRecord> getAllTrades() {
        return tradeStoreDB.getAllTrades();
    }
}
