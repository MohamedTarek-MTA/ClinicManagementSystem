package com.IBM.ClinicManagementSystem.Controllers.Rest.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.PageDTO;
import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Models.Entities.User;
import com.IBM.ClinicManagementSystem.Services.Security.CustomUserDetails;
import com.IBM.ClinicManagementSystem.Services.Site.UserService;
import com.IBM.ClinicManagementSystem.Utils.Helper.ApiResponse;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageDTO<UserDTO>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) User.Gender gender,
            @RequestParam(required = false) User.Status status,
            @RequestParam(required = false) User.Role role,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction){
        Pageable pageable = Helper.pageHandler(page,size,sortBy,direction);
        return ResponseEntity.ok(ApiResponse.success("",userService.searchUsers(name,address,gender,status,role,pageable)));

    }
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userDetails.getId())));
    }
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }
    @GetMapping("/user/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@RequestParam String email){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByEmail(email)));
    }
    @GetMapping("/user/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhone(@RequestParam String phone){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByPhone(phone)));
    }

    @PatchMapping("/change-status/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> changeUserStatus(@PathVariable Long id, @RequestParam(required = true) User.Status status){
        return ResponseEntity.ok(ApiResponse.success(userService.changeUserStatus(id,status)));
    }
    @PatchMapping("/change-role/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> changeUserRole(@PathVariable Long id, @RequestParam(required = true) User.Role role){
        return ResponseEntity.ok(ApiResponse.success(userService.changeUserRole(id,role)));
    }

    @PutMapping("/user/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserDTO dto){
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(userDetails.getId(),dto)));
    }

    @PatchMapping(value = "/user/profile-pic",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> changeProfilePic(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("image") MultipartFile image){
        return ResponseEntity.ok(ApiResponse.success(userService.changeProfileImage(userDetails.getId(),image)));
    }
    @PatchMapping("/activate-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success("User Activated Successfully!",userService.enableUserById(id)));
    }
    @PatchMapping("/ban-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> banUser(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success("User Banned Successfully!",userService.disableUserById(id)));
    }
    @DeleteMapping("/user/profile")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteUserById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Your Account is deleted to restore it again please contact admin!"));
    }
}
