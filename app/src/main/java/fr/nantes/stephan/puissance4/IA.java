package fr.nantes.stephan.puissance4;

/**
 * Created by Ugho on 17/03/14.
 */
public class IA {

    private final static int[][] valposi={{3,4,5,7,5,4,3},{4,6,8,10,8,6,4},{5,8,11,13,11,8,5},{5,8,11,13,11,8,5},{4,6,8,10,8,6,4},{3,4,5,7,5,4,3}};
    private final static int MAX = 100000;
    private final static int PROFONDEUR = 5; // MAX 6
    public final static String COMPUTER = "C";
    public final static String PLAYER = "P";

    private String[][] mJeu = new String[7][6];

    public IA() {
    }

    public int calculCoupAJouer(final String[][] jeu) {

        this.mJeu = jeu;

        Node root = new Node();
        root.setRoot(true);

        AnalysePositionFuture(root, PROFONDEUR, mJeu, COMPUTER);

        return getMeilleurColonne(root);
    }

    public void AnalysePositionFuture(Node n, final int depth, final String[][] game, final String who) {

        if (gagne(game, COMPUTER)) {
            n.setEstimation(MAX + MAX);
            n.setDepth(depth);
            return;
        }

        if (gagne(game, PLAYER)) {
            n.setEstimation(-MAX - MAX);
            n.setDepth(depth);
            return;
        }

        if (depth == 0) {
            n.setEstimation(estimerJeu(game));
            return;
        }

        for (int col=0; col<=6; col++) {
            final String saveJeu[][] = new String[7][6];
            int nbPionParCo[] = nombrePionsParColonnes(game);

            for (int i=0; i<=6; i++) {
                System.arraycopy(game[i], 0, saveJeu[i], 0, 6);
            }

            if (nbPionParCo[col] < 6) {
                int position = ((5 - nbPionParCo[col]) * 7) + col;
                int ligne = (int) Math.floor(position / 7);

                saveJeu[col][ligne] = who;
                Node no = new Node(col, depth, n);
                n.getMyNodes().add(no);

                AnalysePositionFuture(no, depth-1, saveJeu, switchQui(who));
            }
        }
    }

    private int estimerJeu(final String[][] jeu) {
        final int estimationOrdi = valeurPion(jeu, COMPUTER) + gagneDansUnCoup(jeu, COMPUTER) + gagneDansDeuxCoup(jeu, COMPUTER);
        final int estimationJoueur = valeurPion(jeu, PLAYER) + gagneDansUnCoup(jeu, PLAYER) + gagneDansDeuxCoup(jeu, PLAYER);

        return estimationOrdi - estimationJoueur;
    }

    public int getMeilleurColonne(Node root) {

        int colonneAjouer = 0;

        for (int i=0;i<root.getMyNodes().size();i++) {
            imprim(root.getMyNodes().get(i));
        }

        int max = -9999999;

        colonneAjouer = root.getMyNodes().get(0).getCol();

        for (int i=0; i<root.getMyNodes().size(); i++) {

            final int currentEstimation = root.getMyNodes().get(i).getEstimation();
            final int currentCol = root.getMyNodes().get(i).getCol();

            if (currentEstimation > max
                    || (currentEstimation == max & (currentCol == 2 || currentCol == 3 || currentCol ==4 ))) {

                max = currentEstimation;
                colonneAjouer = currentCol;
            }
        }

        return colonneAjouer;
    }

    private void imprim(Node node){
        for (int i=0; i<node.getMyNodes().size(); i++) {
            if(node.getMyNodes().get(i).getEstimation()==0 & node.getMyNodes().get(i).getMyNodes().size()>0) {
                imprim(node.getMyNodes().get(i));
            }
        }
        if (node.getDepth() %2 == 0) {
            int min=9999999;
            for (int k=0;k<node.getMyNodes().size();k++) {
                if (node.getMyNodes().get(k).getEstimation()<min) {
                    min=node.getMyNodes().get(k).getEstimation();
                    node.setEstimation(min);
                }
            }
        } else {
            int max=-9999999;
            for (int k=0;k<node.getMyNodes().size();k++) {
                if (node.getMyNodes().get(k).getEstimation()>max) {
                    max=node.getMyNodes().get(k).getEstimation();
                    node.setEstimation(max);
                }
            }
        }
    }

