package com.fasthub.backend.admin.product.event;

import java.util.List;

public record BulkProductSavedEvent(List<Long> productIds) {}
