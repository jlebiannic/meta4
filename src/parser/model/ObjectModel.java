package parser.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.Setter;
import parser.model.meta4.Ut;
import parser.model.salarie.Salarie;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonTypeInfo(use = Id.NAME, property = "type", include = As.EXTERNAL_PROPERTY)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = Salarie.class, name = Salarie.TYPE),
        @JsonSubTypes.Type(value = Ut.class, name = Ut.TYPE)
})
public abstract class ObjectModel {
    public static final Map<String, Class<? extends ObjectModel>> types = Map.of(
            Salarie.TYPE,
            Salarie.class,
            Ut.TYPE,
            Ut.class);

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    @JsonIgnore
    private ObjectModel parent;

    @Getter
    @Setter
    private boolean readOnly;

    public ObjectModel(String type) {
        super();
        this.type = type;
    }

    @JsonIgnore
    public abstract List<ObjectModel> getChildren();

    public abstract void addChild(ObjectModel so);

    public abstract void removeChildren();

    public abstract void removeChild(ObjectModel so);

    public abstract boolean hasChildren();

    public abstract void copyChildren(ObjectModel so);

}
