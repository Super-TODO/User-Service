package com.spring.userservice.service;

import com.spring.userservice.Repository.UserRepository;
import com.spring.userservice.dto.UserRequestDTO;
import com.spring.userservice.dto.UserResponseDTO;
import com.spring.userservice.entity.User;
import com.spring.userservice.utils.PageResponse;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponseDTO addUser(UserRequestDTO request) {
        User user = modelMapper.map(request, User.class);
        user.setEnabled(true);
        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));

        if (request.getUsername() != null) {
            existing.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            existing.setPassword(request.getPassword());
        }

        User updated = userRepository.save(existing);
        return modelMapper.map(updated, UserResponseDTO.class);
    }


    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
        userRepository.delete(user);
    }

    public PageResponse<UserResponseDTO> findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return PageResponse.<UserResponseDTO>builder()
                .content(users.map(user -> modelMapper.map(user, UserResponseDTO.class)).getContent())
                .pageNumber(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .build();
    }
}
