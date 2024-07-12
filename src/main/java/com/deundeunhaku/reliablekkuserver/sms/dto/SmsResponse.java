package com.deundeunhaku.reliablekkuserver.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@AllArgsConstructor
@Getter
@Builder
public class SmsResponse {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
    int certificationNumber;
}

