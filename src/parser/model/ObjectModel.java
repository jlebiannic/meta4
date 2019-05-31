package parser.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class ObjectModel {
    @Getter
    @Setter
    private List<UtParent> utParents = new ArrayList<>();
}
