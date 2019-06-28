package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.maif.autop.referentiels.fregs.api.io.salarie.ParametrageSalarie;
import fr.maif.autop.referentiels.fregs.api.io.salarie.contrat.Fonction;
import fr.maif.autop.referentiels.fregs.api.io.salarie.contrat.TempsTravail;
import fr.maif.autop.referentiels.fregs.api.io.salarie.type.TypeContrat;
import fr.maif.autop.referentiels.fregs.api.io.salarie.type.TypeHoraire;
import fr.maif.autop.referentiels.fregs.api.io.salarie.type.TypeTempsTravail;
import fr.maif.autop.referentiels.fregs.api.io.salarie.type.TypeVariabilite;
import fr.maif.autop.referentiels.fregs.model.common.Period;
import fr.maif.autop.referentiels.fregs.model.meta4.ObjectWithUtParent;
import fr.maif.autop.referentiels.fregs.model.meta4.ParentLink;
import fr.maif.autop.referentiels.fregs.model.salarie.PeriodWithSalarieProps;
import fr.maif.autop.referentiels.fregs.model.salarie.Salarie;
import fr.maif.autop.referentiels.fregs.util.PeriodsUtil;
import parser.model.Entite;
import parser.model.Groupe;
import parser.model.StructureOrganisationnelle;
import parser.model.UniteTravail;
import parser.model.factory.FregsFactory;
import parser.util.DateUtil;
import parser.util.TempsUtil;

public class ParseMeta4 {

    private static Map<String, UniteTravail> code2ut = new HashMap<>();
    private static Map<UniteTravail, List<UniteTravail>> parentUt2children = new HashMap<>();
    private static List<UniteTravail> rootUTs = new ArrayList<>();
    private static List<UniteTravail> allUts = new ArrayList<>();
    private static List<Salarie> allSalaries = new ArrayList<>();
    private static Map<UniteTravail, List<Salarie>> ut2salaries = new HashMap<>();
    private static Map<String, Salarie> matricule2salarie = new HashMap<>();
    private static Map<UniteTravail, Salarie> ut2manager = new HashMap<>();
    private static Map<String, String> matricule2utCode = new HashMap<>();

    private static final String pad = "\t";
    private static final Map<UniteTravail, StructureOrganisationnelle> utFregsMap = new HashMap<>();

    private static int maxDeep = 0;

