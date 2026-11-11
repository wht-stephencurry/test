package com.elder.preconsult.vo;

/**
 * 量表列表出参。只暴露前端需要的字段，
 * 禁止把 entity 的 id / status / createTime 等内部字段直接返回（AGENT.md 硬性规则 B-1）。
 */
public class ScaleVO {

    private String code;
    private String name;
    private String category;
    private String scoringType;
    private Integer itemCount;

    public ScaleVO() {
    }

    public ScaleVO(String code, String name, String category, String scoringType, Integer itemCount) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.scoringType = scoringType;
        this.itemCount = itemCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getScoringType() {
        return scoringType;
    }

    public void setScoringType(String scoringType) {
        this.scoringType = scoringType;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}
