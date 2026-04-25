package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdatePasswordRequest;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.ProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController{

    private final ProfileService service;

    public ProfileController(ProfileService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> showProfile(){
        return ResponseEntity.ok(service.showUserData());
    }

    @PatchMapping
    public ResponseEntity<Void> updateUserData(@RequestBody ProfileUpdateRequest request){ //null is used.
        service.updateProfile(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updateUserPassword(@RequestBody @Valid ProfileUpdatePasswordRequest request){
        service.updatePassword(request);
        return ResponseEntity.noContent().build();
    }
}
