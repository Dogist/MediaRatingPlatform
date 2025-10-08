package at.fhtw.mrp.service;

import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.exceptions.InvalidInputException;

public interface AuthService {
    String loginUser(UserAuthDTO userAuth) throws InvalidInputException;

    String checkAuthToken(String token);
}
