package com.IBM.ClinicManagementSystem.Services.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.PageDTO;
import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Mappers.Site.PageMapper;
import com.IBM.ClinicManagementSystem.Mappers.Site.UserMapper;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Repositories.Mysql.UserRepository;
import com.IBM.ClinicManagementSystem.Utils.User.UserSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        return  userRepository.existsByEmail(email);
    }
    public Boolean userExistsByPhone(String phone){
        return userRepository.existsByPhone(phone);
    }
    @Cacheable(value = "usersById",key = "#id")
    public UserDTO getUserById(Long id){
        return userMapper.toDTO(
                userRepository.findById(id).orElseThrow(()
                        -> new IllegalArgumentException("User Not Found !")));
    }

    public User getUserEntityByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()
                -> new IllegalArgumentException("User or Email are Not Found !"));
    }
    @Cacheable(value = "usersByEmail",key = "#email")
    public UserDTO getUserByEmail(String email){
        return userMapper.toDTO(getUserEntityByEmail(email));
    }
    @Cacheable(value = "usersByPhone",key = "#phone")
    public UserDTO getUserByPhone(String phone){
        return userMapper.toDTO(
                userRepository.findByPhone(phone).orElseThrow(()
                        -> new IllegalArgumentException("User or Phone Number are Not Found !")));
    }
    @Cacheable(
            value = "usersPage",
            key = "{" +
                    "#name, " +
                    "#address, " +
                    "#gender != null ? #gender.name() : 'ALL', " +
                    "#status != null ? #status.name() : 'ALL', " +
                    "#role != null ? #role.name() : 'ALL', " +
                    "#pageable.pageNumber, " +
                    "#pageable.pageSize, " +
                    "#pageable.sort.toString()" +
                    "}"
    )    public PageDTO<UserDTO> searchUsers(
                                    String name,
                                   String address,
                                   User.Gender gender,
                                   User.Status status,
                                   User.Role role,
                                   Pageable pageable
    ){
        Specification<User> specification =
                UserSpecifications.filterUsers(name,address,gender,status,role);
        return PageMapper.toDTO(
                userRepository.findAll(specification,pageable).map(userMapper::toDTO));
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null"),
            @CacheEvict(value = "usersByPhone", key = "#result.phone", condition = "#result != null")
    })
    public UserDTO changeUserStatus(Long id, User.Status status){
        var user = getUserEntityById(id);
        Optional.ofNullable(status).ifPresent(user::setStatus);
        if (user.getStatus().equals(User.Status.ACTIVE)) {
            user.setEnabled(true);
        } else {
            user.setEnabled(false);
        }
        return userMapper.toDTO(userRepository.saveAndFlush(user));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null"),
            @CacheEvict(value = "usersByPhone", key = "#result.phone", condition = "#result != null")
    })
    public UserDTO enableUserById(Long id){
        var user = getUserEntityById(id);
        user.setStatus(User.Status.ACTIVE);
        user.setEnabled(true);
       return userMapper.toDTO(userRepository.saveAndFlush(user));
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null"),
            @CacheEvict(value = "usersByPhone", key = "#result.phone", condition = "#result != null")
    })
    public UserDTO disableUserById(Long id){
        var user = getUserEntityById(id);
        user.setStatus(User.Status.BANNED);
        user.setEnabled(false);
       return userMapper.toDTO(userRepository.saveAndFlush(user));
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null"),
            @CacheEvict(value = "usersByPhone", key = "#result.phone", condition = "#result != null")
    })
    public UserDTO changeUserRole(Long id, User.Role role){
        var user = getUserEntityById(id);
        Optional.ofNullable(role).ifPresent(user::setRole);
        return userMapper.toDTO(userRepository.saveAndFlush(user));
    }
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersPage", allEntries = true),
            @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
            @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null"),
            @CacheEvict(value = "usersByPhone", key = "#result.phone", condition = "#result != null")
    })
    public User saveUser(User user){
        return userRepository.saveAndFlush(user);
    }


}
