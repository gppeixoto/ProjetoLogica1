import java.util.Vector;

class Expressao{
	String rep;
	boolean val;
	Expressao(String rep){
		this.rep = rep;
		this.val = false;
	}
}

class functions{
	
	/**
	 * Função e(-)
	 * @param val1
	 * @param val2
	 * @return
	 */
	static boolean and(boolean val1, boolean val2){
		return (val1&&val2);
	}
	
	/**
	 * Função ou(+)
	 * @param val1
	 * @param val2
	 * @return
	 */
	static boolean or(boolean val1, boolean val2){
		return (val1||val2);
	}
	
	/**
	 * Função implica(>)
	 * @param val1
	 * @param val2
	 * @return
	 */
	static boolean implies(boolean val1, boolean val2){
		if (val1==true && val2==false){
			return false;
		}
		return true;
	}
	
	/**
	 * Função not (-) 
	 * @param val
	 * @return
	 */
	static boolean not(boolean val){
		return !val;
	}

	/**
	 * Confere se tal subexpressão já existe na lista e retorna um booleano
	 * @param in
	 * @param list
	 * @return boolean
	 */
	static boolean jaFoiAdicionado(Expressao in, Vector<Expressao> list){
		for (int i=0; i<list.size(); ++i){
			if ((in.rep).equals(list.elementAt(i).rep)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna um booleano se a expressão for atômica
	 * @param in
	 * @return boolean
	 */
	static boolean isAtom(Expressao in){ //confere se é atomica ou cte
		if (in.rep.length()==1) return true;
		return false;
	}

	/**
	 * Função que retorna dizendo se o objeto é uma constante 1 ou 0
	 * @param in
	 * @return
	 */
	static boolean isCte(Expressao in){
		if (in.rep.equals("1") || in.rep.equals("0")) return true;
		return false;
	}

	/**
	 * Retorna um booleano se o char for um operador do tipo +, . ou >.
	 * @param char a
	 * @return boolean
	 */
	static boolean isOp(char a){ //confere se é operador binario
		if (a=='+' || a=='.' || a=='>'){
			return true;
		}
		return false;
	}

	/**
	 * Método que a partir de uma EBF, preenche uma lista com
	 * cada uma das subexpressões da proposição original.
	 * @param String in
	 * @param Vector<String> list
	 */
	static void getAll(Expressao in, Vector<Expressao> list){ //insere todas as subexpressoes
		if (in.rep.length()==1){ //entrada atomica
			if (jaFoiAdicionado(in, list)==false){ //evita adicionar elementos que já estão no vector
				list.add(in);				
			}	
		} else if (in.rep.charAt(0)=='-'){ //entrada negacao
			if (in.rep.substring(1).length()==1){ //apos a negacao vem atomica
				getAll(new Expressao(in.rep.substring(1, in.rep.length())), list);
			}  else {
				getAll(new Expressao(in.rep.substring(2, in.rep.length()-1)), list);
			}
			if (jaFoiAdicionado(in, list)==false){//evita adicionar elementos que já estão no vector 
				list.add(in);
			}
		} else { //entrada resto
			int par_cnt = 0; //contador de parênteses
			for (int i=0; i<in.rep.length(); i++){
				if (in.rep.charAt(i)=='(') par_cnt++;
				if (in.rep.charAt(i)==')') par_cnt--;
				if (isOp(in.rep.charAt(i)) && par_cnt==0){
					if (isAtom(new Expressao(in.rep.substring(0, i)))){ //sub-expressao atomica a esquerda
						getAll(new Expressao(in.rep.substring(0, i)), list);
					} else { //sub-expressao n-atom a esquerda
						getAll(new Expressao(in.rep.substring(1, i-1)), list);
					} if (isAtom(new Expressao(in.rep.substring(i+1)))){ //sub-expressao atomica a direita
						getAll(new Expressao(in.rep.substring(i+1, in.rep.length())), list);
					} else { //sub-expressao n-atom a direita
						getAll(new Expressao(in.rep.substring(i+2, in.rep.length()-1)), list);
					}
					if (jaFoiAdicionado(in, list)==false){//evita adicionar elementos que já estão no vector
						list.add(in);						
					}
				}
			}
		}
	}

	/**
	 * Retorna o indice da posição na qual tal subexpressao se encontra no Vector<Expressao>
	 * @param in
	 * @param list
	 * @return
	 */
	static int indiceExp(Expressao in, Vector<Expressao> list){
		int i;
		boolean sair=true;
		for (i=0; i<list.size() && sair; i++){
			if (in.rep.equals(list.elementAt(i).rep)){
				sair=false;
				i--;
			}
		}
		return i;
	}

	/**
	 * Ordena a lista de subexpressões de acordo com as especificações:
	 * Se for atômica: x | y | z | t
	 * Senão, ordena por tamanho da subexpressão (e por ordem lexicográfica se for tamanho igual)
	 * @param Vector<Expressao> list
	 */
	public static void sort (Vector<Expressao> list){
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < (list.size())-1; i++){
				if (list.get(i).rep.length()>=list.get(i+1).rep.length()){
					if (list.get(i).rep.length()==list.get(i+1).rep.length()){//sort na ordem lexicografica
						if (list.get(i).rep.compareTo(list.get(i+1).rep)>0){
							Expressao aux = list.get(i+1);
							list.set(i+1, list.get(i));
							list.set(i, aux);
							change = true;
						}
					} else { //sort no tamanho da string
						Expressao aux = list.get(i+1);
						list.set(i+1, list.get(i));
						list.set(i, aux);
						change = true;						
					}
				}
			}
		}
		//ajuste para o t ficar após x, y, z
		int j=0;
		if (list.size()>1){
			while (isAtom(list.get(j))){ 
				if (list.get(j).rep.equals("t") && isAtom(list.get(j+1))){
					Expressao aux = list.get(j+1);
					list.set(j+1, new Expressao("t"));
					list.set(j, aux);
				}
				j++;
			}			
		}
	}

	/**
	 * Faz o cálculo da valoração para cada subexpressão da lista
	 * @param list
	 */
	static void valorar (Vector<Expressao> list){
		for (int i=0; i<list.size(); i++){
			String exp = list.get(i).rep;
			int k=0;
			if (isAtom(list.get(i))){
				if (exp.equals("1")){
					list.get(i).val = true;
				} else if (exp.equals("0")){
					list.get(i).val = false;
				}
			} else if (exp.charAt(0)=='-'){ //entrada negacao
				if (exp.substring(1, exp.length()).length()==1){ //apos - vem atomica
					k = indiceExp(new Expressao(exp.substring(1, exp.length())), list);
				} else {
					k = indiceExp(new Expressao(exp.substring(2, exp.length()-1)), list);
				}
				list.get(i).val = not(list.get(k).val);
			} else { //entrada resto
				int par_cnt = 0;
				for (int j=0; j<exp.length(); j++){
					if (exp.charAt(j)=='(') par_cnt++;
					if (exp.charAt(j)==')') par_cnt--;
					if (par_cnt==0 && isOp(exp.charAt(j))){
						char op = exp.charAt(j);
						int k1=0; //indice da subexp à esquerda
						int k2=0; //indice da subexp à direita
						if (isAtom(new Expressao(exp.substring(0, j)))){ //sub-expressao a esquerda é atômica
							k1 = indiceExp(new Expressao(exp.substring(0, j)), list);
							if (isAtom(new Expressao(exp.substring(j+1)))){
								k2 = indiceExp(new Expressao(exp.substring(j+1)), list);
							} else {
								k2 = indiceExp(new Expressao(exp.substring(j+2, exp.length()-1)), list);
							}
						} else if (!isAtom(new Expressao(exp.substring(0, j)))) { //subexpressão a esquerda não é atômica
							k1 = indiceExp(new Expressao(exp.substring(1, j-1)), list);
							if (isAtom(new Expressao(exp.substring(j+1)))){
								k2 = indiceExp(new Expressao(exp.substring(j+1)), list);
							} else {
								k2 = indiceExp(new Expressao(exp.substring(j+2, exp.length()-1)), list);
							}
						} else if (isAtom(new Expressao(exp.substring(j+1)))){ //sub-expressao atomica a direita
							k2 = indiceExp(new Expressao(exp.substring(j+1)), list);
							if (isAtom(new Expressao(exp.substring(0, j)))){
								k1 = indiceExp(new Expressao(exp.substring(0, j)), list);
							} else {
								k1 = indiceExp(new Expressao(exp.substring(1, j-1)), list);
							}
						} else { //subexpressão a direita não é atômica
							k2 = indiceExp(new Expressao(exp.substring(j+2, exp.length())), list);
							if (isAtom(new Expressao(exp.substring(0, j)))){
								k1 = indiceExp(new Expressao(exp.substring(0, j)), list);
							} else {
								k1 = indiceExp(new Expressao(exp.substring(1, j-1)), list);
							}
						}
						if (op=='+'){
							list.get(i).val = or(list.get(k1).val, list.get(k2).val);
						} else if (op=='.'){
							list.get(i).val = and(list.get(k1).val, list.get(k2).val);
						} else {
							list.get(i).val = implies(list.get(k1).val, list.get(k2).val);
						}
					}
				}//end for
			}//end entrada resto
		}
	}

	/**
	 * Confere se uma subexpressão não é formada apenas por constantes
	 * @param rep
	 * @return
	 */
	static boolean temVariavel(String rep){
		for (int i=0; i<rep.length(); i++){
			if (rep.charAt(i)=='x' || rep.charAt(i)=='y' ||  rep.charAt(i)=='z' ||  rep.charAt(i)=='t'){
				return true;
			}
		}
		return false;
	}

	/**
	 * Retorna o número de variáveis que contém na expressão original
	 * @param list
	 * @return
	 */
	static int numeroVariaveis(Vector<Expressao> list){
		int count=0;
		for (int i=0; i<list.size() && list.get(i)!=null; i++){
			if (list.get(i).rep.equals("x") || list.get(i).rep.equals("y") 
					|| list.get(i).rep.equals("z") || list.get(i).rep.equals("t")){
				count++;
			}
			if (count==4)return count;
		}
		return count;
	}

	/***
	 * Gera uma matriz de booleanos com todas as combinações possíveis de valoração
	 * para as variáveis atômicas
	 * @param n (numero de variaveis)
	 * @return boolean[][]
	 */
	static boolean[][] gerarMatriz(int n){
		int a = (int) Math.pow(2,n);
		boolean[][] valores = new boolean[a][n];
		switch (n) {
		case 1:
			valores[0][0] = false;
			valores[1][0] = true;
			break;
		case 2:
			valores[0][0] = false;
			valores[0][1] = false;
			valores[1][0] = false;
			valores[1][1] = true;
			valores[2][0] = true;
			valores[2][1] = false;
			valores[3][0] = true;
			valores[3][1] = true;
			break;
		case 3:
			for (int i=0; i<8; i++){
				valores[i][0]=false;
				if (i>3) valores[i][0]=true;
			}
			for (int i=0; i<8; i++){
				valores[i][1]=false;
				if (i==2 || i==3 || i==6 || i==7) valores[i][1]=true;
			}
			for (int i=0; i<8; i++){
				valores[i][2]=false;
				if (i>0 && i%2!=0) valores[i][2]=true;
			}
			break;
		case 4:
			for (int i=0; i<16; i++){
				valores[i][0]= false;
				if (i>7) valores[i][0]=true;
			}
			for (int i=0; i<16; i++){
				valores[i][1]=false;
				if ((i>=4 && i<=7)||(i>=12 && i<=15)) valores[i][1]=true;
			}
			for (int i=0; i<16; i++){
				valores[i][2]=false;
				if (i==2 || i==3 || i==6 || i==7 || i==10 || i==11 
						|| i==14 || i==15)valores[i][2]=true;
			}
			for (int i=0; i<16; i++){
				valores[i][3]=false;
				if (i>0 && i%2!=0) valores[i][3]=true;
			}
			break;
		}
		return valores;
	}

	/**
	 * Printa a tabela-verdade
	 * @param list
	 */
	static String printExp(Vector<Expressao> list){
		String s="";
		for (int i=0; i<list.size(); i++){
			if (isAtom(list.get(i)) && !list.get(i).rep.equals("1") && !list.get(i).rep.equals("0")){
				s += "|"+list.get(i).rep;
			} else {
				if (temVariavel(list.get(i).rep)){
					s += "|(" + list.get(i).rep + ")";
				}
			}
		}
		s+="|";
		return s;
	}
	
	/**
	 * Printa os tracinhos
	 * @param s
	 */
	static void printTraco(String s, Arquivo arq){
		for (int i=0; i<s.length(); i++){
			arq.print("-");
		}
		arq.println();
	}
	
	/**
	 * Printa a valoração de cada linha com a respectiva identação
	 * @param list
	 */
	static void printVal(Vector<Expressao> list, Arquivo arq){
		int numespacos = 0;
		for (int i=0; i<list.size(); i++){
			if (isAtom(list.get(i))){
				numespacos = list.get(i).rep.length()-1;				
			} else {
				numespacos = list.get(i).rep.length()+1;
			}
			if (isCte(list.get(i))){

			} else {
				if (temVariavel(list.get(i).rep)){
					arq.print("|");
					if (list.get(i).val){
						for (int j=0; j<numespacos; j++){
							arq.print(" ");
						}
						arq.print("1");
					} else {
						for (int j=0; j<numespacos; j++){
							arq.print(" ");
						}
						arq.print("0");
					}					
				}
			}
		}
	}
	
	/**
	 * Printa a avaliação do resultado final da tabela, se é satisfativel/refutavel/etc
	 * @param aux3
	 */
	static void printResult(boolean[] aux3, Arquivo arq){
		//satisfativel, refutavel, tautologia, insatisfativel
		boolean[] a = {false, false, false, false};
		//satisfativel, refutavel
		for (int i=0; i<aux3.length; i++){
			if (aux3[i]){
				a[0]=true;
			} else {
				a[1]=true;
			}
		}
		//tautologia, insatisfativel
		boolean taut = true;
		for (int i=0; i<aux3.length; i++){
			if (aux3[i]==false){
				taut=false;
			}
		}
		a[2]=taut;
		if (a[0] && a[1] && !a[2] && !a[3]){
			arq.print("satisfativel e refutavel");
		} else if (a[2] && !a[1]){
			arq.print("satisfativel e tautologia");
		} else if (!a[3] && !a[2]){
			arq.print("insatisfativel e refutavel");
		}
		arq.println();
	}

}

public class Gerador{
	public static void main(String[] args) {
		Vector<Expressao> list = new Vector<Expressao>();
		Arquivo arq = new Arquivo("Expressoes.in", "Expressoes.out");	
		int cases= arq.readInt();
		int cnt=1;

		do {
			if (cases!=0){
				list.clear();
				String rep = arq.readString();
				if (!functions.temVariavel(rep)){
					arq.println("Tabela #" + cnt);
					arq.println();
				} else {
					Expressao in = new Expressao(rep);
					if (functions.isAtom(in)) {
						list.add(in);
					} else {
						functions.getAll(new Expressao(in.rep.substring(1, in.rep.length()-1)), list);
					}
					functions.sort(list);
					arq.println("Tabela #" + cnt);
					String s = functions.printExp(list);
					functions.printTraco(s, arq);
					arq.println(s);
					functions.printTraco(s, arq);
					int aux=0;
					int aux2=(int)Math.pow(2, functions.numeroVariaveis(list));
					boolean[] aux3 = new boolean[aux2];
					boolean[][] mat = functions.gerarMatriz(functions.numeroVariaveis(list));
					for (int i=0; i<aux2; i++){
						for (int j=0; j<list.size(); j++){
							if (functions.isAtom(list.get(j))){
								if (list.get(j).rep.equals("1")){
									list.get(j).val=true;
								} else if (list.get(j).rep.equals("0")){
									list.get(j).val=false;
								} else {
									list.get(j).val = mat[i][aux];
									aux++;
									if (aux==4) break;
								}
							}
						}
						aux=0;
						functions.valorar(list);
						aux3[i] = list.get(list.size()-1).val;
						functions.printVal(list, arq);
						arq.print("|");
						arq.println();
						functions.printTraco(s, arq);
					}
					functions.printResult(aux3, arq);
					arq.println();
				}
				cnt++;
				cases--;				
			}
		} while (cases>0);
	
	}


}