package parser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ParentLink {
    @Getter
    @Setter
    private String idParent;
    @Getter
    @Setter
    private Integer dateDebut;
    @Getter
    @Setter
    private Integer dateFin;

    public ParentLink() {
    }

    public ParentLink(String codeUTParent, String dateDebut, String dateFin) {
        this.idParent = codeUTParent;
        this.dateDebut = Integer.parseInt(dateDebut.replaceAll("[^(0-9)]", ""));
        this.dateFin = Integer.parseInt(dateFin.replaceAll("[^(0-9)]", ""));
    }

}
