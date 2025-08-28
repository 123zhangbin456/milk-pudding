package com.milkpudding.user.controller;

import cn.hutool.core.util.IdUtil;
import com.milkpudding.user.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @GetMapping("/{id}")
    public User findById(@PathVariable String id) {
        return User.builder()
                .id(id)
                .name("Milk")
                .email("milk@pudding.dev")
                .build();
    }

    @PostMapping
    public User create(@Valid @RequestBody CreateUserReq req) {
        String id = IdUtil.fastSimpleUUID();
        log.info("create user: {}", req);
        return User.builder()
                .id(id)
                .name(req.getName())
                .email(req.getEmail())
                .build();
    }

    @Data
    public static class CreateUserReq {
        @NotBlank
        private String name;
        @Email
        private String email;
    }
}
