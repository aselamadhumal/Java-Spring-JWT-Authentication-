package com.jwtauth.jwtauth.exceptions;
import com.jwtauth.jwtauth.dto.DefaultResponse;
import com.jwtauth.jwtauth.dto.LoginRequestDTO;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final UserRepository userRepository;

    public GlobalExceptionHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   /* @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<String> handleLoginFailedException(LoginFailedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
*/

   @ExceptionHandler(LoginFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<DefaultResponse> handleLoginFailedException(LoginFailedException e) {

       return ResponseEntity.status(400).body(DefaultResponse.builder()
                .code(ResponseUtil.CANNOT_FIND_ACCOUNT)
                .title("Invalid credentials")
                .message("The username or password is incorrect.")
                .data(LoginRequestDTO.builder().build())
               .build()
        );
    }

  /*  @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Object> handleLoginFailedException(LoginFailedException e) {
        return ResponseEntity.badRequest().body(DefaultResponse.error("Invalid credentials", "The username or password is incorrect."));
    }*/



    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
