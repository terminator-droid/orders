package com.dudev.orderservice.service;

import com.dudev.orderservice.dto.CreateUserDto;
import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.dto.FullUserDto;
import com.dudev.orderservice.model.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService extends UserDetailsService {
    boolean existsByUsername(String username);

    Optional<UserDto> findById(UUID id);

    List<UserDto> findAll();

    FullUserDto getCurrentUser();

    void grantAdminRole(UUID userId);

    UserDto createUser(CreateUserDto createUserDto, Role role);

    void delete(UUID id);

    UserDto findByUsername(String username);

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Page<UserDto> findAllPageable(int page, int size, String sort);
}