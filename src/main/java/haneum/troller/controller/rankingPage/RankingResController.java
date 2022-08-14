package haneum.troller.controller.rankingPage;

import haneum.troller.dto.mainPage.MainPageDto;
import haneum.troller.dto.ranking.RankingPageDto;
import haneum.troller.service.ranking.MainPageRankService;
import haneum.troller.service.ranking.RankingPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="rank",description = "랭킹페이지,메인페이지 관련 api")
@RequestMapping("/api/rank/")
@RestController
@Slf4j
@RequiredArgsConstructor
public class RankingResController {
    private final RankingPageService rankingPageService;
    private final MainPageRankService mainPageRankService;


    @Operation(summary = "랭킹(일반) api",description = "랭킹페이지에서 사용되는 api'\n" +
            "일반 5*5 솔로랭크 기준 랭크 순위'\n" +
            "승리,티어,소환사 아이콘, 승률, 패배, 소환사 레벨을 보여주긴 하는데 그중 필요한것만골라서 사용하면 될듯"
    )
    @GetMapping("normal")
    public RankingPageDto getRankingRankPage()throws ParseException{
            log.debug("랭킹페이지(일반) 조");
            RankingPageDto rankingPageDto = rankingPageService.getRankingOrderPage();
            return rankingPageDto;
    }


    @Operation(summary = "랭킹(메인페이지) api",description = "메인페이지에서 사용되는 api'\n" +
            "일반 5*5 솔로랭크 기준 랭크 순위'\n'"+
            "이름:점수 만을 나타냄"
    )
    @GetMapping("main")
    public MainPageDto getRankingMainPage()throws ParseException{
        log.debug("main페이지 랭킹 조회");
        MainPageDto mainPageDto = mainPageRankService.getRankOrder();

        return mainPageDto;
    }
}
