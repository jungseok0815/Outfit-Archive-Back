package com.fasthub.backend.oper.product.mapper;

import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.ResponseProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.product.entity.ProductImg;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-07T16:36:32+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ResponseProductDto productToProductDto(Product product) {
        if ( product == null ) {
            return null;
        }

        ResponseProductDto responseProductDto = new ResponseProductDto();

        responseProductDto.setProductNm( product.getProductNm() );
        responseProductDto.setProductCode( product.getProductCode() );
        responseProductDto.setProductPrice( product.getProductPrice() );
        responseProductDto.setProductQuantity( product.getProductQuantity() );
        responseProductDto.setCategory( product.getCategory() );
        List<ProductImg> list = product.getImages();
        if ( list != null ) {
            responseProductDto.setImages( new ArrayList<ProductImg>( list ) );
        }

        return responseProductDto;
    }

    @Override
    public Product productDtoToProduct(UpdateProductDto productDto) {
        if ( productDto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.id( productDto.getId() );
        product.productNm( productDto.getProductNm() );
        product.productCode( productDto.getProductCode() );
        product.productPrice( productDto.getProductPrice() );
        product.productQuantity( productDto.getProductQuantity() );
        product.category( productDto.getCategory() );

        return product.build();
    }

    @Override
    public Product InsertproductDtoToProduct(InsertProductDto productDto) {
        if ( productDto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.productNm( productDto.getProductNm() );
        product.productCode( productDto.getProductCode() );
        product.productPrice( productDto.getProductPrice() );
        product.productQuantity( productDto.getProductQuantity() );
        product.category( productDto.getCategory() );

        return product.build();
    }
}
