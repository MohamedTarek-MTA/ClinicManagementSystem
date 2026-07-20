package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Mappers.Site.UserMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Repositories.Mysql.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserEntityById(Long id){
        return userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("User Not Found!"));
    }
    public Boolean userExistsByEmail(String email){
        return  userRepository.findByEmail(email).isPresent();
    }
    public Boolean userExistsByPhone(String phone){
        return userRepository.findByPhone(phone).isPresent();
    }
    @Cacheable(value = "usersById",key = "#id")
    public UserDTO getUserById(Long id){
        return userMapper.INSTANCE.toDTO(
                userRepository.findById(id).orElseThrow(()
                        -> new IllegalArgumentException("User Not Found !")));
    }

    public User getUserEntityByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()
                -> new IllegalArgumentException("User or Email are Not Found !"));
    }
    @Cacheable(value = "usersByEmail",key = "#email")
    public UserDTO getUserByEmail(String email){
        return userMapper.INSTANCE.toDTO(
                userRepository.findByEmail(email).orElseThrow(()
                        -> new IllegalArgumentException("User or Email are Not Found !")));
    }
    @Cacheable(value = "usersByPhone",key = "#phone")
    public UserDTO getUserByPhone(String phone){
        return userMapper.INSTANCE.toDTO(
                userRepository.findByPhone(phone).orElseThrow(()
                        -> new IllegalArgumentException("User or Phone Number are Not Found !")));
    }
    private Page<UserDTO> getUsersByGender(User.Gender gender, Pageable pageable){
        return userRepository.findByGender(gender,pageable).map(userMapper::toDTO);
    }
    private Page<UserDTO> getUsersByStatus(User.Status status, Pageable pageable){
        return userRepository.findByStatus(status,pageable).map(userMapper::toDTO);
    }
    public Page<UserDTO> getUsersByName(String name,Pageable pageable){
        return userRepository.findByNameContainingIgnoreCase(name,pageable).map(userMapper::toDTO);
    }
    public Page<UserDTO> getUsersByAddress(String address,Pageable pageable){
        return userRepository.findByAddressContainingIgnoreCase(address,pageable).map(userMapper::toDTO);
    }
    @Transactional
    public UserDTO changeUserStatus(Long id, User.Status status, Boolean enabled){
        var user = getUserEntityById(id);
        Optional.ofNullable(status).ifPresent(user::setStatus);
        Optional.ofNullable(enabled).ifPresent(user::setEnabled);

        return userMapper.INSTANCE.toDTO(userRepository.save(user));
    }
    @Transactional
    private UserDTO changeUserRole(Long id, User.Role role){
        var user = getUserEntityById(id);
        Optional.ofNullable(role).ifPresent(user::setRole);
        return userMapper.INSTANCE.toDTO(userRepository.save(user));
    }



    public void saveUser(User user){
        userRepository.save(user);
    }
    public void activeUser(Long id){
        changeUserStatus(id, User.Status.ACTIVE,true);
    }

}
