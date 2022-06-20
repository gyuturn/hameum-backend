package haneum.troller.service;

import haneum.troller.domain.Member;
import haneum.troller.dto.LoginForm;
import haneum.troller.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @Transactional(readOnly = false)
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getMemberId();
    }


    //로그인
    public boolean validLogin(LoginForm loginForm) {
        Member member = memberRepository.findByEmail(loginForm.getEMail());
        if (passwordEncoder.matches(loginForm.getPassword(), member.getPassword())) {
            return true;
        }
        else {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    //이메일 중복인증
    public boolean validDuplicateEmail(String email){
        boolean result=false;
        try {
            memberRepository.findByEmail(email);
        } catch (Exception e) {
            result=true;
        }
        finally {
            return result;
        }
    }
}