    private String switchQui(final String qui) {
        return (qui.equals(COMPUTER)) ?  PLAYER : COMPUTER;
    }

    private int[] nombrePionsParColonnes(final String[][] jeu) {
        final int tab[] = new int[7];

        for (int cpt = 0; cpt<=6; cpt++) {
            tab[cpt] = 0;
        }

        for (int i=0; i<=6; i++) {
            for (int j=0; j<=5; j++) {
                if (jeu[i][j] != null) {
                    tab[i] += 1;
                }
            }
        }

        return tab;
    }

    public boolean gagne(final String[][] mPionsJouee, final String qui) {

        //vérif  par colonne
        for(int i = 0; i<=6; i++) {
            for(int j = 0; j<=2; j++) {
                if(qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i][j + 1]) && qui.equals(mPionsJouee[i][j + 2]) && qui.equals(mPionsJouee[i][j + 3])) {
                    return true;
                }
            }
        }

        //vérif  par ligne
        for(int i = 0; i<=3; i++) {
            for(int j = 0; j<=5; j++) {
                if(qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j]) && qui.equals(mPionsJouee[i + 2][j]) && qui.equals(mPionsJouee[i + 3][j])) {
                    return true;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for(int i = 0; i<=3; i++) {
            for(int j = 3; j<=5; j++) {
                if(qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j - 1]) && qui.equals(mPionsJouee[i + 2][j - 2]) && qui.equals(mPionsJouee[i + 3][j - 3])) {
                    return true;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for(int i = 3; i<=6; i++) {
            for(int j = 3; j<=5; j++) {
                if(qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i - 1][j - 1]) && qui.equals(mPionsJouee[i - 2][j - 2]) && qui.equals(mPionsJouee[i - 3][j - 3])) {
                    return true;
                }
            }
        }

        return false;
    }

    public int valeurPion(final String[][] mPionsJouee, final String qui) {
        int valeur = 0;
        for(int i=0; i<=6; i++) {
            for(int j=0; j<=5; j++) {
                if(qui.equals(mPionsJouee[i][j])) {
                    valeur += valposi[j][i];
                }
            }
        }
        return valeur;
    }

    public int gagneDansDeuxCoup(final String[][] mPionsJouee, final String qui) {
        int retour = 0;

        //vérif  par colonne
        for(int i = 0; i<=6; i++) {
            for(int j = 0; j<=2; j++) {
                if((qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i][j + 1]) && mPionsJouee[i][j + 2] == null && mPionsJouee[i][j + 3] == null)
                        || (mPionsJouee[i][j] == null && mPionsJouee[i][j + 1] == null && qui.equals(mPionsJouee[i][j + 2]) && qui.equals(mPionsJouee[i][j + 3]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i][j + 1] == null && mPionsJouee[i][j + 2] == null && qui.equals(mPionsJouee[i][j + 3])))
                {
                    retour += 300;
                }
            }
        }

        //vérif  par ligne
        for(int i = 0; i<=3; i++) {
            for(int j = 0; j<=5; j++) {
                if((qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j]) && mPionsJouee[i + 2][j] == null && mPionsJouee[i + 3][j] == null)
                        || (mPionsJouee[i][j] == null && mPionsJouee[i + 1][j] == null && qui.equals(mPionsJouee[i + 2][j]) && qui.equals(mPionsJouee[i + 3][j]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i + 1][j] == null && mPionsJouee[i + 2][j] == null && qui.equals(mPionsJouee[i + 3][j])))
                {
                    retour += 300;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for(int i = 0; i<=3; i++) {
            for(int j = 3; j<=5; j++) {
                if((qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j - 1]) && mPionsJouee[i + 2][j - 2] == null && mPionsJouee[i + 3][j - 3] == null)
                        || (mPionsJouee[i][j] == null && mPionsJouee[i + 1][j - 1] == null && qui.equals(mPionsJouee[i + 2][j - 2]) && qui.equals(mPionsJouee[i + 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i + 1][j - 1] == null && mPionsJouee[i + 2][j - 2] == null && qui.equals(mPionsJouee[i + 3][j - 3])))
                {
                    retour += 300;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for(int i = 3; i<=6; i++) {
            for(int j = 3; j<=5; j++) {
                if((mPionsJouee[i][j] == null && mPionsJouee[i - 1][j - 1] == null && qui.equals(mPionsJouee[i - 2][j - 2]) && qui.equals(mPionsJouee[i - 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i - 1][j - 1]) && mPionsJouee[i - 2][j - 2] == null && mPionsJouee[i - 3][j - 3] == null)
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i - 1][j - 1] == null && mPionsJouee[i - 2][j - 2] == null && qui.equals(mPionsJouee[i - 3][j - 3])))
                {
                    retour += 300;
                }
            }
        }

        return retour;
    }

    public int gagneDansUnCoup(final String[][] mPionsJouee, final String qui) {
        int retour = 0;

        //vérif  par colonne
        for(int i = 0; i<=6; i++) {
            for(int j = 0; j<=2; j++) {
                if((mPionsJouee[i][j] == null && qui.equals(mPionsJouee[i][j + 1]) && qui.equals(mPionsJouee[i][j + 2]) && qui.equals(mPionsJouee[i][j + 3]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i][j + 1] == null && qui.equals(mPionsJouee[i][j + 2]) && qui.equals(mPionsJouee[i][j + 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i][j + 1]) && mPionsJouee[i][j + 2] == null && qui.equals(mPionsJouee[i][j + 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i][j + 1]) && qui.equals(mPionsJouee[i][j + 2]) && mPionsJouee[i][j + 3] == null))
                {
                    retour += 5000;
                }
            }
        }

        //vérif  par ligne
        for(int i = 0; i<=3; i++) {
            for(int j = 0; j<=5; j++) {
                if((mPionsJouee[i][j] == null && qui.equals(mPionsJouee[i + 1][j]) && qui.equals(mPionsJouee[i + 2][j]) && qui.equals(mPionsJouee[i + 3][j]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i + 1][j] == null && qui.equals(mPionsJouee[i + 2][j]) && qui.equals(mPionsJouee[i + 3][j]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j]) && mPionsJouee[i + 2][j] == null && qui.equals(mPionsJouee[i + 3][j]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j]) && qui.equals(mPionsJouee[i + 2][j]) && mPionsJouee[i + 3][j] == null))
                {
                    retour += 5000;
                }
            }
        }

        //vérif  par diagonale vers la droite
        for(int i = 0; i<=3; i++) {
            for(int j = 3; j<=5; j++) {
                if((mPionsJouee[i][j] == null && qui.equals(mPionsJouee[i + 1][j - 1]) && qui.equals(mPionsJouee[i + 2][j - 2]) && qui.equals(mPionsJouee[i + 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i + 1][j - 1] == null && qui.equals(mPionsJouee[i + 2][j - 2]) && qui.equals(mPionsJouee[i + 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j - 1]) && mPionsJouee[i + 2][j - 2] == null && qui.equals(mPionsJouee[i + 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i + 1][j - 1]) && qui.equals(mPionsJouee[i + 2][j - 2]) && mPionsJouee[i + 3][j - 3] == null))
                {
                    retour += 5000;
                }
            }
        }

        //vérif  par diagonale vers la gauche
        for(int i = 3; i<=6; i++) {
            for(int j = 3; j<=5; j++) {
                if((mPionsJouee[i][j] == null && qui.equals(mPionsJouee[i - 1][j - 1]) && qui.equals(mPionsJouee[i - 2][j - 2]) && qui.equals(mPionsJouee[i - 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && mPionsJouee[i - 1][j - 1] == null && qui.equals(mPionsJouee[i - 2][j - 2]) && qui.equals(mPionsJouee[i - 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i - 1][j - 1]) && mPionsJouee[i - 2][j - 2] == null && qui.equals(mPionsJouee[i - 3][j - 3]))
                        || (qui.equals(mPionsJouee[i][j]) && qui.equals(mPionsJouee[i - 1][j - 1]) && qui.equals(mPionsJouee[i - 2][j - 2]) && mPionsJouee[i - 3][j - 3] == null))
                {
                    retour += 5000;
                }
            }
        }

        return retour;
    }

}
