package haneum.troller.service.security;

import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.login.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private static final String secretKey="GGeokDrupakdlsdkqwdkdfdaddjflkdwodfdasdasdafsdfeflwfqvfdmfdsfdkjaslfjisdfjosidf";
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    public JwtDto makeTokensForLogin(Member member) {
        String accessToken = createAccessToken(member.getMemberId(), 60*1000*60*24); //토큰 주기 24시간으로 설정 (test)            String refreshToken = jwtEncoder.createRefreshToken(member.getEmail(), 60 * 1000*2); //토큰 주기 1주일으로 설정 (test)
        String refreshToken = createRefreshToken(member.getEmail(), 60*1000*60*24); //토큰 주기 48시간으로 설정 (test)
        memberService.updateRefreshToken(member, refreshToken);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);


        JwtDto jwtDto = JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return jwtDto;
    }

    public String createAccessToken(Long id,long expTime){
        if (expTime <= 0) {
            throw new RuntimeException("만료시간이 0보다 커야함");
        }

        return getJwtToken(String.valueOf(id), expTime);
    }
    public String createRefreshToken(String eMail, long expTime) {
        if (expTime <= 0) {
            throw new RuntimeException("만료시간이 0보다 커야함");
        }
        return getJwtToken(eMail, expTime);
    }

    private String getJwtToken(String subject, long expTime) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyByte = DatatypeConverter.parseBase64Binary(secretKey);

        //key 생성
        Key singingKey = new SecretKeySpec(secretKeyByte, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setSubject(subject)
                .signWith(singingKey, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .compact();
    }


    public String getSubjectByToken(String token) {
        String subject = null;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        subject = claims.getSubject();

        return subject;

    }

    //토큰 검증 메서드를 boolean
    public Long validTokenForAccessToken(String token) {
//        if (token == null) {
//            return false;
//        }
        String id = getSubjectByToken(token);
        return Long.valueOf(id);
    }

    public String findLolNameByToken(String token) throws IllegalAccessException {
        String email = getSubjectByToken(token);
        Member member = memberRepository.findByEmail(email);
        return member.getLolName();
    }

//    public void getSubject(@RequestHeader(value = "accessToken") String token) {
//        try {
//            getSubjectByToken(token);
//        } catch (ExpiredJwtException e) {
//            return new ResponseEntity(HttpStatus.FORBIDDEN);
//        } catch (Exception e) {
//            return new ResponseEntity(HttpStatus.NOT_FOUND);
//        }
//
//    }

}
