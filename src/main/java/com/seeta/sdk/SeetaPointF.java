package com.seeta.sdk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeetaPointF {
    public double x;
    public double y;

    @Override
    public String toString() {
        return "SeetaPointF{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
