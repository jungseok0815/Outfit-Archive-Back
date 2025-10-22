package com.fasthub.backend.cmm;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class CmmController {
    @RequestMapping("/*/*")
    public ResponseEntity<?> handleAllRequests(
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        log.info("Request received - Method: {}, Path: {}", method, path);
        log.info("Request Body: {}", body);
        // 여기서 라우팅 로직 구현
        // 예: path와 method에 따라 적절한 서비스 호출
        return ResponseEntity.ok("Request processed");
    }
}
