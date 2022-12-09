import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Fait avec l'aider complémentaire de raphael L
 * 
 * 
 * <p>
 * Implantation de l'interface Collection basée sur les arbres binaires de
 * recherche. Les éléments sont ordonnés soit en utilisant l'ordre naturel (cf
 * Comparable) soit avec un Comparator fourni à la création.
 * </p>
 * 
 * <p>
 * Certaines méthodes de AbstractCollection doivent être surchargées pour plus
 * d'efficacité.
 * </p>
 * 
 * @param <E>
 *            le type des clés stockées dans l'arbre
 */
public class ABR<E extends Comparable<E>> extends AbstractCollection<E> {
	private Noeud racine;
	private int taille;
	private Comparator<? super E> cmp;

	private class Noeud{
		E cle;
		Noeud pere;
		Noeud gauche;
		Noeud droit;
		

		Noeud(E cle) 
		{
			this.cle = cle;
		}

		/**
		 * Renvoie le noeud contenant la clé minimale du sous-arbre enraciné
		 * dans ce noeud
		 * 
		 * @return le noeud contenant la clé minimale du sous-arbre enraciné
		 *         dans ce noeud
		 */
		Noeud minimum() 
		{
			Noeud x = this;
			while (x.gauche != null) {
				x = x.gauche;
			}
			
			return x;
		}

		/**
		 * Renvoie le successeur de ce noeud
		 * 
		 * @return le noeud contenant la clé qui suit la clé de ce noeud dans
		 *         l'ordre des clés, null si c'es le noeud contenant la plus
		 *         grande clé
		 */
		Noeud suivant() 
		{
			Noeud x = this;
			if (x.droit != null) {
				return x.droit.minimum();
			}
			
			Noeud y = x.pere;
			while (y != null && x == y.droit) {
			    x = y;
			    y = y.pere;
			}
			
			return y;
		}
	}

	// Constructeurs

	/**
	 * Crée un arbre vide. Les éléments sont ordonnés selon l'ordre naturel
	 */
	@SuppressWarnings("unchecked")
	public ABR() 
	{
		this.taille = 0;
		this.cmp = (Comparator<E>) Comparator.naturalOrder();
		//this.cmp = ( e1, e2)->((Comparable) e1).compareTo( e2 );
		this.racine = null;
	}

	/**
	 * Crée un arbre vide. Les éléments sont comparés selon l'ordre imposé par
	 * le comparateur
	 * 
	 * @param cmp
	 *            le comparateur utilisé pour définir l'ordre des éléments
	 */
	public ABR(Comparator<? super E> cmp) 
	{
		this();
		this.cmp = cmp;
	}

	/**
	 * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
	 * que c. L'ordre des éléments est l'ordre naturel.
	 * 
	 * @param c
	 *            la collection à copier
	 */
	@SuppressWarnings("unchecked")
	public ABR(Collection<? extends E> c) 
	{
		this();
		addAll(c);
		
	}

	@Override
	public Iterator<E> iterator() 
	{
		return new ABRIterator();
	}

	@Override
	public int size() 
	{
		return this.taille;
	}

	// Quelques méthodes utiles

	/**
	 * Recherche une clé. Cette méthode peut être utilisée par
	 * {@link #contains(Object)} et {@link #remove(Object)}
	 * 
	 * @param o
	 *            la clé à chercher
	 * @return le noeud qui contient la clé ou null si la clé n'est pas trouvée.
	 */
	private Noeud rechercher(Object o) 
	{
		Noeud noeudDelete = new Noeud((E) o);
		Noeud x = this.racine;
            while (x != null && x.cle != o) {
                if( cmp.compare(noeudDelete.cle,x.cle) <=-1 )
                    x = x.gauche;
                else
                    x = x.droit;
            }
            return x;
	}

	/**
	 * Supprime le noeud z. Cette méthode peut être utilisée dans
	 * {@link #remove(Object)} et {@link Iterator#remove()}
	 * 
	 * @param z
	 *            le noeud à supprimer
	 * @return le noeud contenant la clé qui suit celle de z dans l'ordre des
	 *         clés. Cette valeur de retour peut être utile dans
	 *         {@link Iterator#remove()}
	 */
	private Noeud supprimer(Noeud z) 
	{
		  Noeud y ,x;
		  
		  Noeud svt = z.suivant();
		  if (z.gauche == null || z.droit == null)
			    y = z;
		  else
		    y = svt;
		  // y est le nœud à détacher
		
		  if (y.gauche != null)
		    x = y.gauche;
		  else
		    x = y.droit;
		  // x est le fils unique de y ou null si y n'a pas de fils
		
		  if (x != null) x.pere = y.pere;
		
		  if (y.pere == null) { // suppression de la racine
		    racine = x;
		  } else {
		    if (y == y.pere.gauche)
		      y.pere.gauche = x;
		    else
		      y.pere.droit = x;
		  }
		
		  if (y != z) {
			  z.cle = y.cle;
			  svt = z;
		  }

		taille--;
		return svt;
	}

