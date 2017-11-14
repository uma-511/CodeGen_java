package com.warrior.gen;

import com.google.gson.Gson;
import com.warrior.gen.model.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String args []) throws IOException {
        CodeGen codeGen = new CodeGen();
        String configPath = codeGen.getRunPath("config.json");
        String json = FileUtils.readFileToString(new File(configPath),"UTF-8");
        Config config = new Gson().fromJson(json,Config.class);

        codeGen.genCode(config);
    }
}
