package com.fasthub.backend.user.similar.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmbeddingResponse {
    private float[] embedding;
}
