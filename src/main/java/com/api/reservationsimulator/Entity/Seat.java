package com.api.reservationsimulator.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Seat {
    private Long seatId;
    private String seatName; 
    private int maxNum;
    private int curNum;

    public boolean increaseNum(){
        if(maxNum < curNum+1){
            return false;
        }else{
            curNum += 1;
            return true;
        }
    }
    public boolean decreaseNum(){
        if(curNum>0){
            curNum -= 1;
            return true;
        }else{
            return false;
        }
    }
    public boolean isFull(){
        if(maxNum==curNum){
            return true;
        }else{
            return false;
        }
    }
}
