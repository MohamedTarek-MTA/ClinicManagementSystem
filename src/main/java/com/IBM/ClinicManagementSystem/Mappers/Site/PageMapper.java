package com.IBM.ClinicManagementSystem.Mappers.Site;

import com.IBM.ClinicManagementSystem.DTOs.Site.PageDTO;
import org.springframework.data.domain.Page;

public class PageMapper{
        public static <T> PageDTO<T> toDTO(Page<T> page) {
                if (page == null) {
                        return null;
                }
                return new PageDTO<>(
                        page.getContent(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast(),
                        page.isFirst()
                );
        }
}