package com.example.wybory_demo.data.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO<T> {
    private T result;
    private String message;

    public ResultDTO(T result) {
        this.result = result;
    }
}
