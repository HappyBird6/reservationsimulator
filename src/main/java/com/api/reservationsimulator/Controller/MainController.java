package com.api.reservationsimulator.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.api.reservationsimulator.Manager.SeatManager;
// import com.api.reservationsimulator.Service.AsyncCallerService;


@Controller
public class MainController {
    // @Autowired
    // AsyncCallerService asyncCallerService;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("seatList",SeatManager.seatList);
        // asyncCallerService.setTest(1);
        return mav;
    }
}
