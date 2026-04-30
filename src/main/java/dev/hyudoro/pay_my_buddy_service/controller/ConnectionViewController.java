package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.hyudoro.pay_my_buddy_service.dto.ConnectionRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;
import jakarta.validation.Valid;


@Controller
public class ConnectionViewController{
    private final ConnectionService connectionService;

    public ConnectionViewController(ConnectionService service){
        this.connectionService = service;
    }

    @GetMapping("/connections")
    public String connectionPage(){
        return "connections";
    }

    @PostMapping("/connections")
    public String addConnection(@Valid @ModelAttribute ConnectionRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getAllErrors()
                .get(0)
                .getDefaultMessage();
            redirectAttributes.addFlashAttribute("error",errorMessage);
            return "redirect:/connections";}

        try{
            connectionService.addConnection(request);
        }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/connections";
    }
}
