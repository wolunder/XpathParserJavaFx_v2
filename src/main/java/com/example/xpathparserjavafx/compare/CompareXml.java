package com.example.xpathparserjavafx.compare;


import com.example.xpathparserjavafx.model.RegRecordOwner;

import java.io.FileNotFoundException;
import java.util.List;

public class CompareXml {
    //сравнение по кад выпискам
    public List<RegRecordOwner> compareRegRecordList(List<RegRecordOwner> newList, List<RegRecordOwner> oldList) throws NullPointerException, FileNotFoundException {

        for (int i = 0; i < newList.size(); i++) {
            for (int j = 0; j < oldList.size(); j++) {

                if (newList.get(i).getRegRecord().replaceAll(" ", "")
                        .equals(oldList.get(j).getRegRecord().replaceAll(" ", "")) && oldList.get(j).isChangeStatus()) {
                    if (newList.get(i).getOwner().contains("Физическое лицо")) {
                        newList.get(i).setOwner(oldList.get(j).getOwner());
                        oldList.get(j).setChangeStatus(false);
                        break;
                    } else {
                        oldList.get(j).setChangeStatus(false);
                    }
                }
            }
        }
        for (RegRecordOwner o : oldList) {
            if (o.isChangeStatus()) {
                o.setOwner("Нет совпадений - " + o.getOwner());
                newList.add(o);
            }
        }
        return newList;
    }

    // сравнение по переходу права
    public List<RegRecordOwner> compareTransferListAndRegRecordList(List<RegRecordOwner> transferList, List<RegRecordOwner> regRecordOwnerList) {
        for (int i = 0; i < transferList.size(); i++) {
            for (int j = 0; j < regRecordOwnerList.size(); j++) {

                if (transferList.get(i).getOwner().contains("Физическое лицо")) {
                    if (transferList.get(i).getRegRecord().trim()
                            .equals(regRecordOwnerList.get(j).getRegRecord().trim())
                            && regRecordOwnerList.get(j).isChangeStatus()) {
                        transferList.get(i).setOwner(regRecordOwnerList.get(j).getOwner());
                        transferList.get(i).setChangeStatus(false);
                        regRecordOwnerList.get(j).setChangeStatus(false);
                        break;
                    }
                } else {
                    regRecordOwnerList.get(j).setChangeStatus(false);
                    break;
                }
            }

        }
        return transferList;
    }

}
