package com.api.reservationsimulator.DTO;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationOrderDTO{
    private String userId;
    private String reservationId;
    private int order;
    private int isPass; // 0이면 pass, 1이면 nonpass
}
