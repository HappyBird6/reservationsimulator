package com.api.reservationsimulator.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.api.reservationsimulator.Entity.Seat;

import jakarta.annotation.PostConstruct;

@Component
public class SeatManager {

    public static HashMap<Long, Seat> seatMap;
    public static List<Seat> seatList;
    public SeatManager() {
        seatMap = new HashMap<>();
        seatList = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        seatList.add(new Seat(10001l,"A",100,0));
        seatList.add(new Seat(10002l,"B",200,0));
        seatList.add(new Seat(10003l,"C",500,0));
        this.loadSeatData();
    }

    public void loadSeatData() {
        // DB에서 불러오기 > seats 업데이트
        for (Seat seat : seatList) {
            if (seatMap.containsKey(seat.getSeatId())) {
                seatMap.replace(seat.getSeatId(), seat);
            } else {
                seatMap.put(seat.getSeatId(), seat);
            }
        }
    }
}