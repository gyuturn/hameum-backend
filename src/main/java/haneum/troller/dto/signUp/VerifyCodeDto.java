package haneum.troller.dto.signUp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class VerifyCodeDto {
    private String code;
    private String expiredTime;

}
