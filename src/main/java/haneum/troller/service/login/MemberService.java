package haneum.troller.service.login;

import haneum.troller.Enum.LoginType;
import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.kakaoDto.AuthorizationDto;
import haneum.troller.dto.kakaoDto.KakaoSignUpDto;
import haneum.troller.dto.login.SignInDto;
import haneum.troller.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final KaKaoLoginService kaKaoLoginService;

    //회원가입
    public Long join(Member member) {
        memberRepository.save(member);
        return member.getMemberId();
    }

    //카카오 회원가입
    public Member kakaoJoin(KakaoSignUpDto kakaoSignUpDto) throws Exception {
        String email = kaKaoLoginService.getEmailByAccessToken(kakaoSignUpDto.getAccessToken());
        Member member = Member.builder()
                .email(email)
                .lolName(kakaoSignUpDto.getLolName())
                .build();
        member.updateLoginType(LoginType.KAKAO.label());
        join(member);
        return member;
    }

//    //카카오 로그인
//    public boolean validKakaoLogin(AuthorizationDto authorizationDto) throws Exception {
//        JwtDto jwtdto = kaKaoLoginService.getKakaoAccessToken(authorizationDto.getCode());
//        String email = kaKaoLoginService.getEmailByAccessToken(jwtdto.getAccessToken());
//        //이미 회원가입이 되어 있는 경우
//        if(!checkDuplicateEmail(email)){
//            return true;
//        }
//        else{
//           return false;
//        }
//
//    }

    //로그인시 소셜로그인으로 가입된 이메일 구분
    public boolean findLoginType(Member member) {
        if (member.getType() == LoginType.KAKAO.label()) {
            return false;
        } else return true;
    }




    //refresh-token 저장
    @Transactional(readOnly = false)
    public Member updateRefreshToken(Member member, String token) {
        member.updateRefreshToken(token);
        return member;
    }



    //로그인(비밀번호)
    public boolean validLogin(SignInDto loginDto) {
        if (passwordEncoder.matches(loginDto.getPassword(), loginDto.getPassword())) {
            return true;
        } else {
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
