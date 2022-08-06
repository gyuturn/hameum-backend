package haneum.troller.common.aop;

import haneum.troller.common.exception.exceptions.JWTException;
import haneum.troller.domain.Member;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.login.MemberService;
import haneum.troller.service.security.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.SignatureException;

@RequiredArgsConstructor
@Aspect     // AOP Aspect
@Component
public class AuthAspect {

    private static final String AUTHORIZATION = "JWT-accessToken";

    private final JwtService jwtService;
    private final HttpServletRequest httpServletRequest;
    private final MemberRepository memberRepository;

    @Around("@annotation(haneum.troller.common.aop.annotation.Auth)") // 어노테이션과 Aspect 연결
    public Object accessToken(final ProceedingJoinPoint pjp) throws Throwable {
        try {
            String accessToken = httpServletRequest.getHeader(AUTHORIZATION); // HTTP Header 에서 AccessToken을 꺼냄
            Long memberId = jwtService.validTokenForAccessToken(accessToken);          // Token 검증
            Member member = memberRepository.findById(memberId).get();
//            UserContext.USER_CONTEXT.set(new JwtPayload(user.getId()));
            return pjp.proceed();
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new JWTException("jwt에서 인증 로직 error(ex-시간만료)");
        }
    }
}