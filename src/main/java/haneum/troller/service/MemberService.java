package haneum.troller.service;

import haneum.troller.domain.Member;
import haneum.troller.dto.login.LoginDto;
import haneum.troller.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    //로그인(비밀번호)
    public boolean validPassword(LoginDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEMail());
        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return true;
        }
        else {
            return false;
        }
    }

    //이메일 중복인증
    public boolean checkDuplicateEmail(String email){
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

    //롤 닉네임이 이미 사용중인지 체크
    public boolean checkDuplicateLolName(String lolName) {
        List<Member> memberList = memberRepository.findByLolName(lolName);
        if (memberList.size() == 0) {
            return true;
        }
        else return false;
    }
}
