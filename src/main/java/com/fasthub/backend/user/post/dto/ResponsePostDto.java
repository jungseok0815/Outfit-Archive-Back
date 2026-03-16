package com.fasthub.backend.user.post.dto;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.user.post.entity.Post;
import com.fasthub.backend.user.post.entity.PostImg;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ResponsePostDto {

    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userNm;
    private String profileImgNm;
    private List<PostImg> images;
    private List<ProductSummary> products;
    private long likeCount;
    private long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResponsePostDto(Post post, long likeCount, long commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUser().getId();
        this.userNm = post.getUser().getUserNm();
        this.profileImgNm = post.getUser().getProfileImgNm();
        this.images = post.getImages();
        this.products = post.getPostProducts().stream()
                .map(pp -> new ProductSummary(pp.getProduct()))
                .toList();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    @Getter
    public static class ProductSummary {
        private final Long id;
        private final String productNm;
        private final int productPrice;
        private final ProductCategory category;
        private final String brandNm;

        public ProductSummary(Product product) {
            this.id = product.getId();
            this.productNm = product.getProductNm();
            this.productPrice = product.getProductPrice();
            this.category = product.getCategory();
            this.brandNm = product.getBrand().getBrandNm();
        }
    }
}
