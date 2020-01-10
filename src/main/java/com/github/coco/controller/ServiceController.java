package com.github.coco.controller;

import com.github.coco.util.JwtUtil;
import com.github.coco.vo.StatisticsVO;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/statistics")
    public StatisticsVO loadStatistics() {
        // TODO You should select data from database
        StatisticsVO sv = new StatisticsVO();
        sv.setTodayServed(10);
        sv.setTodayOnline(90);
        sv.setTodayScore(4.6);
        sv.setTotalServed(85);
        sv.setTotalOnline(925);
        sv.setTotalScore(4.4);
        return sv;
    }
}