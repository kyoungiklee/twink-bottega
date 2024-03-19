package org.opennuri.study.security.core.adapter.in.web.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/messages")
    public String message() {
        return "front/pages/messages";
    }

    @GetMapping("/config")
    public String config() {
        return "front/pages/config";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "front/pages/mypage";
    }
}
