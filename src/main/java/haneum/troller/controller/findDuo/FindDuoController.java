package haneum.troller.controller.findDuo;


import com.google.gson.Gson;
import haneum.troller.common.aop.annotation.Auth;
import haneum.troller.domain.Board;
import haneum.troller.domain.GameRecord;
import haneum.troller.dto.findDuo.FindDuoDeleteDto;
import haneum.troller.dto.findDuo.FindDuoFilterDto;
import haneum.troller.dto.findDuo.FindDuoRequestDto;
import haneum.troller.dto.findDuo.FindDuoResponseDto;
import haneum.troller.repository.BoardRepository;
import haneum.troller.repository.GameRecordRepository;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.findDuo.FindDuoBoard;
import haneum.troller.service.findDuo.FindDuoFilterService;
import haneum.troller.service.findDuo.FindDuoService;
import haneum.troller.service.findDuo.FindDuoToDtoService;
import haneum.troller.service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final GameRecordRepository gameRecordRepository;
    private final FindDuoFilterService findDuoFilterService;

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
        FindDuoBoard findDuoBoard = new FindDuoBoard();
        findDuoToDtoService.getMostChamp(gameRecordRepository.findById(lolName).get().getMostChampion(), lolName, findDuoBoard);
        findDuoToDtoService.getLinePrefer(gameRecordRepository.findById(lolName).get().getLineInfo(), lolName, findDuoBoard);
        findDuoToDtoService.getFullRecord(gameRecordRepository.findById(lolName).get().getFullRecord(), lolName, findDuoBoard);
        findDuoToDtoService.getUserInfo(gameRecordRepository.findById(lolName).get().getUserInfo(), lolName, findDuoBoard);
        Board board = findDuoToDtoService.setFindDto(findDuoBoard, findDuoRequestDto, lolName);
//        FindDuoResponseDto findDuoResponseDto = findDuoToDtoService.getFindDuoDto(lolName);
//        Board board = findDuoService.requestDtoToEntity(findDuoRequestDto);  // requestBody 에 있는 내용을 entity에 추가해야 함
//        board = findDuoService.riotApiToEntity(findDuoResponseDto, findDuoRequestDto, board);
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

    public FindDuoRequestDto makeRequestDto(){
        FindDuoRequestDto findDuoRequestDto = new FindDuoRequestDto();
        findDuoRequestDto.setContent("test yap");
        findDuoRequestDto.setPositionData("Top shin Byeong ja");
        findDuoRequestDto.setTimeStamp(1111111111);
        findDuoRequestDto.setMike(Boolean.FALSE);
        findDuoRequestDto.setTitle("test title");
        return findDuoRequestDto;
    }

    public ResponseEntity testBoard(FindDuoRequestDto findDuoRequestDto, String lolName) throws ParseException {
        FindDuoBoard findDuoBoard = new FindDuoBoard();
        findDuoToDtoService.getMostChamp(gameRecordRepository.findById(lolName).get().getMostChampion(), lolName, findDuoBoard);
        findDuoToDtoService.getLinePrefer(gameRecordRepository.findById(lolName).get().getLineInfo(), lolName, findDuoBoard);
        findDuoToDtoService.getFullRecord(gameRecordRepository.findById(lolName).get().getFullRecord(), lolName, findDuoBoard);
        findDuoToDtoService.getUserInfo(gameRecordRepository.findById(lolName).get().getUserInfo(), lolName, findDuoBoard);
        Board board = findDuoToDtoService.setFindDto(findDuoBoard, findDuoRequestDto, lolName);
//        FindDuoResponseDto findDuoResponseDto = findDuoToDtoService.getFindDuoDto(lolName);
//        Board board = findDuoService.requestDtoToEntity(findDuoRequestDto);  // requestBody 에 있는 내용을 entity에 추가해야 함
//        board = findDuoService.riotApiToEntity(findDuoResponseDto, findDuoRequestDto, board);
        // 라이엇 api 데이터 파싱한 내용을 entity에 추가해야 함
        boardRepository.save(board);
        String json = new Gson().toJson(boardRepository.findAll());
        System.out.println(json);
        return new ResponseEntity(json, HttpStatus.OK);
    }
    @GetMapping("/test")
    public void runTest() throws ParseException, InterruptedException {
        testBoard(makeRequestDto(), "akaps");
        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "낙서공책");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "렌고쿠 변쥬로");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "경험치만먹을까");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "치키 챠");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "다숨곰");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "파워업키트");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "희 철");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "Tallis");
//        Thread.sleep(1000 * 40);
//        testBoard(makeRequestDto(), "말 이쁘게 하세여");

    }
    @GetMapping("/deleteTest")
    public ResponseEntity deleteTest() {
        boardRepository.deleteById(1L);
        return new ResponseEntity(boardRepository.findAll(), HttpStatus.CREATED);
    }

    @GetMapping("/filter")
    public ResponseEntity filter(@RequestParam(value = "position", required = false) String position, @RequestParam(value = "rate") String rate){
        findDuoFilterService.boardFilter(position, rate);
        return new ResponseEntity(boardRepository.findAll(), HttpStatus.CREATED);
    }
}
