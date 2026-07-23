package com.IBM.ClinicManagementSystem.Controllers.Rest.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.UserDTO;
import com.IBM.ClinicManagementSystem.Services.Security.CustomUserDetails;
import com.IBM.ClinicManagementSystem.Services.Site.UserService;
import com.IBM.ClinicManagementSystem.Utils.Helper.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userDetails.getId())));
    }

    @PutMapping("/user/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserDTO dto){
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(userDetails.getId(),dto)));
    }

    @PatchMapping(value = "/user/profile-pic",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> changeProfilePic(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("image") MultipartFile image){
        return ResponseEntity.ok(ApiResponse.success(userService.changeProfileImage(userDetails.getId(),image)));
    }

    @DeleteMapping("/user/profile")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteUserById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("Your Account is deleted to restore it again please contact admin!"));
    }
}
