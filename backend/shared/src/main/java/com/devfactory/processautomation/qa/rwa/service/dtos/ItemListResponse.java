package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemListResponse<T> extends ValidationBase {
    private List<T> items = new ArrayList<>();

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<T> input) {
        items = new ArrayList<>(input);
    }
}
