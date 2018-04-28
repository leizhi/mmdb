package com.mooo.mycoz.test;

import com.mooo.mycoz.test.dbobj.test.User;

public class Test {

    public void t(){
        try {
            for (;;){
                try {
                    User riskTheme = new User();
                    riskTheme.retrieve();
                    System.out.println("["+riskTheme.getName()+"]");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            User user = new User();
//            user.searchAndRetrieveList();
//            Method getMethod = user.getClass().getMethod("getName");
//
//            Object columnValue = getMethod.invoke(user);
//            System.out.println("["+columnValue+"]");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Test test = new Test();
        test.t();
//        String val = StringUtils.class.getSimpleName();
//        System.out.println(StringUtils.humpToSplit(val,"_"));
    }
}
