package com.dudev.orderservice.service.impl;

import com.dudev.orderservice.dto.CreateUserDto;
import com.dudev.orderservice.dto.UserDto;
import com.dudev.orderservice.dto.FullUserDto;
import com.dudev.orderservice.exception.UserNotFoundException;
import com.dudev.orderservice.mapper.UserMapper;
import com.dudev.orderservice.model.enums.Role;
import com.dudev.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements com.dudev.orderservice.service.UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserDto> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public FullUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority role = authentication.getAuthorities().stream().findFirst().orElseThrow();
        UserDto userDto = findByUsername(((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername());
        return FullUserDto.builder()
                .id(userDto.getId())
                .role(role.getAuthority())
                .username(userDto.getUsername())
                .build();
    }

    @Transactional
    @Override
    public void grantAdminRole(UUID userId) {
        com.dudev.orderservice.model.User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserDto createUser(CreateUserDto createUserDto, Role role) {
        return Optional.of(createUserDto)
                .map(user -> {
                    com.dudev.orderservice.model.User entity = userMapper.toEntity(user);
                    entity.setRole(role);
                    entity.setPassword(passwordEncoder.encode(user.getPassword()));
                    return entity;
                })
                .map(userRepository::save)
                .map(userMapper::toDto)
                .orElseThrow();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    userRepository.flush();
                    return true;
                });
    }

    @Override
    public UserDto findByUsername(String username) {
        return userRepository.getUserByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByUsername(username)
                .map(user -> new User(user.getUsername(),
                        user.getPassword(), Collections.singleton(user.getRole())))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user " + username));
    }

    @Override
    public Page<UserDto> findAllPageable(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);  // User → UserDto
    }
}
