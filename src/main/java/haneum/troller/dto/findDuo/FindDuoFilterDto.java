package haneum.troller.dto.findDuo;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@NoArgsConstructor
@Setter
public class FindDuoFilterDto {
    private String position;

    private String rate;
}
