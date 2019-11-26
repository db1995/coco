package com.github.coco.controller;

import com.github.coco.util.JwtUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for service
 *
 * @author db1995
 */
@RestController
@RequestMapping("/service")
public class ServiceController {
    @PostMapping("/login")
    public String login(String username, String password) {
        // TODO Maybe you should use your database to replace this
        if (("test1@coco.com".equals(username) || "test2@coco.com".equals(username) || "test3@coco.com".equals(username))
                && "123456".equals(password)) {
            return JwtUtil.generateToken(username);
        }
        return "";
    }
}