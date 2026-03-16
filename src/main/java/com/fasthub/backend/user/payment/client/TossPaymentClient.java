package com.fasthub.backend.user.payment.client;

import com.fasthub.backend.cmm.error.ErrorCode;
import com.fasthub.backend.cmm.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@Component
@Slf4j
public class TossPaymentClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com/v1/payments";

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentClient(@Value("${toss.secret-key}") String secretKey) {
        this.secretKey = secretKey;
        this.restClient = RestClient.create();
    }

    // 결제 승인
    public void confirm(String paymentKey, String orderId, int amount) {
        try {
            restClient.post()
                    .uri(TOSS_BASE_URL + "/confirm")
                    .header("Authorization", basicAuth())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "paymentKey", paymentKey,
                            "orderId", orderId,
                            "amount", amount
                    ))
                    .retrieve()
                    .toBodilessEntity();
            log.info("[Toss] 결제 승인 완료 orderId={}, amount={}", orderId, amount);
        } catch (Exception e) {
            log.error("[Toss] 결제 승인 실패 orderId={}, error={}", orderId, e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_CONFIRM_FAIL);
        }
    }

    // 결제 취소
    public void cancel(String paymentKey, String cancelReason) {
        try {
            restClient.post()
                    .uri(TOSS_BASE_URL + "/" + paymentKey + "/cancel")
                    .header("Authorization", basicAuth())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancelReason", cancelReason))
                    .retrieve()
                    .toBodilessEntity();
            log.info("[Toss] 결제 취소 완료 paymentKey={}", paymentKey);
        } catch (Exception e) {
            log.error("[Toss] 결제 취소 실패 paymentKey={}, error={}", paymentKey, e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_CANCEL_FAIL);
        }
    }

    // Basic Auth: Base64(secretKey + ":")
    private String basicAuth() {
        String credentials = secretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
