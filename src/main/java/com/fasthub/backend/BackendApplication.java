package com.fasthub.backend;

import com.fasthub.backend.oper.auth.entity.User;
import com.fasthub.backend.oper.board.entity.Board;
import com.fasthub.backend.oper.product.entity.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
