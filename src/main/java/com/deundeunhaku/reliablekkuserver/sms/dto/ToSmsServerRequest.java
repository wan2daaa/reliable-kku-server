package com.deundeunhaku.reliablekkuserver.sms.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ToSmsServerRequest {
    String body;
    String sendNo;
    List<RecipientList> recipientList;
}
