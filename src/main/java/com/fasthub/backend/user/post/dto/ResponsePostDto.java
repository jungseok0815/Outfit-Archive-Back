package com.fasthub.backend.user.post.dto;

import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.product.entity.Product;
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
    private boolean liked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ResponsePostDto(Post post, long likeCount, long commentCount, boolean liked) {
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
        this.liked = liked;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    @Getter
    public static class ProductSummary {
        private final Long id;
        private final String productNm;
        private final int productPrice;
        private final ResponseCategoryDto category;
        private final String brandNm;

        public ProductSummary(Product product) {
            this.id = product.getId();
            this.productNm = product.getProductNm();
            this.productPrice = product.getProductPrice();
            this.category = toCategoryDto(product.getCategory());
            this.brandNm = product.getBrand() != null ? product.getBrand().getBrandNm() : null;
        }

        private static ResponseCategoryDto toCategoryDto(Category category) {
            if (category == null) return null;
            ResponseCategoryDto dto = new ResponseCategoryDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setKorName(category.getKorName());
            dto.setEngName(category.getEngName());
            return dto;
        }
    }
}
