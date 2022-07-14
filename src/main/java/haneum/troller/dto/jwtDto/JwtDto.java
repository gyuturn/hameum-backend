package haneum.troller.dto.jwtDto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class JwtDto {
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
