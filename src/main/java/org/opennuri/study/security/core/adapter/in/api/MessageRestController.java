package org.opennuri.study.security.core.adapter.in.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MessageRestController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/api/messages")
    @ResponseBody
    public ResponseEntity<String> getMessages() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Hello, World1");
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(result));
    }
}
