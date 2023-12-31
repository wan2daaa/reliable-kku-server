package com.deundeunhaku.reliablekkuserver.sms.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.deundeunhaku.reliablekkuserver.sms.constant.SmsConstant;
import com.deundeunhaku.reliablekkuserver.sms.dto.RecipientList;
import com.deundeunhaku.reliablekkuserver.sms.dto.SmsCertificationNumber;
import com.deundeunhaku.reliablekkuserver.sms.dto.SmsResponse;
import com.deundeunhaku.reliablekkuserver.sms.dto.ToSmsServerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 기존 nhn 활용한 문자 인증 이였는데 nhn은 사업자만 가능하여 진행하기 불편하여 CoolSms로 변경하였습니다.
 */
@Deprecated
@Slf4j
@Service
public class SmsService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final SmsConstant smsConstant;

  public SmsService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
      SmsConstant smsConstant) {
    this.restTemplate = restTemplateBuilder.build();
    this.objectMapper = objectMapper;
    this.smsConstant = smsConstant;
  }


  public SmsCertificationNumber sendCertificationNumberToPhoneNumber(String phoneNumber) {
    int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
    String content = "안녕하세요! 든붕이 입니다. \n인증번호는 " + randomNumber + " 입니다.";

    sendSms(phoneNumber, content);

    return SmsCertificationNumber.of(randomNumber);
  }

  public void sendNewPasswordToPhoneNumber(String phoneNumber, String newPassword) {
    String content = "안녕하세요! 든붕이 입니다. \n새로운 비밀번호는 " + newPassword + " 입니다.";

    sendSms(phoneNumber, content);
  }

  public void sendOrderCompleteMessage(String phoneNumber, Long leftMinutes) {
    String content = "안녕하세요! 든붕이 입니다. \n주문이 완료되었습니다. \n" + leftMinutes + "분 후에 완료될 예정입니다.";

    sendSms(phoneNumber, content);
  }

  public void sendOrderCancelMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n가게의 사정으로 인해 주문이 취소되었습니다.\n다음에 이용해주세요.";

    sendSms(phoneNumber, content);
  }


  public void sendOrderPickupMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵이 완성되었습니다!\n30분 내로 매장에서 붕어빵을 수령해주세요.";

    sendSms(phoneNumber, content);
  }

  public void sendOrderNotTakeMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵을 시간내에 수령하지 않아 미수령 처리하였습니다.";

    sendSms(phoneNumber, content);
  }


  public void sendOrderFinishMessage(String phoneNumber) {
    String content = "안녕하세요! 든붕이 입니다.\n붕어빵 맛있게 드세요! :>";

    sendSms(phoneNumber, content);
  }

  private void sendSms(String phoneNumber, String content) {
    try {
      String apiServerUrl =
          "https://api-sms.cloud.toast.com/sms/v3.0/appKeys/" + smsConstant.getAppKey()
          + "/sender/sms";

      List<RecipientList> recipientList = setRecipientList(phoneNumber);

      ToSmsServerRequest smsPayload = setToSmsServerPayload(recipientList, content,
          smsConstant.getPhone());
      String payload = objectMapper.writeValueAsString(smsPayload);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(APPLICATION_JSON);
      headers.set("X-Secret-Key", smsConstant.getSecretKey());

      HttpEntity request = new HttpEntity(payload, headers);

      var response = restTemplate.exchange(
              apiServerUrl,
              HttpMethod.POST,
              request,
              SmsResponse.class
          )
          .getBody();

    } catch (Exception e) {
      log.warn("메시지 전송 실패", e.getMessage());
    }
  }

  private ToSmsServerRequest setToSmsServerPayload(
      List<RecipientList> recipientList, String content, String sendPhoneNumber) {
    return ToSmsServerRequest.builder()
        .body(content)
        .sendNo(sendPhoneNumber)
        .recipientList(recipientList)
        .build();
  }

  private List<RecipientList> setRecipientList(String recipientNumber) {
    return new ArrayList<>(Collections.singleton(RecipientList.of(recipientNumber)));
  }

}
