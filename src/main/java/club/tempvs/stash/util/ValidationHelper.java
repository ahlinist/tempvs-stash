package club.tempvs.stash.util;

import club.tempvs.stash.dto.ErrorsDto;

public interface ValidationHelper {

    ErrorsDto getErrors();

    void addError(ErrorsDto errorsDto, String field, String messageKey);

    void addError(ErrorsDto errorsDto, String field, String messageKey, Object[] args);

    void processErrors(ErrorsDto errorsDto);
}
