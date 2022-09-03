package haneum.troller.service.dataflow;

import haneum.troller.dto.dataflow.LocalDateTimeDto;

import java.time.LocalDateTime;

public class LocalDateService {

    public static  LocalDateTime getLocalDateTime(LocalDateTimeDto localDateTimeDto) {
        LocalDateTime otherLocalDateTime = LocalDateTime.of(localDateTimeDto.getYear(),
                localDateTimeDto.getMonth(),
                localDateTimeDto.getDay() ,
                localDateTimeDto.getHour(),
                localDateTimeDto.getMinute(),
                localDateTimeDto.getSecond());
        return otherLocalDateTime;
    }


}
