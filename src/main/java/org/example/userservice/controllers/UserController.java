package org.example.userservice.controllers;

import org.example.userservice.dtos.*;
import org.example.userservice.dtos.ResponseStatus;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

import static org.example.userservice.dtos.ResponseStatus.SUCCESS;

@RestController
@RequestMapping("/users")
public class UserController {
    //login, signUp, validateToke, logout
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto logIn(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto responseDto = new LoginResponseDto();
        try{

            Token token = userService.login(
                    loginRequestDto.getEmail(),
                    loginRequestDto.getPassword()
            );
            responseDto.setToken(token.getValue());
            responseDto.setResponseStatus(ResponseStatus.SUCCESS);

        }catch(Exception e){
            System.out.println("================");
            System.out.println(e.getMessage());
            responseDto.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDto;
    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        User user= userService.signUp(
                signUpRequestDto.getName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword()
        );
        //once user is sign up then we need to convert this user into userDto
//        UserDto userDto = new UserDto();
//        userDto.setName(user.getName()); we should write this logic to UserDto class

        return UserDto.from(user);
    }

    @PostMapping("/logout")
    public void logOut(@RequestBody LogOutRequestDto logOutRequestDto){
        userService.logOut(logOutRequestDto.getToken());
    }

    @GetMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable String token){
        User user = userService.validateToken(token);
        return UserDto.from(user);
    }
}
