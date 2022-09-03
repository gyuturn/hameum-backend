package haneum.troller.dto.dataflow;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemberListDto {

    private List<Long> memberList;

}
