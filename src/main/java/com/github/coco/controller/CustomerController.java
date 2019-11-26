package com.github.coco.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for customer
 *
 * @author db1995
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @GetMapping
    public String login() {
        String customerId = UUID.randomUUID().toString();
        return customerId;
    }
}