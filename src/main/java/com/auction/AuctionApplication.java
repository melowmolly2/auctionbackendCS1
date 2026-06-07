package com.auction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the auction system.
 */
@SpringBootApplication
@EnableScheduling
public class AuctionApplication {

  private static final Logger log = LoggerFactory.getLogger(AuctionApplication.class);

  /**
   * Main method to start the auction application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    System.out.println("Hello Bob the Builder");
    log.info("Starting AuctionApplication");
    SpringApplication.run(AuctionApplication.class, args);
  }
}
