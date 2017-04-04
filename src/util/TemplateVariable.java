package it.menzani.yiupp.util;

public final class TemplateVariable {

    private final CharSequence name;
    private final Object value;

    public TemplateVariable(String name, Object value) {
        this.name = '{' + name.toUpperCase() + '}';
        this.value = value;
    }

    public String fill(String text) {
        return text.replace(name, expand());
    }

    public String expand() {
        return value.toString();
    }

}
