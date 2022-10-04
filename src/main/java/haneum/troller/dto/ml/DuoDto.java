package haneum.troller.dto.ml;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DuoDto implements  Comparable<DuoDto>{

    private String lolName;
    private Double trollPossibility;
    private String tier;
    private String ranking;
    private String winRate;

    @Override
    public int compareTo(DuoDto p) {
        if(this.trollPossibility > p.trollPossibility) {
            return 1; // x에 대해서는 오름차순
        }
        return -1;
    }
}
