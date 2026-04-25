package dev.hyudoro.pay_my_buddy_service.service.inter;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdatePasswordRequest;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateRequest;

public interface ProfileService{
    ProfileResponse showUserData();
    void updateProfile(ProfileUpdateRequest request);
    void updatePassword(ProfileUpdatePasswordRequest request);
    }
