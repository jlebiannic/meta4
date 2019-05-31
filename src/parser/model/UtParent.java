package parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UtParent {
    @Getter
    @Setter
    private String codeUTParent;
    @Getter
    @Setter
    private String dateDebut;
    @Getter
    @Setter
    private String dateFin;

}
