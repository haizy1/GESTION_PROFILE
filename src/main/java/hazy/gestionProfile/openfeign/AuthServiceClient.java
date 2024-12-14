package hazy.gestionProfile.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "http://localhost:1010")
public interface AuthServiceClient {
    @GetMapping("/auth/user-details")
    ReqRes getUserDetails(@RequestHeader("Authorization") String token);
}



