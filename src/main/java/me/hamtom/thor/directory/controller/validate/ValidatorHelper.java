package me.hamtom.thor.directory.controller.validate;

import me.hamtom.thor.directory.dto.enumerated.OptionValue;
import me.hamtom.thor.directory.exception.PredictableRuntimeException;

public class ValidatorHelper {
    public static final String DIR_NAME_REG = "(?!.*\\s)^[a-zA-Z0-9_-]+$";
    public static final String PATH_NAME_REG = "^\\/?([a-zA-Z0-9_-]+\\/)*[a-zA-Z0-9_-]+$";
    public static final String NO_BLANK_REG = "^(?!.*\\\\s).*$";

    public static OptionValue strToOptionValue(String valueName, String value) {
        // null 확인
        if (value == null) {
            // null
            return OptionValue.NO_VALUE;
        }
        // T, F 확인
        if (value.equalsIgnoreCase("T")) {
            return OptionValue.TRUE;
        } else if (value.equalsIgnoreCase("F")) {
            return OptionValue.FALSE;
        } else{
            String exceptionMsg = String.format("%s 값은 T, F만 가능합니다.", valueName);
            throw new PredictableRuntimeException(exceptionMsg);
        }
    }
}
