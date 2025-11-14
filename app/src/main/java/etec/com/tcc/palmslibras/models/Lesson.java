package etec.com.tcc.palmslibras.models;

import java.io.Serializable;

// Esta é a classe do MÓDULO, que corresponde à tabela LessonEntry.
public class Lesson implements Serializable {
    private long id;
    private String title;
    private String description;
    private int orderIndex;

    public Lesson(long id, String title, String description, int orderIndex) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    // ... (Getters) ...
        public long getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public int getOrderIndex(){
        return orderIndex;
    }
}