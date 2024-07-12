package com.deundeunhaku.reliablekkuserver.member.controller;

import com.deundeunhaku.reliablekkuserver.jwt.constants.TokenDuration;
import com.deundeunhaku.reliablekkuserver.jwt.util.JwtTokenUtils;
import com.deundeunhaku.reliablekkuserver.member.domain.Member;
import com.deundeunhaku.reliablekkuserver.member.dto.AdminLoginRequest;
import com.deundeunhaku.reliablekkuserver.member.service.AdminMemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminAuthenticationController {

    private final JwtTokenUtils jwtTokenUtils;
    private final AdminMemberService adminMemberService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody AdminLoginRequest loginRequest) {

        adminMemberService.login(loginRequest.username(), loginRequest.password());

        String accessToken = jwtTokenUtils.generateJwtToken(loginRequest.username(),
                TokenDuration.ACCESS_TOKEN_DURATION_ADMIN.getDuration());

        return ResponseEntity.ok()
                .header(AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Member member,
                                       HttpServletResponse response) {
        adminMemberService.deleteRefreshToken(member);

        return ResponseEntity.ok().build();
    }

    private static void setTokenMaxAgeZero(Cookie cookie, HttpServletResponse response) {
        cookie.setValue("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
