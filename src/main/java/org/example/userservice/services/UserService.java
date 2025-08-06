package org.example.userservice.services;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.repositories.TokenRepository;
import org.example.userservice.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service

public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        //first check if it is present in DB or not,, if not throw an exception
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with email " + email + " not found");
        }
        //password that user is passing should match password present in DB
        User user = optionalUser.get();
        if (bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            //if matches then generate token
            Token token = createToken(user);
            //save in db
            Token savedToken = tokenRepository.save(token);
            return savedToken;
        }
        return null;

    }
    ///////////////////

    public User signUp(String name, String email, String password){

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        //user.setHashedPassword(password);
        //First encrypt the password using BCrypt Algorithm before storing it into DB

        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }
    /////////////////////////

    public void logOut(String tokenValue){
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue,
                false,
                 new Date()
        );
        if(optionalToken.isEmpty()){
            //throw Exception or null;
            return;
        }
        Token token = optionalToken.get();
        token.setDeleted(true);
        //save
        tokenRepository.save(token);
    }

    public User validateToken(String token){
        //first, we need to check whether token value is present in DB or not
        //second, token is valid or not (expiry Time of the token should be > current time and deleted(inst var in Base Model)) should be false)
        Optional<Token>optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                                        token,
                                        false,
                 new Date() //current Time

        );
        if(optionalToken.isEmpty()){
            //token is invalid
            return null;
        }
        return optionalToken.get().getUser();
    }


    //generate Token (some random String values)
    private Token createToken(User user){
        Token token = new Token();
        token.setUser(user);

        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        //expiry time of token is let's say 30 days from now
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAfterToday = today.plusDays(30);

        Date expiryAt = Date.from(thirtyDaysAfterToday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryAt);
        return token;
    }
}
