package dev.hyudoro.pay_my_buddy_service.utils;

public class EmailUtils{

    public static boolean isValid(String email){
        if (email == null || email.isBlank()) return false;
        return email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");
    }
}
