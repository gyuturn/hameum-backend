package haneum.troller.service;

import haneum.troller.domain.Member;
import haneum.troller.dto.login.SignInDto;
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
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getMemberId();
    }


    //refresh-token 저장
    @Transactional(readOnly = false)
    public Member updateRefreshToken(Member member, String token) {
        member.updateRefreshToken(token);
        return member;
    }



    //로그인(비밀번호)
    public boolean validLogin(SignInDto loginDto) {
        Member member = memberRepository.findByEmail(loginDto.getEmail());
        if (passwordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            return true;
        }
        else {
            return false;
        }
    }

    //이메일 중복인증
    public boolean checkDuplicateEmail(String email){

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return true;
        }
        else return false;

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
