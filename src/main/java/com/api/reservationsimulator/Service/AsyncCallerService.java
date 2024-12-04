// package com.api.reservationsimulator.Service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// @Service
// public class AsyncCallerService {
//     @Autowired
//     private ReservationTestService reservationTestService;

//     @Async("taskExecutor")
//     public void setTest(int num) {
//         for (int i = 0; i < num; i++) {
//             reservationTestService.testReservation();
//         }
//     }
// }
