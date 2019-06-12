package parser.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class UniteTravail extends ObjectWithUtParent {
    //	<UniteTravail codeUT="0020" datedebut="1999-09-01" dateFin="4000-01-01" libelle="XX - Direction" codeSociete="001" codeEtablissement="S7901">
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
	private String codeUT;
    @Getter
    @Setter
	private String dateDebut;
    @Getter
    @Setter
	private String dateFin;
    @Getter
    @Setter
	private String libelle;
    @Getter
    @Setter
	private String codeSociete;
    @Getter
    @Setter
	private String codeEtablissement;

    public UniteTravail() {
        super();
        this.id = UUID.randomUUID().toString();
    }

    public UniteTravail(String codeUT, String libelle, String codeSociete, String codeEtablissement) {
        this();
        this.codeUT = codeUT;
        this.libelle = libelle;
        this.codeSociete = codeSociete;
        this.codeEtablissement = codeEtablissement;
    }

}
