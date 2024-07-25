package com.api.reservationsimulator.Enum;

import lombok.Getter;

@Getter
public enum CacheType {

    ReservationID("RESERVATION_ID",3 * 60 * 60 ,10 * 10000);
    // SeatStatus("SEAT_STATUS",3 * 60 * 60,10);

    private String cacheName;
    private int expiredAfterWrite;
    private int maximumSize;

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize){
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize; 
    }
}
