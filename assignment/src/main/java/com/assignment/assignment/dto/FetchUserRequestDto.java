package com.assignment.assignment.dto;

import java.util.List;

public class FetchUserRequestDto {
    private List<Long> id;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }
}
