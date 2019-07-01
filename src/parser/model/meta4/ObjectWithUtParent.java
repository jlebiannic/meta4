package parser.model.meta4;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import parser.model.IWithIdentifiant;
import parser.model.ObjectModel;

public abstract class ObjectWithUtParent extends ObjectModel implements IWithIdentifiant {

    @Getter
    @Setter
    private List<ParentLink> utParentLinks = new ArrayList<>();

    @Getter
    @Setter
    @JsonIgnore
    private List<ParentLink> soParentLinks = new ArrayList<>();

    @Getter
    @Setter
    @JsonIgnore
    private String linkedParentId;

    public ObjectWithUtParent(String type) {
        super(type);
    }

    @Override
    public List<ObjectModel> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addChild(ObjectModel so) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeChildren() {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeChild(ObjectModel so) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void copyChildren(ObjectModel so) {
        // TODO Auto-generated method stub

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