    public static void main(String[] args) {
        //        File fXmlFile = new File("input/sirh-201901140800.xml");
        File fXmlFile = new File("input/sirh-201901311156.xml");
        DocumentBuilder dBuilder;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // UNITE DE TRAVAIL
            // First pass : create and register element
            NodeList unitesTravail = doc.getElementsByTagName("UniteTravail");

            for (int indexUT = 0; indexUT < unitesTravail.getLength(); indexUT++) {
                Element utMarkup = (Element) unitesTravail.item(indexUT);
                UniteTravail ut = new UniteTravail(
                        utMarkup.getAttribute("codeUT"),
                        //                        utMarkup.getAttribute("datedebut"),
                        //                        utMarkup.getAttribute("dateFin"),
                        utMarkup.getAttribute("libelle"),
                        utMarkup.getAttribute("codeSociete"),
                        utMarkup.getAttribute("codeEtablissement"));
                code2ut.put(ut.getCodeUT(), ut);
                allUts.add(ut);

            }

            for (int indexUT = 0; indexUT < unitesTravail.getLength(); indexUT++) {
                Element utMarkup = (Element) unitesTravail.item(indexUT);
                String codeUt = utMarkup.getAttribute("codeUT");
                linkWithUtParentForTags(utMarkup, code2ut.get(codeUt), "UTMere");
            }

            // transformation de la collection d'UTs en JSON
            objectToJsonFile(allUts, "target/uts.json");

            // SALARIE
            NodeList salaries = doc.getElementsByTagName("Salarie");
            for (int indexSalarie = 0; indexSalarie < salaries.getLength(); indexSalarie++) {
                Element salarieMarkup = (Element) salaries.item(indexSalarie);
                // Construction des paramètres à date
                List<Period> periods = new ArrayList<>();
                Map<String, Period> mapAtributeNamePeriod = new HashMap<>();
                Salarie salarie = new Salarie(
                        salarieMarkup.getAttribute("matricule"),
                        salarieMarkup.getAttribute("nomFamille"),
                        salarieMarkup.getAttribute("prenom"),
                        salarieMarkup.getAttribute("mail"));
                matricule2salarie.put(salarie.getMatricule(), salarie);
                allSalaries.add(salarie);
                linkWithUtParentForTags(salarieMarkup, salarie, "RattachementUniteTravail");
                // Rattachement UnitÃ© de travail
                if (salarieMarkup.getElementsByTagName("RattachementUniteTravail").getLength() > 0) {
                    String codeUt = ((Element) salarieMarkup.getElementsByTagName("RattachementUniteTravail").item(0)).getAttribute("codeUT");
                    if (code2ut.containsKey(codeUt)) {
                        UniteTravail uniteTravail = code2ut.get(codeUt);
                        if (!ut2salaries.containsKey(uniteTravail)) {
                            ut2salaries.put(uniteTravail, new ArrayList<>());
                        }
                        ut2salaries.get(uniteTravail).add(salarie);
                        matricule2utCode.put(salarie.getMatricule(), uniteTravail.getCodeUT());
                    } else {
                        System.err.println("Mother UT is not known for Salarie : " + salarie.getMatricule() + "->" + codeUt);
                    }
                }


                // CONTRAT
                TypeContrat typeContrat = null;
                TempsTravail tempsTravail = null;
                Fonction fonctionSalarie = null;

                if (salarieMarkup.getElementsByTagName("Contrat").getLength() > 0) {
                    Element contratMarkup = (Element) salarieMarkup.getElementsByTagName("Contrat").item(0);
                    String type = contratMarkup.getAttribute("type");
                    typeContrat = TypeContrat.valueOf(type);
                    
                    // Gestion des périodes
                    periods.add(Period.builder()
                                    .startDate(DateUtil.strDateToInteger(contratMarkup.getAttribute("dateDebut")))
                                    .endDate(DateUtil.strDateToInteger(contratMarkup.getAttribute("dateFin"))).build());
                    mapAtributeNamePeriod.put("Contrat", Period.builder()
                            .startDate(DateUtil.strDateToInteger(contratMarkup.getAttribute("dateDebut")))
                            .endDate(DateUtil.strDateToInteger(contratMarkup.getAttribute("dateFin"))).build());
                }
                if (salarieMarkup.getElementsByTagName("TempsTravail").getLength() > 0) {
                    Element tempTravailMarkup = ((Element) salarieMarkup.getElementsByTagName("TempsTravail").item(0));
                    tempsTravail = new TempsTravail();
                    String type = tempTravailMarkup.getAttribute("type");
                    tempsTravail.setType(TypeTempsTravail.valueOf(type));
                    String horaireContractuel = tempTravailMarkup.getAttribute("horaireContractuel");
                    if (horaireContractuel != null && !horaireContractuel.equals("")) {
                        tempsTravail.setHoraireContractuel(TempsUtil.strToQuardHeure(horaireContractuel));
                    }
                    String forfaitJourIndividuel = tempTravailMarkup.getAttribute("forfaitJourIndividuel");
                    if (forfaitJourIndividuel != null && !forfaitJourIndividuel.equals("")) {
                        tempsTravail.setForfaitJourIndividuel(Double.parseDouble(forfaitJourIndividuel));
                    }
                    String typeHoraire = tempTravailMarkup.getAttribute("typeHoraire");
                    tempsTravail.setTypeHoraire(TypeHoraire.valueOf(typeHoraire.toUpperCase().replaceAll(" ", "_")));
                    String typeVariabilite = tempTravailMarkup.getAttribute("typeVariabilite");
                    tempsTravail.setTypeVariabilite(TypeVariabilite.valueOf(typeVariabilite.toUpperCase().replaceAll(" ", "_")));
                    
                    // Gestion des périodes
                    periods.add(
                            Period.builder()
                                    .startDate(DateUtil.strDateToInteger(tempTravailMarkup.getAttribute("dateDebut")))
                                    .endDate(DateUtil.strDateToInteger(tempTravailMarkup.getAttribute("dateFin"))).build());
                    mapAtributeNamePeriod.put("TempsTravail", Period.builder()
                            .startDate(DateUtil.strDateToInteger(tempTravailMarkup.getAttribute("dateDebut")))
                            .endDate(DateUtil.strDateToInteger(tempTravailMarkup.getAttribute("dateFin"))).build());
                }
                if (salarieMarkup.getElementsByTagName("Fonction").getLength() > 0) {
                    Element fonctionMarkup = (Element) salarieMarkup.getElementsByTagName("Fonction").item(0);
                    fonctionSalarie = new Fonction();
                    String fonction = fonctionMarkup.getAttribute("libelle");
                    fonctionSalarie.setLibelle(fonction);
                    String code = fonctionMarkup.getAttribute("code");
                    fonctionSalarie.setCode(code);
                    // Gestion des périodes
                    periods.add(
                            Period.builder()
                                    .startDate(DateUtil.strDateToInteger(fonctionMarkup.getAttribute("dateDebut")))
                                    .endDate(DateUtil.strDateToInteger(fonctionMarkup.getAttribute("dateFin"))).build());
                    mapAtributeNamePeriod.put("Fonction", Period.builder()
                            .startDate(DateUtil.strDateToInteger(fonctionMarkup.getAttribute("dateDebut")))
                            .endDate(DateUtil.strDateToInteger(fonctionMarkup.getAttribute("dateFin"))).build());

                }
                
                List<Period> newPeriods = new ArrayList<>();
                periods.stream().sorted((p1, p2) -> p1.getStartDate() - p2.getStartDate()).forEach(p -> {
                    PeriodsUtil.addPeriod(newPeriods, p.getStartDate(), p.getStartDate());
                });

                // Transformation des Period en PeriodWithSalarieProps
                List<PeriodWithSalarieProps> salariePeriods = new ArrayList<>();
                newPeriods.forEach(p -> {
                    PeriodWithSalarieProps periodWithSalarieProps = new PeriodWithSalarieProps(p.getStartDate(), p.getEndDate());
                    periodWithSalarieProps.setValues(new ParametrageSalarie());
                    salariePeriods.add(periodWithSalarieProps);
                });
                
                for (PeriodWithSalarieProps p : salariePeriods) {
                    salarie.getProps().add(p);
                    Period contratPeriod = mapAtributeNamePeriod.get("Contrat");
                    if (contratPeriod.getStartDate() <= p.getStartDate() && contratPeriod.getEndDate() >= p.getEndDate()) {
                        p.getValues().setTypeContrat(typeContrat);
                    }

                    Period tempsTravailPeriod = mapAtributeNamePeriod.get("TempsTravail");
                    if (tempsTravailPeriod.getStartDate() <= p.getStartDate() && tempsTravailPeriod.getEndDate() >= p.getEndDate()) {
                        p.getValues().setTempsTravail(tempsTravail);
                    }

                    Period fonctionPeriod = mapAtributeNamePeriod.get("Fonction");
                    if (fonctionPeriod.getStartDate() <= p.getStartDate() && fonctionPeriod.getEndDate() >= p.getEndDate()) {
                        p.getValues().setFonction(fonctionSalarie);
                    }
                }

            }

            // transformation de la collection de salaries en JSON
            objectToJsonFile(allSalaries, "target/salaries.json");

            // Second pass : resolve hierarchy + manager
            for (int indexUT = 0; indexUT < unitesTravail.getLength(); indexUT++) {
                Element utMarkup = (Element) unitesTravail.item(indexUT);
                UniteTravail uniteTravailFille = code2ut.get(utMarkup.getAttribute("codeUT"));
                // Paranet UT
                NodeList parentNodes = utMarkup.getElementsByTagName("UTMere");
                if (parentNodes.getLength() == 1) {
                    UniteTravail uniteTravailMere = code2ut.get(((Element) parentNodes.item(0)).getAttribute("codeUT"));
                    if (uniteTravailMere != null) {
                        if (!parentUt2children.containsKey(uniteTravailMere)) {
                            parentUt2children.put(uniteTravailMere, new ArrayList<>());
                        }
                        parentUt2children.get(uniteTravailMere).add(uniteTravailFille);
                    } else {
                        System.err.println(
                                "Mother UT is not known for UT : " + utMarkup.getAttribute("codeUT") + "->" + ((Element) parentNodes.item(0))
                                        .getAttribute("codeUT"));
                        rootUTs.add(uniteTravailFille);
                    }
                } else {
                    rootUTs.add(uniteTravailFille);
                }
                // Manager
                NodeList managers = utMarkup.getElementsByTagName("Manager");
                // TODO : gestion multimanagers + date
                if (managers.getLength() == 1) {
                    String managerMatricule = ((Element) managers.item(0)).getAttribute("matricule");
                    if (matricule2salarie.containsKey(managerMatricule)) {
                        ut2manager.put(uniteTravailFille, matricule2salarie.get(managerMatricule));
                    } else {
                        System.err.println("UT manager is not known : " + utMarkup.getAttribute("codeUT") + "->" + managerMatricule);
                    }
                } else {
                    System.err.println("UT without manager : " + utMarkup.getAttribute("codeUT"));
                }
            }
            System.out.println(unitesTravail.getLength() + " unitÃ©s de travail");
            System.out.println(salaries.getLength() + " salariÃ©s");

            //            for (UniteTravail uniteTravail : rootUTs) {
            //                printUt(uniteTravail, "", 1);
            //            }
            //            for (UniteTravail uniteTravail : rootUTs) {
            //                createHtmlTree(uniteTravail);
            //            }

            // System.out.println(createMongoCollectionUts(rootUTs));

            // createMongoCollectionSalarie();

            // Création de l'arbre des FREGS
            // createFregsTree();

            //            System.out.println("MaxDeep : " + maxDeep);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void linkWithUtParentForTags(Element element, ObjectWithUtParent objectModel, String tagname) {
        NodeList utChildrenNodeList = element.getElementsByTagName(tagname);
        for (int idxUtChildrenNodeList = 0; idxUtChildrenNodeList < utChildrenNodeList.getLength(); idxUtChildrenNodeList++) {
            Element utChildElement = (Element) utChildrenNodeList.item(idxUtChildrenNodeList);

            String codeUt = utChildElement.getAttribute("codeUT");
            UniteTravail ut = code2ut.get(codeUt);
            if (ut != null) {
                ParentLink utParent = new ParentLink(ut.getIdentifiant(), utChildElement.getAttribute("dateDebut"),
                        utChildElement.getAttribute("dateFin"));
                objectModel.getUtParentLinks().add(utParent);
            }

        }
    }

