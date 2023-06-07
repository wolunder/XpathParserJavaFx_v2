package com.example.xpathparserjavafx.exception;

public class ParserFormatException extends Exception{
    @Override
    public String toString() {
        return "Выбран другой тип парсера";
    }
}
