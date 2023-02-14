package com.example.trade.controller;

import com.example.trade.aspect.TrackExecutionTime;
import com.example.trade.dto.TradeRecord;
import com.example.trade.service.TradeService;
import com.example.trade.store.TradeProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The trade transmission end point is provided by this controller. Only trade posting api is exposed over POST.
 * The get API is to demonstrate that the data is stored in order as we don't have backed access to show data
 *
 */

@RestController
@Slf4j
public class TradeController {

    @Autowired
    TradeService tradeService;

    @TrackExecutionTime
    @RequestMapping (method= RequestMethod.POST, value="/transmitTrade")
    public void transmitTrade (@RequestBody TradeRecord tradeRecord) {
        try {
            tradeService.validateAndSaveTrade(tradeRecord);
        } catch (TradeProcessingException e) {
            log.error(e.getMessage(),e);
        }
    }
    @TrackExecutionTime
    @RequestMapping (method= RequestMethod.GET, value="/getTradesInOrder")
    public List<TradeRecord> getAllTrades () {
        return tradeService.getAllTrades();
    }
}
