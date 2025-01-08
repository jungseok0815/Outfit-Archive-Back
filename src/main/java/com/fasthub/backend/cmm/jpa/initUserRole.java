package com.fasthub.backend.cmm.jpa;

import com.fasthub.backend.cmm.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class initUserRole implements CommandLineRunner {
//
//    private final RoleRepository roleRepository;
//
//    @Override
//    public void run(String... args) {
//        Arrays.stream(UserRole.values()).forEach(role -> {
//            if (!roleRepository.existsByName(role)){
//                roleRepository.save(new UserRoleEntity(role));
//            }
//        });
//    }
//}