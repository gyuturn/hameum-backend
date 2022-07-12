package haneum.troller.security;

import haneum.troller.domain.Member;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.MemberService;
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
public class SecurityService {
    private  String secretKey;
    @Autowired
    MemberRepository memberRepository;

    //로그인 서비스 던질때 같이
    public String createToken(String eMail,long expTime) {
        if (expTime <= 0) {
            throw new RuntimeException("만료시간이 0보다 커야함");
        }

        Member member = memberRepository.findByEmail(eMail);
        secretKey = member.getPassword();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyByte = DatatypeConverter.parseBase64Binary(secretKey);

        //key 생성
        Key singingKey = new SecretKeySpec(secretKeyByte, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setSubject(eMail)
                .signWith(singingKey, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .compact();
    }


    public String getEmailByToken(String token) {
        String eMail = null;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        eMail = claims.getSubject();

        return eMail;

    }

    //토큰 검증 메서드를 boolean
    public boolean validToken(String token) {
        String email = getEmailByToken(token);
        if (email!=null) return true;
        else return false;
    }

    public String findLolNameByToken(String token) throws IllegalAccessException {
        String email = getEmailByToken(token);
        Member member = memberRepository.findByEmail(email);
        return member.getLolName();
    }
}
