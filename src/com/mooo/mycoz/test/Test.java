package com.mooo.mycoz.test;

import com.mooo.mycoz.common.StringUtils;

import java.lang.reflect.Method;

public class Test {

    public class User{

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public void t(){
        try {
            User user = new User();
            user.setName(null);
            Method getMethod = user.getClass().getMethod("getName");

            Object columnValue = getMethod.invoke(user);
            System.out.println("["+columnValue+"]");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {
//        Test test = new Test();
//        test.t();
        String val = StringUtils.class.getSimpleName();
        System.out.println(StringUtils.humpToSplit(val,"_"));
    }
}
