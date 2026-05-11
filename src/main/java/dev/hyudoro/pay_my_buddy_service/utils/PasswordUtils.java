package dev.hyudoro.pay_my_buddy_service.utils;

public class PasswordUtils{
    public static boolean isValid(String password){
        if(password == null || password.isBlank()) return false;
        //(?=.*?[condition]) is a lazy lookahead so that the first pattern ".*" doesnt go through the last element first then
        //backtracks concurrently with the second pattern [condition].
        return password.matches("(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[!@#$%^&*()~`|/\\?.,']).{12,}");
    }
}
