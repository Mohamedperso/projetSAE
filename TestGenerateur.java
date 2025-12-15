import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestGenerateur {

    private static final Random rand = new Random();

    public static void main(String[] args) {
        GenerateurGroupe generateur = new GenerateurGroupe();

        System.out.println("===============================");
        System.out.println("  TEST DU GENERATEUR DE GROUPE ");
        System.out.println("===============================\n");

        // 1. Test Unitaire Rapide (Petite promo)
        System.out.println("--- 1. TEST RAPIDE (18 Etudiants, 3 Groupes) ---");
        List<Etudiant> promo = genererPromoAleatoire(18);

        System.out.println("\n> Test Algo 1 (Glouton Moyenne) :");
        generateur.gloutonMoyenne(promo, 3);

        System.out.println("\n> Test Algo 2 (Glouton BAC) :");
        generateur.gloutonBac(promo, 3);

        // 2. Test Force Brute (Très petite taille pour ne pas planter)
        System.out.println("\n--- 2. TEST FORCE BRUTE (10 Etudiants, 2 Groupes) ---");
        List<Etudiant> petitePromo = genererPromoAleatoire(10);
        generateur.algoForceBrute(petitePromo, 2);

        // 3. Comparaison Statistique
        System.out.println("\n--- 3. COMPARAISON PERFORMANCE (100 Simulations) ---");
        System.out.println("Paramètres : 36 étudiants, 6 groupes (Objectif : 6 par groupe, min 4 filles)");
        comparerAlgorithmes(100, 36, 6);

        // 4. Test du "Selecteur Intelligent" (Cas Réaliste App)
        System.out.println("\n--- 4. TEST SMART SELECTOR (Simulation App) ---");

        System.out.println("Cas A : Petit groupe (Option Anglais - 11 élèves - 3 groupes)");
        List<Etudiant> petitGroupe = genererPromoAleatoire(11);
        // Ici ça devrait déclencher le FORCE BRUTE
        generateur.genererGroupesAutomatique(petitGroupe, 3);

        System.out.println("\nCas B : Promo Normale (50 élèves - 5 groupes)");
        List<Etudiant> grandGroupe = genererPromoAleatoire(50);
        // Ici ça devrait déclencher le GLOUTON
        generateur.genererGroupesAutomatique(grandGroupe, 5);
    }

    /**
     * Compare les performances des deux algo gloutons sur plusieurs essais.
     */
    public static void comparerAlgorithmes(int nbTests, int taillePromo, int nbGroupes) {
        GenerateurGroupe gen = new GenerateurGroupe();

        int succesMoyenne = 0;
        int succesBac = 0;

        double totalScoreMoyenne = 0;
        double totalScoreBac = 0;

        for (int i = 0; i < nbTests; i++) {
            List<Etudiant> p = genererPromoAleatoire(taillePromo);

            // Algo 1
            List<Groupe> g1 = gen.gloutonMoyenne(p, nbGroupes);
            if (g1 != null) { // Si valide
                succesMoyenne++;
                totalScoreMoyenne += calculerScore(g1);
            }

            // Algo 2
            List<Groupe> g2 = gen.gloutonBac(p, nbGroupes);
            if (g2 != null) { // Si valide
                succesBac++;
                totalScoreBac += calculerScore(g2);
            }
        }

        System.out.println("\nRESULTATS :");

        System.out.println("Gloûton MOYENNE :");
        System.out.println(" - Succès : " + succesMoyenne + "/" + nbTests + " (" + succesMoyenne + "%)");
        if (succesMoyenne > 0)
            System.out.println(" - Score Moyen : " + String.format("%.2f", totalScoreMoyenne / succesMoyenne));

        System.out.println("Gloûton BAC :");
        System.out.println(" - Succès : " + succesBac + "/" + nbTests + " (" + succesBac + "%)");
        if (succesBac > 0)
            System.out.println(" - Score Moyen : " + String.format("%.2f", totalScoreBac / succesBac));

        if (succesBac > 0 && succesMoyenne > 0) {
            double diff = (totalScoreMoyenne / succesMoyenne) - (totalScoreBac / succesBac);
            if (diff > 0)
                System.out.println(
                        "\n=> Glouton BAC est meilleur en moyenne de " + String.format("%.2f", diff) + " points.");
            else
                System.out.println(
                        "\n=> Glouton Moyenne est meilleur en moyenne de " + String.format("%.2f", -diff) + " points.");
        }
    }

    /**
     * Recalcule le score pour les stats (Ecart Moyenne + Ecart Bac)
     */
    private static double calculerScore(List<Groupe> groupes) {
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
        return (maxMoy - minMoy) + (maxBac - minBac);
    }

    public static List<Etudiant> genererPromoAleatoire(int n) {
        List<Etudiant> liste = new ArrayList<>();
        String[] bacs = { "GM", "GI", "STI", "A" };

        // On génère suffisamment de filles pour que le test soit intéressant (env 45%)
        // Sinon l'algo échoue souvent juste à cause du manque de filles.
        for (int i = 0; i < n; i++) {
            char genre = (rand.nextDouble() < 0.45) ? 'F' : 'M';
            String bac = bacs[rand.nextInt(bacs.length)];
            double moy = 8.0 + (rand.nextDouble() * 12.0); // Note entre 8 et 20

            // id, nom, genre, bac, moyenne
            liste.add(new Etudiant(i, "Etu" + i, genre, bac, moy));
        }
        return liste;
    }
}
