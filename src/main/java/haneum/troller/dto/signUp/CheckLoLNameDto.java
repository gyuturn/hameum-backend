package haneum.troller.dto.signUp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class CheckLoLNameDto {
    private boolean dupLolName;
    private boolean validLolName;

}
