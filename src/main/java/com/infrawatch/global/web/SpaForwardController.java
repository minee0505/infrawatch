package com.infrawatch.global.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 프론트엔드는 주소를 브라우저에서 직접 바꾸는 방식이라 서버에는 해당 경로가 없다.
 * 새로고침 시 서버가 404 를 주지 않도록 화면 주소는 index.html 로 넘긴다.
 */
@Controller
public class SpaForwardController {

    @GetMapping({
            "/login",
            "/dashboard",
            "/facilities",
            "/facilities/{facilityId}",
            "/alerts",
            "/members",
            "/audit-logs"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
