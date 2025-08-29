package com.example.bajajfinserv.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    private String webhookUrl;
    private String accessToken;
}