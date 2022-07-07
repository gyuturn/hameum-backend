package haneum.troller.dto.signUp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class CheckLoLNameDto {
    private String lolName;
    private boolean dupLolName;
    private boolean validLolName;

}
