package com.fasthub.backend.user.similar.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;

@Component
@Slf4j
public class EmbeddingClient {

    @Value("${ai.embedding.url}")
    private String embeddingUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public float[] embed(String imagePath) {
        FileSystemResource resource = new FileSystemResource(Paths.get(imagePath).toFile());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        log.info("[EmbeddingClient] Python 서버 요청: {}", imagePath);
        EmbeddingResponse response = restTemplate.postForObject(embeddingUrl, request, EmbeddingResponse.class);

        if (response == null || response.getEmbedding() == null) {
            throw new RuntimeException("Python 서버로부터 벡터를 받지 못했습니다.");
        }
        return response.getEmbedding();
    }
}
