package com.seeta.sdk;

import java.io.File;
import java.io.FileNotFoundException;

public class SeetaModelSetting {

    // when device is GPU, id means GPU id
    public final int id;
    public final String[] model;
    public final SeetaDevice device;

    public SeetaModelSetting(String[] models, SeetaDevice dev) throws FileNotFoundException {
        this(models, dev, true);
    }

    public SeetaModelSetting(String[] models, SeetaDevice dev, boolean validated) throws FileNotFoundException {
        this(0, models, dev, validated);
    }

    public SeetaModelSetting(int id, String[] models, SeetaDevice dev) throws FileNotFoundException {
        this(id, models, dev, true);
    }

    public SeetaModelSetting(int id, String[] models, SeetaDevice dev, boolean validated) throws FileNotFoundException {
        this.id = id;
        this.device = dev;
        this.model = new String[models.length];

        for (int i = 0; i < models.length; i++) {
            //添加验证
            File file = new File(models[i]);
            if (!file.exists()) {
                if (validated) {
                    throw new FileNotFoundException("模型文件没有找到！");
                }
            }
            this.model[i] = models[i];
        }
    }
}