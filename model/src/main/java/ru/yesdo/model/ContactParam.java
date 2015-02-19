package ru.yesdo.model;

/**
 * User: Krainov
 * Date: 13.02.2015
 * Time: 17:44
 */
public class ContactParam {

    public final static String FIRST_NAME_PARAM = "first_name";
    public final static String SECOND_NAME_PARAM = "second_param";
    public final static String DATE_BIRTHDAY_PARAM = "date_birthday";
    public final static String EMAIL_PARAM = "email";
    public final static String PHONE_PARAM = "phone";
    public final static String ADDRESS_PARAM = "address";
    public final static String CITY_PARAM = "city";
    public final static String UNDERGROUND_PARAM = "underground";

    public enum Type {
        GEO,
        PROFILE,
        ADDRESS
    }

    private String name;
    private Object value;
    private boolean isPrivate;
    private Type paramType;

    private ContactParam(){}
    public ContactParam(String name, Object value, Type paramType) {
        this(name, value, paramType,false);
    }
    public ContactParam(String name, Object value, Type paramType, boolean isPrivate) {
        this.name = name;
        this.value = value;
        this.isPrivate = isPrivate;
        this.paramType = paramType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public Type getParamType() {
        return paramType;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setValue(Object value) {
        this.value = value;
    }

    protected void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    protected void setParamType(Type paramType) {
        this.paramType = paramType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactParam that = (ContactParam) o;

        if (!name.equals(that.name)) return false;
        if (paramType != that.paramType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + paramType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContactParam");
        sb.append("{name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append(", isPrivate=").append(isPrivate);
        sb.append(", paramType=").append(paramType);
        sb.append('}');
        return sb.toString();
    }

    public static interface ContactConditional {
        public boolean check(ContactParam param);
    }
}
