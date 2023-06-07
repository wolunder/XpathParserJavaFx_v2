package com.example.xpathparserjavafx.parser;

import lombok.Getter;

@Getter
public class LabelStringPDF {
    private final String beginLabelOwner = "(правообладатели):";
    private final String endLabelOwner = "Сведения о возможности предоставления третьим лицам";

    private final String beginLabelRegRecord = "права:";
    private final String endLabelRegRecord = "3 Сведения об осуществлении государственной";

    private final String endLabelEncumbrance = "4 Ограничение прав и обременение объекта недвижимости:";

    private final String beginLabelData = "дата государственной регистрации: ";
    private final String endLabelData = "номер государственной регистрации: ";

    private final String endLabelRegEncum = "срок, на который установлены ограничение прав и";

    private final String beginLabelDuration = "обременение объекта недвижимости:";
    private final String endLabelDuration = "лицо, в пользу которого установлены ограничение";

    private final String beginLabelMark = "ДОКУМЕНТ ПОДПИСАН";
    private final String endLabelMark = "Действителен: c 17.05.2022 по 10.08.2023";

    private final String beginLabelEncumOwner = "прав и обременение объекта недвижимости:";
    private final String endLabelEncumOwner = "сведения о возможности предоставления третьим";

    private final String beginLabelDocFound = "основание государственной регистрации:";
    private final String endLabelDocFound = "сведения об осуществлении государственной";

}
