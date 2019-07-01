package parser.model.meta4;

import lombok.Getter;
import lombok.Setter;

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

    public ParentLink(String idParent, Integer startDate, Integer endDate) {
        this.idParent = idParent;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // FIXME A supprimer à terme (utilisé par ParseMeta4)
    public ParentLink(String codeUTParent, String dateDebut, String dateFin) {
        this.idParent = codeUTParent;
        this.startDate = Integer.parseInt(dateDebut.replaceAll("[^(0-9)]", ""));
        this.endDate = Integer.parseInt(dateFin.replaceAll("[^(0-9)]", ""));
    }
}
