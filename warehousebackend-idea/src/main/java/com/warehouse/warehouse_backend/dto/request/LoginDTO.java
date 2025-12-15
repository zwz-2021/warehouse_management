package com.warehouse.warehouse_backend.dto.request;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
}