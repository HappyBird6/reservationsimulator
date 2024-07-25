package com.api.reservationsimulator.Service;

import org.springframework.stereotype.Service;

import com.api.reservationsimulator.Manager.SeatManager;

@Service
public class ReservationService {

    public boolean submitReservation(String userId, long seatId){
        // default :0 , 실패 : -1
        return SeatManager.seatMap.get(seatId).increaseNum();
    }
}
