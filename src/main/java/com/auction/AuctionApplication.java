package com.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AuctionApplication {
  // hello!!
  public static void main(String[] args) {
    System.out.println("Hello");
    SpringApplication.run(AuctionApplication.class, args);
  }

  @GetMapping("/hello")
  public Integer hello(@RequestParam() Integer a, Integer b) {
    return (a + b);
  }
}
