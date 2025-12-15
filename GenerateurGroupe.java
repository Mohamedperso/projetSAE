import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GenerateurGroupe {

    private static final int MIN_FILLES = 4;
    private static final int SEUIL_FORCE_BRUTE = 12; // En dessous de 12 étudiants, on peut faire du Force Brute
                                                     // rapidement

    /*
     * C'est cette fonction que l'application doit appeler.
     * Elle choisit automatiquement le meilleur algo selon la taille de la promo.
     */

    public List<Groupe> genererGroupesAutomatique(List<Etudiant> promo, int nbGroupeVoulus) {
        if (promo.size() <= SEUIL_FORCE_BRUTE) {
            System.out.println("Effectif réduit (" + promo.size() + ") -> Utilisation FORCE BRUTE");
            return algoForceBrute(promo, nbGroupeVoulus);
        } else {
            System.out.println("Effectif important (" + promo.size() + ") -> Utilisation GLOUTON");
            return gloutonBac(promo, nbGroupeVoulus); // ou return gloutonMoyenne(promo, nbGroupeVoulus);
        }
    }

    /*
     * Algorithme Glouton 1 : Équilibrer les Moyennes
     * Stratégie Choisi :
     * 1. On place d'abord les filles pour respecter la contrainte de mixité.
     * 2. On trie le reste des étudiants par Moyenne Générale.
     * 3. On les distribue en Serpentin (aller-retour) pour équilibrer les niveaux.
     */

    public List<Groupe> gloutonMoyenne(List<Etudiant> promo, int nbGroupeVoulus) {
        System.out.println("--- Lancement Algo Glouton Moyenne ---");

        // Préparation des groupes
        List<Groupe> groupes = initialiserGroupes(nbGroupeVoulus);

        // Séparation Filles / Garçons
        List<Etudiant> filles = new ArrayList<>();
        List<Etudiant> autres = new ArrayList<>();

        for (Etudiant e : promo) {
            if (e.getGenre() == 'F') {
                filles.add(e);
            } else {
                autres.add(e);
            }
        }

        // Vérification de faisabilité
        if (filles.size() < MIN_FILLES * nbGroupeVoulus) {
            System.out.println("Attention : Pas assez de filles pour garantir " + MIN_FILLES + " par groupe !");
        }

        // Distribution des Filles (Priorité absolue)
        distribuerFillesEquitablement(filles, groupes);

        // Tri des autres par Moyenne (du meilleur au moins bon)
        // Utilisation d'un Comparator pour trier sur le double 'MoyenneGenerale'
        autres.sort(Comparator.comparingDouble(Etudiant::getMoyenneGenerale).reversed());

        // Distribution des autres (Serpentin pour équilibrer les niveaux)
        distribuerEnSerpentin(autres, groupes);

        // Vérification et Résultat
        if (estValide(groupes, promo.size())) {
            System.out.println("Succès Glouton Moyenne ! Score : " + calculerScoreTotal(groupes));
            return groupes;
        } else {
            System.out.println("Echec Glouton Moyenne : Contraintes non respectées.");
            return null; // Retourne null pour indiquer un échec
        }
    }

    /*
     * Algorithme Glouton 2 : Équilibrer les types de BAC (Puissance BAC)
     * Stratégie Choisi :
     * 1. On place d'abord les filles (comme pour l'autre algo).
     * 2. On trie le reste par "Puissance BAC" (Coefficient).
     * 3. On distribue pour lisser les profils techniques/généraux.
     */
    public List<Groupe> gloutonBac(List<Etudiant> promo, int nbGroupeVoulus) {
        System.out.println("--- Lancement Algo Glouton BAC ---");

        List<Groupe> groupes = initialiserGroupes(nbGroupeVoulus);

        List<Etudiant> filles = new ArrayList<>();
        List<Etudiant> autres = new ArrayList<>();

        for (Etudiant e : promo) {
            if (e.getGenre() == 'F')
                filles.add(e);
            else
                autres.add(e);
        }

        // Distribution des filles
        distribuerFillesEquitablement(filles, groupes);

        // Tri par Puissance BAC (du plus fort au plus faible)
        autres.sort(Comparator.comparingDouble(Etudiant::getPuissanceBac).reversed());

        // Distribution équilibrée
        distribuerEnSerpentin(autres, groupes);

        if (estValide(groupes, promo.size())) {
            System.out.println("Succès Glouton BAC ! Score : " + calculerScoreTotal(groupes));
            return groupes;
        } else {
            System.out.println("Echec Glouton BAC.");
            return null; // Retourne null pour indiquer un échec
        }
    }

    /*
     * Algorithme Force Brute (Backtracking)
     * Teste toutes les combinaisons possibles pour trouver LA meilleure.
     * ATTENTION : Très long si beaucoup d'étudiants (Complexité exponentielle).
     * À utiliser uniquement pour des petits groupes (< 15 étudiants) ou test.
     */

    // Variables globales pour stocker le meilleur résultat trouvé pendant la
    // récursion
    private List<Groupe> meilleureSolution = null;
    private double meilleurScore = Double.MAX_VALUE;

    public List<Groupe> algoForceBrute(List<Etudiant> promo, int nbGroupeVoulus) {
        System.out.println("--- Lancement Force Brute (Mode Test) ---");

        // Réinitialisation des variables
        meilleureSolution = null;
        meilleurScore = Double.MAX_VALUE;

        List<Groupe> groupesVides = initialiserGroupes(nbGroupeVoulus);

        // Appel de la fonction récursive qui va explorer les solutions
        explorateurDeSolutions(promo, 0, groupesVides);

        if (meilleureSolution != null) {
            System.out.println("Force Brute terminée. Meilleur score trouvé : " + meilleurScore);
            return meilleureSolution;
        } else {
            System.out.println("Aucune solution valide trouvée avec la Force Brute.");
            return null;
        }
    }

    /*
     * Fonction Récursive (Backtracking)
     * Essaie de mettre l'étudiant 'indexEtu' dans chaque groupe possible.
     */
    private void explorateurDeSolutions(List<Etudiant> promo, int indexEtu, List<Groupe> groupesActuels) {
        // Cas de base : Tous les étudiants sont placés
        if (indexEtu == promo.size()) {
            // On vérifie si cette solution complète est valide (Filles, Taille...)
            if (estValide(groupesActuels, promo.size())) {
                double scoreActuel = calculerScoreTotal(groupesActuels);

                // Si c'est mieux que ce qu'on a déjà trouvé, on sauvegarde !
                if (scoreActuel < meilleurScore) {
                    meilleurScore = scoreActuel;
                    // On fait une copie complète (Deep Copy) pour ne pas perdre l'état
                    meilleureSolution = copierGroupes(groupesActuels);
                }
            }
            return;
        }

        // L'étudiant à placer maintenant
        Etudiant etudiantAplacer = promo.get(indexEtu);

        // On essaie de l'ajouter dans chaque groupe 1 par 1
        for (Groupe g : groupesActuels) {

            // Petite opti : Si le groupe dépasse déjà la taille max théorique + 1, on évite
            // (élagage)
            // Mais pour une Force Brute pure pédagogique, on peut laisser tester.

            g.ajouterEtudiant(etudiantAplacer);

            // On continue avec l'étudiant suivant (Appel Récursif)
            explorateurDeSolutions(promo, indexEtu + 1, groupesActuels);

            // Backtracking : On enlève l'étudiant pour pouvoir le tester dans le groupe
            // suivant au prochain tour de boucle
            g.retirerEtudiant(etudiantAplacer);
        }
    }

    // Les fonctions qui aident à rendre le code principal plus lisible :

    /* Crée une liste de N groupes vides */
    private List<Groupe> initialiserGroupes(int n) {
        List<Groupe> l = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            l.add(new Groupe(i + 1));
        }
        return l;
    }

    /* Distribue les filles une par une dans chaque groupe */
    private void distribuerFillesEquitablement(List<Etudiant> filles, List<Groupe> groupes) {
        int indexGroupe = 0;
        for (Etudiant f : filles) {
            groupes.get(indexGroupe).ajouterEtudiant(f);

            // On passe au groupe suivant (modulo pour revenir au premier)
            indexGroupe = (indexGroupe + 1) % groupes.size();
        }
    }

    /*
     * Distribue les étudiants en "Serpentin" (1,2,3 puis 3,2,1) pour bien mélanger
     * les niveaux
     */
    private void distribuerEnSerpentin(List<Etudiant> etudiants, List<Groupe> groupes) {
        int nbGroupes = groupes.size();

        for (int i = 0; i < etudiants.size(); i++) {
            int indexGroupe;
            int tour = i / nbGroupes; // Numéro du tour de distribution

            if (tour % 2 == 0) {
                // Tour Pair : De gauche à droite (0 -> N)
                indexGroupe = i % nbGroupes;
            } else {
                // Tour Impair : De droite à gauche (N -> 0)
                indexGroupe = (nbGroupes - 1) - (i % nbGroupes);
            }

            groupes.get(indexGroupe).ajouterEtudiant(etudiants.get(i));
        }
    }

    /* Vérifie toutes les contraintes du sujet */
    private boolean estValide(List<Groupe> groupes, int totalEtudiants) {
        int nbGroupes = groupes.size();

        // Calcul des bornes de taille (Min et Max)
        int tailleMin = totalEtudiants / nbGroupes;
        int tailleMax = (totalEtudiants % nbGroupes == 0) ? tailleMin : tailleMin + 1;

        for (Groupe g : groupes) {
            // Règle 1 : La taille doit être correcte
            if (g.taille() < tailleMin || g.taille() > tailleMax) {
                // System.out.println("Groupe " + g.getIdGroupe() + " invalide (Taille)");
                return false;
            }

            // Règle 2 : Minimum 4 filles (Contrainte stricte)
            if (g.compterFilles() < MIN_FILLES) {
                // System.out.println("Groupe " + g.getIdGroupe() + " invalide (Manque
                // filles)");
                return false;
            }
        }
        return true;
    }

    /* Calcule le score final (On veut le MINIMISER) */
    private double calculerScoreTotal(List<Groupe> groupes) {
        if (groupes == null || groupes.isEmpty())
            return Double.MAX_VALUE;

        double minMoy = 20.0, maxMoy = 0.0;
        double minBac = Double.MAX_VALUE, maxBac = 0.0;

        for (Groupe g : groupes) {
            double moy = g.getMoyenneGroupe();
            double bac = g.getScoreBacTotal();

            if (moy < minMoy)
                minMoy = moy;
            if (moy > maxMoy)
                maxMoy = moy;

            if (bac < minBac)
                minBac = bac;
            if (bac > maxBac)
                maxBac = bac;
        }

        // Score = (Ecart Moyenne) + (Ecart BAC)
        return (maxMoy - minMoy) + (maxBac - minBac);
    }

    /* Copie profonde pour sauvegarder une solution */
    private List<Groupe> copierGroupes(List<Groupe> source) {
        List<Groupe> copie = new ArrayList<>();
        for (Groupe g : source) {
            Groupe nouveauG = new Groupe(g.getIdGroupe());
            // On rajoute les mêmes étudiants
            for (Etudiant e : g.getEtudiants()) {
                nouveauG.ajouterEtudiant(e);
            }
            copie.add(nouveauG);
        }
        return copie;
    }
}
