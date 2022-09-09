package haneum.troller.service.findDuo;


import haneum.troller.domain.Board;
import haneum.troller.dto.findDuo.FindDuoRequestDto;
import haneum.troller.dto.findDuo.FindDuoResponseDto;
import haneum.troller.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindDuoService{

    private final BoardRepository boardRepository;
    private final FindDuoToDtoService findDuoToDtoService;

    public Board riotApiToEntity(FindDuoResponseDto findDuoResponseDto, Board board){
        board = Board.builder()
                .win(Integer.parseInt(findDuoResponseDto.getWin()))
                .lose(Integer.parseInt(findDuoResponseDto.getLose()))
                .killing(Integer.parseInt(findDuoResponseDto.getKill()))
                .death(Integer.parseInt(findDuoResponseDto.getDeath()))
                .assist(Integer.parseInt(findDuoResponseDto.getAssist()))
                .tier(findDuoResponseDto.getTier())
                .favorChampions(findDuoResponseDto.getFavorChampions())
                .favorPosition(findDuoResponseDto.getFavorPosition())
                .lolName(findDuoResponseDto.getLolName())
                .build();
        return board;
    }

    public Board requestDtoToEntity(FindDuoRequestDto findDuoRequestDto) {
        Board newBoard;
        newBoard = Board.builder()
                .positionData(findDuoRequestDto.getPositionData())
                .mike(findDuoRequestDto.getMike())
                .content(findDuoRequestDto.getContent())
                .title(findDuoRequestDto.getTitle())
                .build();
        return newBoard;
    }
}
