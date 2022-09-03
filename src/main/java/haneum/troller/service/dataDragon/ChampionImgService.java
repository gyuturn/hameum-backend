package haneum.troller.service.dataDragon;

import org.springframework.stereotype.Service;

@Service
public class ChampionImgService {

    public String getChampionImg(String championName)  {
        String url = "https://ddragon.leagueoflegends.com/cdn/"
                + "12.15.1"
                + "/img/champion/"
                + championName
                + ".png";
        return url;
    }
}
