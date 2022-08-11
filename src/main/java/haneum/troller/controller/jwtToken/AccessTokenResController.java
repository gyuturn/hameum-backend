package haneum.troller.controller.jwtToken;

import haneum.troller.service.security.JwtService;
import haneum.troller.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
public class AccessTokenResController {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    public ResponseEntity getSubject(@RequestHeader(value = "accessToken") String token) {
        log.info("access-token 인증");
        try {
            jwtService.getSubjectByToken(token);
        } catch (ExpiredJwtException e) {
            log.debug("access-token 만료");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.debug("access-token 일치하지 않음");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }




}
