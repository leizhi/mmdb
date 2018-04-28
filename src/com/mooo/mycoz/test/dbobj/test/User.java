package com.mooo.mycoz.test.dbobj.test;

/**
 * Created by zlei on 10/18/16.
 */

import com.mooo.mycoz.db.DBObject;
public class User extends DBObject{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
