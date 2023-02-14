package com.example.trade.store;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TradeProcessingException extends Exception{

    private String message;
    public TradeProcessingException (String message){
        this.message = message;
    }
}
