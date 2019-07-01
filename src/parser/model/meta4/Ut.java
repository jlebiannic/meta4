package parser.model.meta4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import parser.model.salarie.Salarie;

public class Ut extends ObjectWithUtParent {
    public static final String TYPE = "Ut";

    @Getter
    @Setter
    private String identifiant;

    @Getter
    @Setter
    private String codeUT;
    @Getter
    @Setter
    private String libelle;

    @Getter
    @Setter
    @JsonIgnore
    private String codeSociete;
    @Getter
    @Setter
    @JsonIgnore
    private String codeEtablissement;

    @Getter
    @Setter
    private List<Ut> uts = new ArrayList<>();
    @Getter
    @Setter
    private List<Salarie> salaries = new ArrayList<>();

    public Ut() {
        super(TYPE);
        this.identifiant = UUID.randomUUID().toString();
    }

    public Ut(String code) {
        this();
        this.codeUT = code;
    }

    public Ut(String codeUT, String libelle, String codeSociete, String codeEtablissement) {
        this(codeUT);
        this.libelle = libelle;
        this.codeSociete = codeSociete;
        this.codeEtablissement = codeEtablissement;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void addSortedUt(Ut ut) {
        int index = Collections.binarySearch(this.uts, ut, Comparator.comparing(Ut::getLibelle));
        if (index < 0) {
            index = -index - 1;
        }
        this.uts.add(index, ut);
    }

    public void addSortedSalarie(Salarie salarie) {
        int index = Collections.binarySearch(this.salaries, salarie, Comparator.comparing(Salarie::getNom));
        if (index < 0) {
            index = -index - 1;
        }
        this.salaries.add(index, salarie);
    }
}
