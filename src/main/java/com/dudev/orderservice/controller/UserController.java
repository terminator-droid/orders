package com.dudev.orderservice.controller;

import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    public List<UserDto> findAllUsers(){
        return userService.findAll();
    }

    @DeleteMapping( "/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }
}
