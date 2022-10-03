package haneum.troller.dto.ml;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrollAvgDto {
    private String trollPossibility;
    private String ironAvgTroll;
    private String bronzeAvgTroll;
    private String silverAvgTroll;
    private String goldAvgTroll;
    private String platinumAvgTroll;
    private String diamondAvgTroll;
    private String masterAvgTroll;
    private String grandMasterAvgTroll;
    private String challengerAvgTroll;
}
