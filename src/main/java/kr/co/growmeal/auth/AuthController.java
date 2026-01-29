package kr.co.growmeal.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        System.out.println("[회원가입] email: " + request.get("email"));
        System.out.println("[회원가입] phoneNumber: " + request.get("phoneNumber"));
        System.out.println("[회원가입] password: " + request.get("password"));
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        System.out.println("[로그인] email: " + request.get("email"));
        System.out.println("[로그인] password: " + request.get("password"));
        return ResponseEntity.ok(Map.of("accessToken", "dummy-token"));
    }
}
