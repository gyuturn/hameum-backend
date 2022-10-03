package haneum.troller.controller.findDuo;


import haneum.troller.common.aop.annotation.Auth;
import haneum.troller.domain.Board;
import haneum.troller.dto.findDuo.FindDuoDeleteDto;
import haneum.troller.dto.findDuo.FindDuoRequestDto;
import haneum.troller.dto.findDuo.FindDuoResponseDto;
import haneum.troller.repository.BoardRepository;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.findDuo.FindDuoService;
import haneum.troller.service.findDuo.FindDuoToDtoService;
import haneum.troller.service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Duo search",description = "듀오보드 작성 및 필터링 API")
@RequestMapping("/api/findDuo")
@RestController
@Slf4j
@RequiredArgsConstructor
public class FindDuoController{

    private final BoardRepository boardRepository;
    private final FindDuoService findDuoService;
    private final FindDuoToDtoService findDuoToDtoService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Operation(summary = "듀오찾기 글 등록 및 필터링 api ", description = "듀오찾기 글 생성 및 조회" // 차후 규민형 컴펌 받아야 함
    )
    @ApiResponses(
            value ={
                    @ApiResponse(responseCode = "201", description = "듀오찾기 글 등록 생성")
            }
    )
    @PostMapping("/create")// 노션에 있는 내용으로 일단 사용
    @Auth
    public ResponseEntity createFindDuoBoard(@RequestHeader("JWT-accessToken")String accessToken, @RequestBody FindDuoRequestDto findDuoRequestDto)throws Exception{
// access token 인증 및 롤네임 추출 = lolName;
        Long subjectByToken = Long.valueOf(jwtService.getSubjectByToken(accessToken));
        String lolName = memberRepository.findById(subjectByToken).get().getLolName();
//        FindDuoResponseDto findDuoResponseDto = findDuoToDtoService.getFindDuoDto(lolName);
//        Board board = findDuoService.requestDtoToEntity(findDuoRequestDto);  // requestBody 에 있는 내용을 entity에 추가해야 함
//        board = findDuoService.riotApiToEntity(findDuoResponseDto, board);
//        // 라이엇 api 데이터 파싱한 내용을 entity에 추가해야 함
//        boardRepository.save(board);
//        return new ResponseEntity(boardRepository.findAll(), HttpStatus.OK);
        FindDuoResponseDto findDuoResponseDto = findDuoToDtoService.getFindDuoDto(lolName);
        Board board = findDuoService.requestDtoToEntity(findDuoRequestDto);  // requestBody 에 있는 내용을 entity에 추가해야 함
        board = findDuoService.riotApiToEntity(findDuoResponseDto, findDuoRequestDto, board);
        // 라이엇 api 데이터 파싱한 내용을 entity에 추가해야 함
        boardRepository.save(board);
        return new ResponseEntity(boardRepository.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteFindDuoBoard(@RequestBody FindDuoDeleteDto findDuoDeleteDto){
        long id = findDuoDeleteDto.getId();
        boardRepository.deleteById(id);
        return new ResponseEntity(boardRepository.findAll(), HttpStatus.OK);
    }
}
