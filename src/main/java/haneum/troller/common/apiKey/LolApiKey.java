package haneum.troller.common.apiKey;

import java.util.Random;

public class LolApiKey {
    public static final String API_KEY_01="RGAPI-af37a609-c78a-4507-a9bd-12189d45464b";
    public static final String API_KEY_02 = "RGAPI-73f7ea24-c617-4831-bd15-ed161dfcacc0";

    public static String randomApiKey(){
        Random random = new Random();
        int randomNum = random.nextInt(2);
        if(randomNum==1){
            return API_KEY_01;
        }
        else {
            return API_KEY_02;
        }
    }
}
