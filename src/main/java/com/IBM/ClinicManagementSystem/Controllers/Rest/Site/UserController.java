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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/vi/users")
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
}
