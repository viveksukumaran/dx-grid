package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PagedResponse<T> extends ValidationBase {
    private List<T> items = new ArrayList<>();
    private int totalElements;

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<T> input) {
        if (input != null) {
            items = new ArrayList<>(input);
        }
    }
}
