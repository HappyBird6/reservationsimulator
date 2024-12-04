// package com.api.reservationsimulator.Service;

// import org.openqa.selenium.By;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import io.github.bonigarcia.wdm.WebDriverManager;

// @Service
// public class ReservationTestService {

//     private final String url = "http://localhost:8080";

//     @Async("taskExecutor")
//     public void testReservation() {
//         System.out.println("[" + Thread.currentThread().getName() + "] : 스레드 시작");
//         WebDriverManager.chromedriver().setup();
//         ChromeOptions chromeOptions = new ChromeOptions();
//         chromeOptions.addArguments("--headless");
//         chromeOptions.addArguments("--no-sandbox");
//         chromeOptions.addArguments("--single-process");
//         chromeOptions.addArguments("--disable-dev-shm-usage");
//         ChromeDriver driver = new ChromeDriver(chromeOptions);
//         System.out.println("[" + Thread.currentThread().getName() + "] : 크롬 드라이버 셋업 완료");
        
//         // System.out.println("["+Thread.currentThread().getName()+"] :");
//         try {
//             driver.get(url);
//             System.out.println("[" + Thread.currentThread().getName() + "] : 페이지 접속");
            
//             int waiting = (int)(Math.random()*100);
//             System.out.println("[" + Thread.currentThread().getName() + "] : "+waiting+"ms 대기");
//             Thread.sleep(waiting);
            
//             WebElement element = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div"));
//             element.click();
//             System.out.println("["+Thread.currentThread().getName()+"] : 예약버튼 클릭");
            
//         } catch (Exception e) {
//             System.out.println("["+Thread.currentThread().getName()+"] : 에러 발생!!! "+e.getMessage());
//         }

//     }
// }
