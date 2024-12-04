package com.api.reservationsimulator.Controller;

import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.reservationsimulator.Config.RabbitMQConfig;
import com.api.reservationsimulator.DTO.ReservationOrderDTO;
import com.api.reservationsimulator.Manager.QueueManager;
import com.api.reservationsimulator.Manager.SeatManager;
import com.api.reservationsimulator.Service.ReservationOrderService;
import com.api.reservationsimulator.Service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;




@RestController
public class ReservationController {

    @Autowired
    CacheManager cacheManager;
    @Autowired
    ReservationService reservationService;

    @PostMapping("/purge")
    public int purge(){
        return QueueManager.purgeQueue(RabbitMQConfig.RESERVATION_ORDER_QUEUE);
    }
    // 예약 대기열 신청
    // Producer
    @PostMapping("/reserve")
    public ReservationOrderDTO reserve(@RequestParam("userId") String userId) throws JsonProcessingException, AmqpException {
        String reservationId = UUID.randomUUID().toString();
        int total = 1 + QueueManager.getPlusCount()+QueueManager.getMinusCount();
        int isPass = (total <= ReservationOrderService.ALLOCATED_NUMBER)? 0 : 1;
        ReservationOrderDTO dto = new ReservationOrderDTO(
                                        userId,
                                        reservationId, 
                                        total,
                                        isPass
                                        );
        cacheManager.getCache("RESERVATION_ID").put(reservationId, dto);
        QueueManager.sendMessage(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ORDER_ROUTING_KEY, dto);
        
        return dto;
    }
    // 순번 체크
    @PostMapping("/order")
    public String order(@RequestParam("reservationId") String reservationId) {
        ReservationOrderDTO dto = cacheManager.getCache("RESERVATION_ID").get(reservationId,ReservationOrderDTO.class);
        int isPass = dto.getIsPass();
        int order = dto.getOrder();
        int pCount = QueueManager.getPlusCount();
        int mCount = QueueManager.getMinusCount();
        int curOrder = order+mCount-ReservationOrderService.ALLOCATED_NUMBER;
        // System.out.println(reservationId +" : "+curOrder+","+isPass);
        if(curOrder <= 0) isPass = 0;
        int[] temp = new int[]{ pCount+mCount , curOrder > 0 ? curOrder : 0,isPass};
        return new Gson().toJson(temp);
    }
    // 예약 리퀘
    @PostMapping("/submitSeat")
    public String submitSeat(@RequestParam("userId") String userId,@RequestParam("seatId") long seatId) throws JsonProcessingException {

        boolean isSuccess = reservationService.submitReservation(userId,seatId);
        if(!isSuccess){
            return "1;"+new Gson().toJson(SeatManager.seatList);
        }else{
            ReservationOrderService.decreseCurNum();
            QueueManager.decreasaeCount();
            return "0;"+new Gson().toJson(SeatManager.seatList);
        }
    }   
    @PostMapping("/seat")
    public String seat(){
        return new Gson().toJson(SeatManager.seatList);
    }
}
