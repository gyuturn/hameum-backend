package haneum.troller.controller.mainPage;

import haneum.troller.dto.mainPage.MainPageDto;
import haneum.troller.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainPageResController{
    private final MainPageService mainPageService;

    @GetMapping("/main_page")
    public MainPageDto getRanking()throws ParseException{
        MainPageDto mainPageDto = mainPageService.getRankOrder();

        return mainPageDto;
    }
}