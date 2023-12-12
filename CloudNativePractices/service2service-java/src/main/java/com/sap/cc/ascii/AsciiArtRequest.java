package com.sap.cc.ascii;

public class AsciiArtRequest {
    private String toConvert;
    private String fontId;

    public  AsciiArtRequest(String toConvert,String fontId){
        this.toConvert = toConvert;
        this.fontId = fontId;
    }

    public void set_toConvert(String toConvert){
        this.toConvert = toConvert;
    }
    public void set_fontId(String fontId){
        this.fontId = fontId;
    }

    public String get_toConvert(){
        return this.toConvert;
    }
    public String get_fontId(){
        return  this.fontId;
    }
}
