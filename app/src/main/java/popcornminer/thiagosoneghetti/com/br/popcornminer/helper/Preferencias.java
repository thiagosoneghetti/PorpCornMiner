package popcornminer.thiagosoneghetti.com.br.popcornminer.helper;

import android.content.Context;
import android.content.SharedPreferences;

import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;

public class Preferencias {
    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "popcornminer.preferencias";
    private final int MODE = 0;
    private  SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";

    // Construtor do SharedPreferences
    public Preferencias ( Context contextoParametro){
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }
    // Salvar o identificador do usuário atual
    public void salvarDados ( String identificadorUsuario ){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuario);
        editor.commit();
    }
    // Buscar o identificador do usuário atual
    public String getIdentificador() {
        return preferences.getString(CHAVE_IDENTIFICADOR, null);
    }

}
