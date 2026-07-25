package com.IBM.ClinicManagementSystem.Controllers.Rest.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.DoctorDTO;
import com.IBM.ClinicManagementSystem.DTOs.Site.PageDTO;
import com.IBM.ClinicManagementSystem.Services.Security.CustomUserDetails;
import com.IBM.ClinicManagementSystem.Services.Site.DoctorService;
import com.IBM.ClinicManagementSystem.Utils.Helper.ApiResponse;
import com.IBM.ClinicManagementSystem.Utils.Helper.Helper;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageDTO<DoctorDTO>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = Helper.pageHandler(page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.success(doctorService.searchDoctors(name,specialization,pageable)));

    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<ApiResponse<DoctorDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(doctorService.getById(id)));
    }
    @PutMapping("/doctor-details")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorDTO>> updateDoctor(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody DoctorDTO doctorDTO){
        return ResponseEntity.ok(ApiResponse.success(doctorService.updateDoctorDetails(userDetails.getId(),doctorDTO)));
    }
}
