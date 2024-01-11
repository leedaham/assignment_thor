package me.hamtom.thor.directory.domain.common.validate;

import me.hamtom.thor.directory.domain.common.enumerated.OptionValue;
import me.hamtom.thor.directory.domain.common.exception.PredictableRuntimeException;

public class ValidatorHelper {
    public static final String DIR_NAME_REG = "(?!.*\\s)^[a-zA-Z0-9_-]+$";
    public static final String PATH_NAME_REG = "^\\/?([a-zA-Z0-9_-]+\\/)*[a-zA-Z0-9_-]+$";
    public static final String NO_BLANK_REG = "^(?!.*\\\\s).*$";

    public static OptionValue strToOptionValue(String valueName, String value) {
        // null 혹은 f,F -> FALSE
        // t,T -> TRUE
        if (value == null || value.equalsIgnoreCase("F")) {
            // null
            return OptionValue.FALSE;
        } else if (value.equalsIgnoreCase("T")) {
            return OptionValue.TRUE;
        }

        //예외
        String exceptionMsg = String.format("%s 값은 T, F만 가능합니다.", valueName);
        throw new PredictableRuntimeException(exceptionMsg);
    }
}
