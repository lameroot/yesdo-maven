package ru.yesdo.model;

/**
 * Created by lameroot on 13.12.14.
 * Тэги которые может создавать как мерчант, так и пользователь, привязывая их к своему продукту или ещё куда-то.
 * Для администратора приложения должна быть отдельная вкладка, где можно просматривать облако тэгов, каким оразом они привязаны к пользователям.
 * Делать на neo4j + spring-data
 * !!!На данном этапе использовать как заглушку, предусмотреть только вкладку для тэгов, где можно просто выводить их списком
 */
public class Tag {

    private Long id;
    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tag{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
