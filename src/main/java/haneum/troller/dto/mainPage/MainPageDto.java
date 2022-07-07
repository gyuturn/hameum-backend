package haneum.troller.dto.mainPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.HashMap;

@Getter
@NoArgsConstructor
@Setter
public class MainPageDto{
    private HashMap<String, Integer>rankMap;
}