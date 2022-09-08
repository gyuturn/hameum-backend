package haneum.troller.dto.findDuo;

import haneum.troller.domain.Board;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@NoArgsConstructor
@Setter
public class FindDuoRequestDto{
    private int vaildTime;
    private long timeStamp;
    private Boolean mike;
    private String title;
    private String content;

    public Board toEntity(){
        return Board.builder()
                .vaildTime(vaildTime)
                .mike(mike)
                .title(title)
                .content(content)
                .timeStamp(timeStamp)
                .build();
    }
}
