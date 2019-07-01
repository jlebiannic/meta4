package parser.model.factory;

import org.modelmapper.ModelMapper;

import parser.model.Entite;
import parser.model.Filiere;
import parser.model.Groupe;
import parser.model.Regroupement;
import parser.model.StructureOrganisationnelle;
import parser.model.meta4.Ut;
import parser.model.salarie.Salarie;

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

    public static StructureOrganisationnelle createFiliere(Ut ut, Salarie manager) {

        Filiere so = mm.map(ut, Filiere.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;
    }

    public static Regroupement createRegroupement(Ut ut, Salarie manager) {
        Regroupement so = mm.map(ut, Regroupement.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;
    }

    public static Entite createEntite(Ut ut, Salarie manager) {
        Entite so = mm.map(ut, Entite.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;

    }

    public static Groupe createGroupe(Ut ut, Salarie manager) {
        Groupe so = mm.map(ut, Groupe.class);
        if (manager != null) {
            so.setReferentMatricule(manager.getMatricule());
        }
        return so;

    }

}
