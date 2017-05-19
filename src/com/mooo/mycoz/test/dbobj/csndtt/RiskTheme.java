package com.mooo.mycoz.test.dbobj.csndtt;

/**
 * Created by zlei on 10/18/16.
 */

import com.mooo.mycoz.db.DBObject;

public class RiskTheme extends DBObject {
    private Integer id;
    private Integer riskQuestionnaireId;
    private Integer themeId;
    private String isRight;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getRiskQuestionnaireId() {
        return riskQuestionnaireId;
    }
    public void setRiskQuestionnaireId(Integer riskQuestionnaireId) {
        this.riskQuestionnaireId = riskQuestionnaireId;
    }
    public Integer getThemeId() {
        return themeId;
    }
    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }
    public String getIsRight() {
        return isRight;
    }
    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }
}
