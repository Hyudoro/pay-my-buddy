package dev.hyudoro.pay_my_buddy_service.service.inter;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileResponse;
import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateDataRequest;

public interface ProfileService{
    ProfileResponse showUserData();
    void updateProfile(ProfileUpdateDataRequest request);
    }
