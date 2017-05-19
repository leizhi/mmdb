package com.mooo.mycoz.test.dbobj.wineBranch;

/**
 * Created by zlei on 10/18/16.
 */

import com.mooo.mycoz.db.DBObject;
public class User extends DBObject{
    private Integer id;
    private String name;
    private String password;
    private String alias;
    private String active;
    private Integer branchId;
    private Integer typeId;
    private String mobile;
    private String uuid;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }
    public Integer getBranchId() {
        return branchId;
    }
    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
    public Integer getTypeId() {
        return typeId;
    }
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
