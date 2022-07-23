package haneum.troller.controller.jwtToken;

import haneum.troller.common.config.security.JwtEncoder;
import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
@Tag(name="Access",description = "AccessToken관련 API")
@RestController
@RequestMapping("/api/jwt/access/")
@Slf4j
@RequiredArgsConstructor
public class AccessTokenResController {
    private final JwtEncoder jwtEncoder;
    private final MemberRepository memberRepository;



    @Operation(summary = "access-token인증 api",description = "iput: 토큰'\n" +"" +
            "유효한 토큰인지 확인하는 api(access)'\n" )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "토큰 인증 성공"),
                    @ApiResponse(responseCode = "403",description = "토큰 시간만료"),
                    @ApiResponse(responseCode = "404",description = "토큰이 일치하지 않음")
            }
    )
    @GetMapping("auth")
    public ResponseEntity getSubject(@RequestHeader(value = "accessToken") String token) {
        log.info("access-token 인증");
        try {
            jwtEncoder.getSubjectByToken(token);
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
