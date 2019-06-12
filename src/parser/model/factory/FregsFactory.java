package parser.model.factory;

import org.modelmapper.ModelMapper;

import parser.model.Entite;
import parser.model.Filiere;
import parser.model.Groupe;
import parser.model.Regroupement;
import parser.model.Salarie;
import parser.model.StructureOrganisationnelle;
import parser.model.UniteTravail;

public class FregsFactory {
    private static ModelMapper mm = new ModelMapper();
    static {
        //        PropertyMap<UniteTravail, StructureOrganisationnelle> propertyMap = new PropertyMap<UniteTravail, StructureOrganisationnelle>() {
        //
        //            @Override
        //            protected void configure() {
        //                map().setCode(source.getCodeUT());
        //            }
        //        };
        //        mm.addMappings(propertyMap);
        //        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    public static StructureOrganisationnelle createFiliere(UniteTravail ut, Salarie manager) {

        Filiere so = mm.map(ut, Filiere.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;
    }

    public static Regroupement createRegroupement(UniteTravail ut, Salarie manager) {
        Regroupement so = mm.map(ut, Regroupement.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;
    }

    public static Entite createEntite(UniteTravail ut, Salarie manager) {
        Entite so = mm.map(ut, Entite.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;

    }

    public static Groupe createGroupe(UniteTravail ut, Salarie manager) {
        Groupe so = mm.map(ut, Groupe.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;

    }

}
