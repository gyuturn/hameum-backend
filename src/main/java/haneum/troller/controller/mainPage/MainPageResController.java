package haneum.troller.controller.mainPage;

import haneum.troller.dto.member.mainPage.MainPageDto;
import haneum.troller.service.MainPageService;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;

public class MainPageResController{
    private final MainPageService mainPageService;

    public MainPageResController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }

    @GetMapping("/main_page")
    public MainPageDto getRanking()throws ParseException{
        MainPageDto mainPageDto = mainPageService.getRankOrder();

        return mainPageDto;
    }
}