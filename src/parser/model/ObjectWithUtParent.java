package parser.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class ObjectWithUtParent {

    @Getter
    @Setter
    private List<ParentLink> utParentLinks = new ArrayList<>();


}
