package fi.fileuploader.feature.file.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserCsvData {

    @CsvBindByPosition(position = 0)
    private String givenName;

    @CsvBindByPosition(position = 1)
    private String familyName;

    @CsvBindByPosition(position = 2)
    private String registrationNumber;

    @CsvBindByPosition(position = 3)
    private String email;
}
