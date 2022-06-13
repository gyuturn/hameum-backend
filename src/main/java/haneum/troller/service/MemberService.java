package haneum.troller.service;

import haneum.troller.domain.Member;
import haneum.troller.dto.LoginForm;
import haneum.troller.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    //회원가입
    @Transactional(readOnly = false)
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getMemberId();
    }


    //로그인
    public boolean validLogin(LoginForm loginForm) {
        Member member = memberRepository.findByEmail(loginForm.getEMail());
        if (member.getPassword().equals(loginForm.getPassword())) {
            return true;
        }
        else {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }
}
