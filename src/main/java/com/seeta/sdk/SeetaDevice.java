package com.seeta.sdk;

import lombok.Getter;

@Getter
public enum SeetaDevice {
    SEETA_DEVICE_AUTO(0),
    SEETA_DEVICE_CPU(1),
    SEETA_DEVICE_GPU(2);

    private final int value;

    SeetaDevice(int value) {
        this.value = value;
    }
}