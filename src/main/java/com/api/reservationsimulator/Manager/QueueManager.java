package com.api.reservationsimulator.Manager;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.api.reservationsimulator.Config.RabbitMQConfig;
import com.api.reservationsimulator.DTO.ReservationOrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

@Component
public class QueueManager {
    public final static int ALLOCATED_NUMBER = 3;
    public final static int ALLOCATED_TIME = 1000 * 60 * 5;
    private static RabbitAdmin rabbitAdmin;
    private static RabbitTemplate rabbitTemplate;
    private static AtomicInteger plusCount; 
    private static AtomicInteger minusCount; 

    public QueueManager(RabbitAdmin rabbitAdmin, RabbitTemplate rabbitTemplate) {
        QueueManager.rabbitAdmin = rabbitAdmin;
        QueueManager.rabbitTemplate = rabbitTemplate;
        plusCount = new AtomicInteger(0);
        minusCount = new AtomicInteger(0);
       
        QueueManager.purgeQueue(RabbitMQConfig.RESERVATION_ORDER_QUEUE);
    }

    public static void sendMessage(String exchange, String routingKey, ReservationOrderDTO dto) throws JsonProcessingException, AmqpException {
        rabbitTemplate.convertAndSend(exchange, routingKey, new Gson().toJson(dto));

        QueueManager.increaseCount();
    }
    public static int getPlusCount() {
        return plusCount.get();
    }
    public static int getMinusCount(){
        return minusCount.get();
    }
    public static void increaseCount(){
        plusCount.incrementAndGet();
    }
    public static void decreasaeCount(){
        minusCount.decrementAndGet();
    }

    public static int purgeQueue(String queueName){
        System.out.println("[QueueManager purgeQueue] PROCESSING PurgeQueue : " + queueName + "...");
        
        rabbitAdmin.purgeQueue(queueName,false);
        int deletedCount = plusCount.get() - minusCount.get();
        plusCount = new AtomicInteger(0);
        minusCount = new AtomicInteger(0);
        
        System.out.println("[QueueManager purgeQueue] COMPLETE   PurgeQueue : " + queueName+" / "+deletedCount+"개 비워짐");
        
        return deletedCount;
    }
}
