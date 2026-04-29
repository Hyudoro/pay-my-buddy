package dev.hyudoro.pay_my_buddy_service.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.hyudoro.pay_my_buddy_service.dto.TransactionRequest;
import dev.hyudoro.pay_my_buddy_service.service.inter.ConnectionService;
import dev.hyudoro.pay_my_buddy_service.service.inter.TransactionService;
import jakarta.validation.Valid;

@Controller
public class TransferController {
    private final ConnectionService connectionService;
    private final TransactionService transactionService;

    public TransferController(ConnectionService connectionService, TransactionService transactionService){
        this.connectionService = connectionService;
        this.transactionService = transactionService;
    }

    @GetMapping("/transfer")
    public String transferPage(Model model){
        model.addAttribute("connections", connectionService.listConnection());
        model.addAttribute("transactions", transactionService
                           .listTransaction(PageRequest.of(0,10)));
        return "transfer";
    }

    @PostMapping("/transfer")
    public String makeTransfer(@Valid @ModelAttribute TransactionRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getAllErrors()
                .get(0)
                .getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", "Invalid amount.");
            return "redirect:/transfer";
        }
        try{
            transactionService.makeTransaction(request);
            }
        catch(Exception e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/transfer";
    }
}
