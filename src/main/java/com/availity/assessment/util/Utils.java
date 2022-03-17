package com.availity.assessment.util;

import java.util.Stack;

public class Utils {
    public static boolean checkParentheses(String str) {
        Stack<Character> stack = new Stack<>();
        if(str.isBlank()) return false;
        for(int i=0; i< str.length(); i++) {
            if(str.charAt(i) == '(') {
                stack.push(str.charAt(i));
            }
            else if(str.charAt(i) == ')') {
                if(stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
            else {
                continue;
            }
        }
        if(!stack.isEmpty()) return false;
        return true;
    }
}
