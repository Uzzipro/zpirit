package com.canatme.zpirit.Dataclasses;

public class CategoryDto {
    private String category;


    public CategoryDto()
    {

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CategoryDto(String category) {
        this.category = category;
    }
}
