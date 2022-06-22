package haneum.troller.service.dataDragon;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class MyPageImgService {
    private static String version;

    public String getIconImg(String iconNum)  {
        version = VersionService.getNewVersion();
        String url = "http://ddragon.leagueoflegends.com/cdn/"
                + version
                + "/img/profileicon/"
                + iconNum
                + ".png";

        return url;
    }


}
