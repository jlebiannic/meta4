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
    private Integer startDate;
    @Getter
    @Setter
    private Integer endDate;

    public ParentLink() {
    }

    public ParentLink(String codeUTParent, String dateDebut, String dateFin) {
        this.idParent = codeUTParent;
        this.startDate = Integer.parseInt(dateDebut.replaceAll("[^(0-9)]", ""));
        this.endDate = Integer.parseInt(dateFin.replaceAll("[^(0-9)]", ""));
    }

}
