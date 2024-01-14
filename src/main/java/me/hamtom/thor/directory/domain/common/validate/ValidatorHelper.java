package me.hamtom.thor.directory.domain.common.validate;

public class ValidatorHelper {
    public static final String PATH_NAME_REG = "^(?!.*//)[a-zA-Z0-9-_\\/]+$";

    public static final String DIR_NAME_REG = "^[a-zA-Z0-9_-]+$";
    public static final String OWNER_GROUP_NAME_REG = "^[a-zA-Z0-9]+$";
    public static final String PERMISSIONS_REG = "^[r-][w-][x-][r-][w-][x-][r-][w-][x-]$";

    public static boolean optionToBoolean(String optionValue) {
        if (optionValue == null) {
            return false;
        } else {
            return Boolean.parseBoolean(optionValue);
        }
    }
}
