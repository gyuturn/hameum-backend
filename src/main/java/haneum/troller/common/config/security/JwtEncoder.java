package haneum.troller.common.config.security;

import haneum.troller.domain.Member;
import haneum.troller.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Service
public class JwtEncoder {
    private static final String secretKey="GGeokDrupakdlsdkqwdkdfdaddjflkdwodfdasdasdafsdfeflwfqvfdmfdsfdkjaslfjisdfjosidf";
    @Autowired
    MemberRepository memberRepository;


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
    public boolean validToken(String token) {
        String email = getSubjectByToken(token);
        if (email!=null) return true;
        else return false;
    }

    public String findLolNameByToken(String token) throws IllegalAccessException {
        String email = getSubjectByToken(token);
        Member member = memberRepository.findByEmail(email);
        return member.getLolName();
    }
}
