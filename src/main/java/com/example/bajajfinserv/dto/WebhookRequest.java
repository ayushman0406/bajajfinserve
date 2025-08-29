package com.example.bajajfinserv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
}