package pl.twojaFirma.ipapviewer.ui;

import javafx.util.StringConverter;
import pl.twojaFirma.ipapviewer.model.Category;

public class CategoryStringConverter extends StringConverter<Category> {
    @Override
    public String toString(Category object) {
        return object == null ? "" : object.name();
    }

    @Override
    public Category fromString(String string) {
        return null;
    }
}
