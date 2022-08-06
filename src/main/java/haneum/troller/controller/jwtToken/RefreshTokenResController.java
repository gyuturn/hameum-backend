package haneum.troller.controller.jwtToken;

import haneum.troller.service.security.JwtService;
import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.login.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Refresh",description = "RefreshToken관련 API")
@RestController
@RequestMapping("/api/jwt/refresh/")
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenResController {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Operation(summary = "access token&refresh token 재발급 api", description =
            "refreshToken을 db와 비교하여 accessToken 재발급'\n" +
            "access-Token은 아무값이나 reqeustBody로 넣어두됨")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "access 토큰 재발급 성공"),
                    @ApiResponse(responseCode = "403", description = "토큰 시간만료"),
                    @ApiResponse(responseCode = "404", description = "토큰이 일치하지 않음")
            }
    )
    @PatchMapping("re-issuance")
    public ResponseEntity reissuanceAccessToken(@RequestBody JwtDto jwtDto) {
        log.info("refresh-token을 이용한 access-token& refresh-token 재발급");
        String refreshToken = jwtDto.getRefreshToken();
        String email;
        try {
            email = jwtService.getSubjectByToken(refreshToken);
        }catch (ExpiredJwtException e) {
            log.debug("refresh-token 만료");
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.debug("refresh-token 일치하지 않음");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Member member = memberRepository.findByEmail(email);
        String newAccessToken = jwtService.createAccessToken(member.getMemberId(), 60 * 1000); //토큰 주기 1분으로 설정 (test)            String refreshToken = jwtEncoder.createRefreshToken(member.getEmail(), 60 * 1000*2); //토큰 주기 1주일으로 설정 (test)
        String newRefreshToken = jwtService.createRefreshToken(member.getEmail(), 60 * 1000*2); //토큰 주기 2분으로 설정 (test)
        memberService.updateRefreshToken(member, refreshToken);

        jwtDto.setAccessToken(newAccessToken);
        jwtDto.setRefreshToken(newRefreshToken);
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
}
