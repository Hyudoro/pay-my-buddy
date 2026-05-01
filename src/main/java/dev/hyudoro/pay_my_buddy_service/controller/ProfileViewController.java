package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.hyudoro.pay_my_buddy_service.dto.ProfileUpdateRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.ProfileService;


@Controller
public class ProfileViewController{
    private final ProfileService profileService;

    public ProfileViewController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model){
        model.addAttribute("profile",profileService.showUserData());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateUserData(@ModelAttribute ProfileUpdateRequest request,
                                 RedirectAttributes redirectAttributes){
        try{
            profileService.updateProfile(request);
        } catch(Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/profile";
    }


}