	/**
	 * Les itérateurs doivent parcourir les éléments dans l'ordre ! Ceci peut se
	 * faire facilement en utilisant {@link Noeud#minimum()} et
	 * {@link Noeud#suivant()}
	 */
	private class ABRIterator implements Iterator<E> {
		private Noeud suiv, prec;
		
		public ABRIterator()
		{
			prec = null;

			if ( racine == null ) 
				suiv = null;
			else 
				suiv = racine.minimum();

		}
		
		public boolean hasNext() 
		{
			return suiv != null;
		}
		
		public E next() 
		{
			if ( suiv == null ) throw new NoSuchElementException();
			prec = suiv;
			suiv = suiv.suivant();
			return prec.cle;
		}

		public void remove() 
		{
			if ( prec == null)  throw new IllegalStateException();
			suiv = supprimer(prec);	
			prec = null;
		}
	}

	// Pour un "joli" affichage

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		toString(racine, buf, "", maxStrLen(racine));
		return buf.toString();
	}

	private void toString(Noeud x, StringBuffer buf, String path, int len) {
		if (x == null)
			return;
		toString(x.droit, buf, path + "D", len);
		for (int i = 0; i < path.length(); i++) {
			for (int j = 0; j < len + 6; j++)
				buf.append(' ');
			char c = ' ';
			if (i == path.length() - 1)
				c = '+';
			else if (path.charAt(i) != path.charAt(i + 1))
				c = '|';
			buf.append(c);
		}
		buf.append("-- " + x.cle.toString());
		if (x.gauche != null || x.droit != null) {
			buf.append(" --");
			for (int j = x.cle.toString().length(); j < len; j++)
				buf.append('-');
			buf.append('|');
		}
		buf.append("\n");
		toString(x.gauche, buf, path + "G", len);
	}

	private int maxStrLen(Noeud x) {
		return x == null ? 0 : Math.max(x.cle.toString().length(),
				Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
	}

	// TODO : voir quelles autres méthodes il faut surcharger
	public Noeud getRacine() { return this.racine; }
	public Comparator<? super E> getCmp() { return this.cmp; }
	
	
	public void ajouter(Noeud z) 
	{
		if( racine == null ) 
		{
			racine = z;
		}
		else 
		{
			Noeud y = null;
			Noeud x = racine;

			while (x != null) 
			{
				y = x;
				int compare = cmp.compare(z.cle, x.cle);

				if ( compare < 0 ) 
					x = x.gauche;
				else 
					x = x.droit;
			}

			z.pere = y;

			if (y == null) // arbre vide
			{ 
				racine = z;
			} else 
			{
				if (cmp.compare(z.cle, y.cle) < 0)
					y.gauche = z;
				else
					y.droit = z;
			}
			z.gauche = z.droit = null;
		}
		taille++;
	}
	
	public boolean add(E e) 
	{
		ajouter( new Noeud(e));
		return true;
	}

	public boolean addAll( Collection<? extends E> c ) 
	{
		for(E element : c) {
			ajouter(new Noeud(element));
		}
		
		return true;
	}

	public void clear() 
	{
		racine = null;
	}

	public boolean contains(Object o) 
	{
		if( rechercher(o) != null ) 
			return true;
		else
			return false;
	}
	
	public boolean containsAll(Collection<?> c) {
		
		for( Object element: c) {
			if( rechercher(element) == null ) 
				return false;
		}
		return true;
	}
	
	public boolean isEmpty() {
		if ( racine == null ) 
			return true;
		else
			return false;
	}
	
	public boolean remove(Object o) {
		Noeud n = rechercher(o);

		if( n == null ) 
			return false;
		
		supprimer(n);
		
		return true;
	}
	

	public boolean removeAll(Collection<?> c) {

		for(Object o : c) 
		{
			Noeud n = rechercher(o);
			if( n == null ) return false;
			
			supprimer(n);
		}
		
		return true;
	}
	
	public Object[] toArray() {
		Object[] objets = new Object[size()];
		Iterator i = iterator();
		int cpt = 0;
		while( i.hasNext() ) {
			objets[cpt++] = i.next();
		}
		
		return objets;
	}
	
	
	public static void main(String[] args)
	{
		Scanner myObj = new Scanner(System.in);
		String choixDeTest;  
		final int longeurStringPourLeTE = 3;  //Je trouvais ça moche de mettre la var en Upper
		do{
			System.out.println("Taper 1 pour tester les différents cas");
			System.out.println("Taper 2 pour tester le temps d'execution du programme");
			System.out.println("Taper 3 pour tester le temps d'execution du programme avec des ints");
			System.out.println("Taper 4 pour tester si les clefs sont ajoutées de la plus petite à la plus grande");
			System.out.print("Quel est votre choix : ");
			choixDeTest = myObj.nextLine();
			System.out.println(); // Pour faire joli


		}while(!choixDeTest.equals("1") && !choixDeTest.equals("2")&& !choixDeTest.equals("3")&& !choixDeTest.equals("4"));

		if(choixDeTest.equals("1"))  //Sert à tester les différents cas pouvant être rencontrés
		{
			ArrayList<String> list = new ArrayList<>();

			list.add("g");
			list.add("u");
			list.add("x");
			list.add("e");
			list.add("h");
			list.add("t");
			list.add("k");
			list.add("z");
			list.add("m");
			list.add("a");
			list.add("j");
			list.add("t");
			
			Collection<String> abr = new ABR<>(list);
			String border = "====================================================";

			System.out.println(border);
			System.out.println("     Test 00: Affichage normal de l'arbre       \n");	
			System.out.println(abr);

			System.out.println(border);
			System.out.println("       Test 01: Supprimer une feuille (z)      \n");	
			abr.remove("z");
			System.out.println(abr);

			System.out.println(border);
			System.out.println("  Test 02: Supprimer un noeud avec une feuille (e)  \n");	
			abr.remove("e");
			System.out.println(abr);

			System.out.println(border);
			System.out.println("  Test 03: Supprimer un noeud avec deux feuille (k) \n");	
			abr.remove("k");
			System.out.println(abr);

			System.out.println(border);
			System.out.println("       Test 04: Supprimer la racine  (g)        \n");	
			abr.remove("g");
			System.out.println(abr);
			System.out.println(border);

		}
		if(choixDeTest.equals("2"))  //Sert à calculer la vitesse d'execution du programme
		{
			System.out.println("Nombre d'ajout | Temps d'execution");
			long start = System.currentTimeMillis(); //Permettre le calcul du temps d'exec
			int totAjout = 10000000 ; 
			ABR<String> arbre = new ABR<>();

			for(int nbAjout=0;nbAjout<totAjout;nbAjout++)
			{
				arbre.add(RandomString(longeurStringPourLeTE));
				if(nbAjout%100000 ==0 )
				{
					long tempsActuel = System.currentTimeMillis() - start;
					System.out.println(nbAjout +" , "+ tempsActuel); //Utile pour faire un doc permettant de tracer la courbe d'ou la virgule
				}
			}			
		}

		if(choixDeTest.equals("3"))  //Sert à calculer la vitesse d'execution du programme avec des int
		{

			System.out.println("Nombre d'ajout | Temps d'execution");
			long start = System.currentTimeMillis(); //Permettre le calcul du temps d'exec
			int totAjout = 10000000 ; 
			ANR<Integer> arbre = new ANR<>();

			for(int nbAjout=0;nbAjout<totAjout;nbAjout++)
			{
				arbre.add((int)(Math.random()*(10+1)));

				if(nbAjout%100000 ==0 )
				{
					long tempsActuel = System.currentTimeMillis() - start;
					System.out.println(nbAjout +" , "+ tempsActuel); //Utile pour faire un doc permettant de tracer la courbe d'ou la virgule
				}
			}		
		}

		if(choixDeTest.equals("4"))  //Le cas le plus défavorable de la plus petite à la plus grande valeur
		{

			System.out.println("Nombre d'ajout | Temps d'execution");
			long start = System.currentTimeMillis(); //Permettre le calcul du temps d'exec
			int totAjout = 50000000 ; 
			ANR<Integer> arbre = new ANR<>();

			for(int nbAjout=0;nbAjout<totAjout;nbAjout++)
			{
				arbre.add(nbAjout);

				if(nbAjout%1000000 ==0 )
				{
					long tempsActuel = System.currentTimeMillis() - start;
					System.out.println(nbAjout +" , "+ tempsActuel); //Utile pour faire un doc permettant de tracer la courbe d'ou la virgule
				}
			}		
		}
	
	}

	public static String RandomString(int stringLength) 
	{
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = stringLength;
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    return generatedString;
	}
}
