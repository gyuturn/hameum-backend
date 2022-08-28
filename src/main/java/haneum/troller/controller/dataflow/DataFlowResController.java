package haneum.troller.controller.dataflow;


import haneum.troller.domain.Member;
import haneum.troller.dto.dataflow.LocalDateTimeDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.dataflow.LocalDateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name="dataflow 관련 api",description = "dataflow 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dataflow")
@Slf4j
public class DataFlowResController {

    private final MemberRepository memberRepository;

    @Operation(summary = "멈베 날짜 비교 api", description = "dataflow app에서 현재 날짜를 받아 현재 날짜보다 24이내에 업데이트 되지 않는 멤버리스트 return"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "정상적 조회"),
                    @ApiResponse(responseCode = "404", description = "멤버 찾지못함.")
            }
    )
    @PostMapping("/compare/member/date")
    public ResponseEntity compareMemberDate(@RequestBody LocalDateTimeDto localDateTimeDto) {
        log.info("멤버-dataflowApp 날짜 비교");
        List<Member> members = memberRepository.findAll();
        List<Long> forUpdateMembersId = new ArrayList<>();

        LocalDateTime otherLocalDateTime = LocalDateService.getLocalDateTime(localDateTimeDto);

        for (Member member : members) {
            if(member.getModifiedDate().compareTo(otherLocalDateTime) == -1){
                forUpdateMembersId.add(member.getMemberId());
            }
        }
        return new ResponseEntity(forUpdateMembersId, HttpStatus.OK);
    }


}
