package haneum.troller.dto.kakaoDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class KakaoSignUpDto {
    private String lolName;
    private String accessToken;
    private String refreshToken;

}
