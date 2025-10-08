package at.fhtw.mrp.dto;

import at.fhtw.mrp.exceptions.InvalidInputException;
import org.apache.commons.lang3.StringUtils;

public record UserAuthDTO(String username, String password) implements Validateable {
    @Override
    public void validate() throws InvalidInputException {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(username)) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
    }
}
