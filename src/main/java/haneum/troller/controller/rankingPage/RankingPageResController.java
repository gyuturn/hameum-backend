package haneum.troller.controller.rankingPage;

import haneum.troller.dto.ranking.RankingPageDto;
import haneum.troller.service.RankingPageService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingPageResController {
    public final RankingPageService rankingPageService;

    @GetMapping("/ranking_page")
    public RankingPageDto getRanking()throws ParseException{
            RankingPageDto rankingPageDto = rankingPageService.getRankingOrderPage();
            return rankingPageDto;
    }
}
