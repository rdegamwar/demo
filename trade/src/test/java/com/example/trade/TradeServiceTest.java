package com.example.trade;


import com.example.trade.dto.TradeRecord;
import com.example.trade.service.TradeService;
import com.example.trade.store.TradeProcessingException;
import com.example.trade.store.TradeStoreDB;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TradeServiceTest {
    @Autowired
    TradeService tradeService;

    @Autowired
    TradeStoreDB tradeStoreDB;

    @BeforeAll
    void setupDataStore() throws Exception {
        Assertions.assertEquals(0, tradeStoreDB.count());

        log.info("#### Setting Up Data #####");
        tradeStoreDB.clearAllData();
        TradeRecord T1 = TradeRecord.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-1")
                .bookId("B1")
                .maturityDate(LocalDate.of(2023, 5, 20))
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        tradeStoreDB.persistRecord(T1);

        TradeRecord T2 = TradeRecord.builder()
                .tradeId("T2")
                .version(1)
                .counterPartyId("CP-1")
                .bookId("B1")
                .maturityDate(LocalDate.of(2024, 5, 20))
                .createdDate(LocalDate.of(2015, 3, 14))
                .isExpired(false)
                .build();
        tradeStoreDB.persistRecord(T2);

        TradeRecord T3 = TradeRecord.builder()
                .tradeId("T2")
                .version(2)
                .counterPartyId("CP-2")
                .bookId("B1")
                .maturityDate(LocalDate.of(2023, 5, 20))
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        tradeStoreDB.persistRecord(T3);

        TradeRecord T4 = TradeRecord.builder()
                .tradeId("T3")
                .version(3)
                .counterPartyId("CP-3")
                .bookId("B2")
                .maturityDate(LocalDate.of(2015, 5, 20))
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        tradeStoreDB.persistRecord(T4);

        Assertions.assertEquals(4, tradeStoreDB.count());
    }

    /**
     * If the version is same it will override the existing record.
     */
    @Test
    public void testUpdateExistingRecord() throws Exception {
        var countBefore = tradeStoreDB.count();
        TradeRecord t = TradeRecord.builder()
                .tradeId("T1")
                .version(1)
                .counterPartyId("CP-11234")
                .bookId("B1234")
                .maturityDate(LocalDate.of(2023, 5, 20))
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        tradeService.validateAndSaveTrade(t);
        //retrieve and validate
        TradeRecord t1 = tradeStoreDB.getTrade(t.getTradeId() + "-" + t.getVersion());
        Assertions.assertEquals(1, t1.getVersion());
        Assertions.assertEquals("CP-11234", t1.getCounterPartyId());
        Assertions.assertEquals("B1234", t1.getBookId());
        var countAfter = tradeStoreDB.count();
        log.info("Trade Map Size {}", countAfter);
        Assertions.assertEquals(countAfter, countBefore);
    }

    /**
     * Store should not allow the trade which has less maturity date than today date.
     */
    @Test
    public void testMaturityDate() {
        TradeRecord t = TradeRecord.builder()
                .tradeId("T5")
                .version(1)
                .counterPartyId("CP-11")
                .bookId("B12")
                .maturityDate(LocalDate.of(2020, 5, 20)) //yesterday
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        Exception exception= null;
        try {
            tradeService.validateAndSaveTrade(t);
        } catch (TradeProcessingException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(TradeService.DATE_MESSAGE, exception.getMessage());
        var countAfter = tradeStoreDB.count();
        log.info("Trade Map Size {}", countAfter);
        Assertions.assertEquals(4, countAfter);
    }
    /**
     * During transmission if the lower version is being received by the store it will reject the trade and
     * throw an exception.
     */
    @Test
    public void testLowerVersion() {
        TradeRecord t = TradeRecord.builder()
                .tradeId("T2")
                .version(1)
                .counterPartyId("CP-11")
                .bookId("B12")
                .maturityDate(LocalDate.of(2023, 5, 20)) //yesterday
                .createdDate(LocalDate.now())
                .isExpired(false)
                .build();
        Exception exception= null;
        try {
            tradeService.validateAndSaveTrade(t);
        } catch (TradeProcessingException e) {
            exception = e;
        }
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(TradeService.VERSION_MESSAGE, exception.getMessage());
        var countAfter = tradeStoreDB.count();
        log.info("Trade Map Size {}", countAfter);
        Assertions.assertEquals(4, countAfter);
    }

    /**
     * cron schedule java code is tested here. we have one expired record which should be found by the cron
     */

    @Test
    public void cronJobSch() {
        log.info("Updating Expired Trades");
        var record = tradeStoreDB.getTradeMap().values().stream()
                .filter(s -> s.getMaturityDate().isBefore(LocalDate.now()))
                .count();
        log.info("count : " + record);
        Assertions.assertEquals(1,record);
    }
}
