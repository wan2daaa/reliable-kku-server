package com.deundeunhaku.reliablekkuserver.jwt;

import static com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration.ACCESS_TOKEN_DURATION;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.deundeunhaku.reliablekkuserver.common.exception.NotAuthorizedException;
import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/token")
public class JwtController {

  private final JwtTokenUtils jwtTokenUtils;

  @GetMapping("/valid")
  public ResponseEntity<Void> isAccessTokenValid(HttpServletRequest request
  ) {

    String accessToken = parseBearerToken(request);

    String phoneNumber = jwtTokenUtils.getPhoneNumber(accessToken);
    Boolean validate = jwtTokenUtils.validate(accessToken, phoneNumber);

    if (validate) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/update")
  public ResponseEntity<Void> updateAccessToken(
      @CookieValue(name = "refreshToken") Cookie refreshTokenCookie
  ) {
    String refreshToken = refreshTokenCookie.getValue();

    String phoneNumber = jwtTokenUtils.getPhoneNumber(refreshToken);
    Boolean validate = jwtTokenUtils.validate(refreshToken, phoneNumber);

    if (!validate) {
      log.warn("토큰 검증 실패 {}", phoneNumber);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    if (jwtTokenUtils.isTokenExpired(refreshToken)) {
      log.warn("토큰 만료 {}", jwtTokenUtils.getPhoneNumber(phoneNumber));

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String newAccessToken = jwtTokenUtils.generateJwtToken(phoneNumber,
        ACCESS_TOKEN_DURATION.getDuration());

    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
        .maxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond())
        .httpOnly(true)
        .build();

    return ResponseEntity.ok()
        .header(AUTHORIZATION, "Bearer " + newAccessToken)
        .header(SET_COOKIE, accessTokenCookie.toString())
        .build();
  }

  private void setAccessTokenInCookie(String accessToken, HttpServletResponse response) {
    Cookie accessTokenCookie = new Cookie("accessToken", "Bearer " + accessToken);
    accessTokenCookie.setMaxAge(TokenDuration.ACCESS_TOKEN_DURATION.getDurationInSecond());
    accessTokenCookie.setHttpOnly(true);
    response.addCookie(accessTokenCookie);
  }

  private String parseBearerToken(HttpServletRequest request) {

    String accessToken = request.getHeader(AUTHORIZATION);

    if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
      return accessToken.substring(7);
    } else {
      throw new NotAuthorizedException("잘못된 토큰입니다.");
    }
  }

}
