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

    public Board riotApiToEntity(FindDuoResponseDto findDuoResponseDto, FindDuoRequestDto findDuoRequestDto, Board board){
        double avgKill = getAvgKda(Integer.parseInt(findDuoResponseDto.getKill()));
        double avgAssist = getAvgKda(Integer.parseInt(findDuoResponseDto.getAssist()));
        double avgDeath = getAvgKda(Integer.parseInt(findDuoResponseDto.getDeath()));
        board = Board.builder()
                .kda(findDuoResponseDto.getKda())
                .win(Integer.parseInt(findDuoResponseDto.getWin()))
                .lose(Integer.parseInt(findDuoResponseDto.getLose()))
                .killing(avgKill)
                .death(avgDeath)
                .assist(avgAssist)
                .tier(findDuoResponseDto.getTier())
                .favorChampions(findDuoResponseDto.getFavorChampions())
                .favorPosition(findDuoResponseDto.getFavorPosition())
                .lolName(findDuoResponseDto.getLolName())
                .positionData(findDuoRequestDto.getPositionData())
                .mike(findDuoRequestDto.getMike())
                .content(findDuoRequestDto.getContent())
                .title(findDuoRequestDto.getTitle())
                .build();
        return board;
    }

    public Board requestDtoToEntity(FindDuoRequestDto findDuoRequestDto) {
        Board newBoard;
        System.out.println("findDuoRequestDto.getContent() = " + findDuoRequestDto.getContent());
        newBoard = Board.builder()
                .positionData(findDuoRequestDto.getPositionData())
                .mike(findDuoRequestDto.getMike())
                .content(findDuoRequestDto.getContent())
                .title(findDuoRequestDto.getTitle())
                .build();
        return newBoard;
    }

    public Double getAvgKda(int killOrDeathOrAssist){

        if (killOrDeathOrAssist == 0)
            return (double)0;
        double n = ((double)killOrDeathOrAssist / 20) * 10;
        n = Math.round(n);
        double avg = n / 10 ;
        return avg;
    }
}
