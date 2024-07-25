package com.api.reservationsimulator.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.api.reservationsimulator.Config.RabbitMQConfig;
import com.api.reservationsimulator.Manager.QueueManager;
import com.api.reservationsimulator.Manager.SeatManager;


@Controller
public class MainController {
    
    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("seatList",SeatManager.seatList);
        return mav;
    }
    
    @PostMapping("/purge")
    public int purge(){
        return QueueManager.purgeQueue(RabbitMQConfig.RESERVATION_ORDER_QUEUE);
    }
}