    private static void objectToJsonFile(Object object, String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String createMongoCollectionSalarie() {
        System.out.println("[");
        boolean isFirst = true;
        for (String matricule : matricule2salarie.keySet()) {
            if (!isFirst) {
                System.out.println(",");
            }
            Salarie salarie = matricule2salarie.get(matricule);
            //            System.out.print(
            //                    "\t{\"matricule\":\"" + matricule
            //                            + "\",\"prenom\":\"" + salarie.getPrenom()
            //                            + "\",\"nom\":\"" + salarie.getNom()
            //                            + "\",\"mail\":\"" + salarie.getMail()
            //                            + "\",\"codeUtOrigine\":\"" + matricule2utCode.get(matricule)
            //                            + "\",\"typeContrat\":\"" + salarie.getTypeContrat()
            //                            + (salarie.getHoraireContractuel() != null ? "\",\"horaireContractuel\":\"" + salarie.getHoraireContractuel() : "")
            //                            + (salarie.getForfaitJourIndividuel() != null ? "\",\"forfaitJourIndividuel\":\"" + salarie.getForfaitJourIndividuel()
            //                                    : "")
            //                            + "\",\"typeHoraire\":\"" + salarie.getTypeHoraire()
            //                            + "\",\"typeVariabilite\":\"" + salarie.getTypeVariabilite()
            //                            + "\",\"fonctionCode\":\"" + salarie.getFonctionCode()
            //                            + "\",\"fonctionLibelle\":\"" + salarie.getFonctionLibelle()
            //                            + "\"}");
            isFirst = false;
        }
        System.out.println("\n]");
        return null;
    }

    private static String createMongoCollectionUts(List<UniteTravail> uts) {
        StringBuilder jsonObject = new StringBuilder();
        for (UniteTravail uniteTravail : uts) {
            boolean isRoot = rootUTs.contains(uniteTravail);
            if (!isRoot && uts.indexOf(uniteTravail) != 0) {
                jsonObject.append(",");
            }
            jsonObject.append("{\"codeUT\":\"" + uniteTravail.getCodeUT() + "\",\"libelle\":\"" + uniteTravail.getLibelle() + "\"");
            if (parentUt2children.containsKey(uniteTravail)) {
                jsonObject.append(",\"children\":[");
                jsonObject.append(createMongoCollectionUts(parentUt2children.get(uniteTravail)));
                jsonObject.append("]");
            }
            jsonObject.append("}");
            if (isRoot) {
                jsonObject.append("\n");
            }
        }
        return jsonObject.toString();
    }

    private static void createHtmlTree(UniteTravail uniteTravail) {
        String item = "<b>" + uniteTravail.getLibelle() + "</b>";
        if (ut2salaries.containsKey(uniteTravail)) {
            int numberOfSalaries = ut2salaries.get(uniteTravail).size();
            item += " (" + numberOfSalaries + " salariÃ©" + (numberOfSalaries > 1 ? "s" : "") + " direct" + (numberOfSalaries > 1 ? "s" : "") + ")";
        }
        int totalSalarie = getTotalSalaries(uniteTravail);
        item += " (" + totalSalarie + " salariÃ©" + (totalSalarie > 1 ? "s" : "") + " au total" + ")";
        item += " (" + "sous UTs : " + (getSousUTs(uniteTravail) - 1) + ")";
        item += " (" + "profondeur : " + getDeep(uniteTravail, 0) + ")";
        System.out.print("<li>");
        if (parentUt2children.containsKey(uniteTravail)) {
            System.out.println("<span class=\"caret\">" + item + "</span>");
            System.out.println("<ul class=\"nested\">");
            for (UniteTravail uniteTravailFille : parentUt2children.get(uniteTravail)) {
                createHtmlTree(uniteTravailFille);
            }
            System.out.println("</ul>");
        } else {
            System.out.print(item);
        }
        System.out.println("</li>");
    }

    private static void createFregsTree() {
        createFregsTree(code2ut.get("2337")); // codeUT="2337" "DRSOC - Direction Relation Sociétaire"
    }

    private static void createFregsTree(UniteTravail rootUT) {
        List<UniteTravail> children = parentUt2children.get(rootUT);
        if (children != null) {
            for (UniteTravail child : children) {
                createFregsTree(rootUT, child, 1);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                List<StructureOrganisationnelle> sos = children.stream().map(ut -> utFregsMap.get(ut)).collect(Collectors.toList());
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("target/fregs.json"), sos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createFregsTree(UniteTravail parentUT, UniteTravail ut, int level) {
        createFreg(parentUT, ut, level);
        List<UniteTravail> children = parentUt2children.get(ut);
        if (children != null) {
            level = level + 1;
            for (UniteTravail child : children) {
                createFregsTree(ut, child, level);
            }
        }

    }

    private static void createFreg(UniteTravail parentUT, UniteTravail ut, int level) {
        switch (level) {
            case 1:
                StructureOrganisationnelle filiere = FregsFactory.createFiliere(ut, ut2manager.get(ut));
                utFregsMap.put(ut, filiere);
                break;
            case 2:
                List<UniteTravail> children = parentUt2children.get(ut);
                StructureOrganisationnelle so = null;
                if (children != null && children.size() > 0) {
                    so = FregsFactory.createRegroupement(ut, ut2manager.get(ut));
                    utFregsMap.put(ut, so);
                } else {
                    so = FregsFactory.createEntite(ut, ut2manager.get(ut));
                    utFregsMap.put(ut, so);
                    so.addSalaries(ut2salaries.get(ut));
                }
                utFregsMap.get(parentUT).addChild(so);

                break;
            case 3:
                Entite entite = FregsFactory.createEntite(ut, ut2manager.get(ut));
                utFregsMap.put(ut, entite);
                utFregsMap.get(parentUT).addChild(entite);
                entite.addSalaries(ut2salaries.get(ut));
                break;
            case 4:
                Groupe groupe = FregsFactory.createGroupe(ut, ut2manager.get(ut));
                utFregsMap.put(ut, groupe);
                utFregsMap.get(parentUT).addChild(groupe);
                groupe.addSalaries(ut2salaries.get(ut));
                break;
            case 5:
                List<Salarie> salaries = ut2salaries.get(ut);
                if (salaries != null) {
                    utFregsMap.get(parentUT).addSalaries(salaries);
                }

                break;

            default:
                break;
        }

    }

    private static int getTotalSalaries(UniteTravail uniteTravail) {
        int numberOfSalaries = 0;
        if (ut2salaries.containsKey(uniteTravail)) {
            numberOfSalaries = ut2salaries.get(uniteTravail).size();
        }
        if (parentUt2children.containsKey(uniteTravail)) {
            for (UniteTravail uniteTravailFille : parentUt2children.get(uniteTravail)) {
                numberOfSalaries += getTotalSalaries(uniteTravailFille);
            }
        }
        return numberOfSalaries;
    }

    private static int getSousUTs(UniteTravail uniteTravail) {
        int deep = 1;
        if (parentUt2children.containsKey(uniteTravail)) {
            for (UniteTravail uniteTravailFille : parentUt2children.get(uniteTravail)) {
                deep += getSousUTs(uniteTravailFille);
            }
        }
        return deep;
    }

    private static int getDeep(UniteTravail uniteTravail, int deep) {
        int maxDeep = deep;
        if (parentUt2children.containsKey(uniteTravail)) {
            for (UniteTravail uniteTravailFille : parentUt2children.get(uniteTravail)) {
                int currentDeep = getDeep(uniteTravailFille, deep + 1);
                if (maxDeep < currentDeep) {
                    maxDeep = currentDeep;
                }
            }
        }
        return maxDeep;
    }

    private static void printUt(UniteTravail uniteTravail, String padding, int deep) {
        if (maxDeep < deep) {
            maxDeep = deep;
        }
        System.out.print(padding + "> UT-" + uniteTravail);
        if (ut2manager.containsKey(uniteTravail)) {
            System.out.print(" -> " + ut2manager.get(uniteTravail).getMatricule());
        }
        System.out.println();
        if (ut2salaries.containsKey(uniteTravail)) {
            for (Salarie salarie : ut2salaries.get(uniteTravail)) {
                System.out.println(padding + pad + "- SL-" + salarie);
            }
        }
        if (parentUt2children.containsKey(uniteTravail)) {
            for (UniteTravail childUniteTravail : parentUt2children.get(uniteTravail)) {
                printUt(childUniteTravail, padding + pad, deep + 1);
            }
        }
    }
}
