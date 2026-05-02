package dev.hyudoro.pay_my_buddy_service.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.hyudoro.pay_my_buddy_service.dto.RegisterRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.AuthService;
import jakarta.validation.Valid;

@Controller
public class AuthViewController{
    private final AuthService authService;

    public AuthViewController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Model model, Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()){
            return "redirect:/transfer";
        }
        model.addAttribute("isLoginPage",true);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()){
            return "redirect:/transfer";
        }
        model.addAttribute("isRegisterPage",true);
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getAllErrors()
                .getFirst()
                .getDefaultMessage();
            redirectAttributes.addFlashAttribute("error",errorMessage);
            return "redirect:/register";}

        try {
            authService.register(request);
        } catch(Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/register";
    }
}
