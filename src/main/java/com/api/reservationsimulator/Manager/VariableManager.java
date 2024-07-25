// package com.api.reservationsimulator.Manager;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.stereotype.Component;

// import com.api.reservationsimulator.Service.ReservationOrderService;

// @Component
// public class VariableManager {
//     public static enum Variable{
//         ALLOCATED_NUMBER,
//         ALLOCATED_TIME,
//     }
//     // ReservationService
//     public static int ALLOCATED_NUMBER = 3;
//     public static int ALLOCATED_TIME = 1000 * 60 * 5;
    
    


//     public static void update(){
//         ReservationOrderService.updateVariable();
//     }
//     public static String getVariableToString(Variable v){
//         return v.toString();
//     }
//     public static int getVariableValue(Variable v){
//         switch(v){
//             case ALLOCATED_NUMBER:
//                 return ALLOCATED_NUMBER;
//             case ALLOCATED_TIME:
//                 return ALLOCATED_TIME;
//             default:
//                 return -1;
//         }
//     }
//     public static Map<String,Integer> getVariables(){
//         Variable[] v = Variable.values();
//         Map<String,Integer> map= new HashMap<>();
//         for(int i = 0; i < v.length; i++){
//             map.put(getVariableToString(v[i]),getVariableValue(v[i]));
//         }
//         return map;
//     }
// }
