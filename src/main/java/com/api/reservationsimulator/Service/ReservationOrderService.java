package com.api.reservationsimulator.Service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.api.reservationsimulator.Config.RabbitMQConfig;
import com.api.reservationsimulator.DTO.ReservationOrderDTO;
import com.api.reservationsimulator.Manager.QueueManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;

// 예약시 대기열 
@Service
public class ReservationOrderService {

    public static int ALLOCATED_NUMBER;
    public static int ALLOCATED_TIME;

    private static AtomicInteger currentNumber;
    @Autowired
    CacheManager cacheManager;
    
    public ReservationOrderService() {
        currentNumber = new AtomicInteger(0);
        updateVariable();
    }

    public static void updateVariable(){
        ALLOCATED_NUMBER = QueueManager.ALLOCATED_NUMBER;
        ALLOCATED_TIME = QueueManager.ALLOCATED_TIME;
    }  

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_ORDER_QUEUE)
    public void processReservationOrder(String message) throws JsonMappingException, JsonProcessingException{         
        // RabbitMQ 메세지에서 DTO 정보 추출
        ReservationOrderDTO dto = new Gson().fromJson(message, ReservationOrderDTO.class);
        String userId = dto.getUserId();
        String reservationId = dto.getReservationId();
        /*
         * 루프문 한계 설정
         * 예약에 할당된 시간 x (할당된 인원+1) 만큼 반복
         */
        int cnt = 0;
        int limit = (ALLOCATED_TIME/1000) * (ALLOCATED_NUMBER+1);
        /*
         * currentNumber : atomic.class 타입, 현재 예약중인 사람의 숫자
         * ALLOCATED_NUMBER : final static int 타입, 동시에 예약 가능한 사람의 숫자 한계치 
         */
        while(currentNumber.get() >= ALLOCATED_NUMBER){
            
            if(cnt >= limit) break;
            
            // 초당 1번 체크
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){}
            
            cnt++;
        }
        // ReservationOrderDTO(유저Id,예약Id,순서,예약 flag,seatId)
        cacheManager.getCache("RESERVATION_ID").put(reservationId, new ReservationOrderDTO(userId,reservationId,1,0));
        
        currentNumber.addAndGet(1);
    }
    
    public static int getCurNum(){
        return currentNumber.get();
    }
    public static void increaseCurNum(){
        currentNumber.incrementAndGet();
    }
    public static void decreseCurNum(){
        int temp = currentNumber.decrementAndGet();
        if(temp<0) currentNumber = new AtomicInteger(0);
    }
}