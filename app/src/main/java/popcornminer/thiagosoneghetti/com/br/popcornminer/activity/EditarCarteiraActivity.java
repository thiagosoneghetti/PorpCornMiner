package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class EditarCarteiraActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private Carteira carteira;
    private EditText eChavePublica;
    private EditText eChavePrivada;
    private EditText eDescricao;
    private Button btAtualizarCarteira;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_carteira);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        // Pegando o contexto atual
        context = this;
        // Verificando se o usuário está logado, caso não, voltará para tela de inicio
        verificarSeUsuarioLogado();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

        // Recuperando os elementos da tela pelo ID
        eChavePublica = findViewById(R.id.editChavePublicaEdt);
        eChavePrivada = findViewById(R.id.editChavePrivadaEdt);
        eDescricao =  findViewById(R.id.editDescricaoEdt);
        btAtualizarCarteira = findViewById(R.id.btAtualizarCarteiraEdt);

        // Recuperando os dados que foram passados na Activity anterior
        final Intent intentQR = getIntent();
        if(intentQR.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intentQR.getSerializableExtra("carteira");
            // Passando os dados da carteira selecionada para a tela
            eDescricao.setText(carteira.getDescricao());
            eChavePublica.setText(carteira.getChave_publica());
            eChavePrivada.setText(carteira.getChave_privada());
        } else {
            Toast.makeText(this, "carteira vazia", Toast.LENGTH_SHORT).show();
        }

        // Função do botão "Adicionar" que faz a verificação dos campos e salva a carteira
        btAtualizarCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chavepublica = eChavePublica.getText().toString();
                String chaveprivada = eChavePrivada.getText().toString();
                String descricao = eDescricao.getText().toString();

                // Confirmando se algum campo está vazio antes de salvar
                if (chavepublica.equals("") || chaveprivada.equals("") || descricao.equals("")) {
                    // Verifiquando quais campos estão vazios e exibindo retorno
                    if (chavepublica.equals("") && chaveprivada.equals("") && descricao.equals("")) {
                        Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    } else if (descricao.equals("")) {
                        Toast.makeText(context, "Insira uma descrição.", Toast.LENGTH_SHORT).show();
                    } else if (chaveprivada.equals("")) {
                        Toast.makeText(context, "Insira uma chave privada.", Toast.LENGTH_SHORT).show();
                    } else if (chavepublica.equals("")) {
                        Toast.makeText(context, "Insira uma chave pública.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Verificando se há alguma chave inválida
                    if (chaveprivada.length() != 64 || chavepublica.length() != 66) {
                        // Verificando qual chave é inválida e dando retorno
                        if (chaveprivada.length() != 64) {
                            Toast.makeText(context, "Insira uma chave privada válida.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Salvando a carteira
                        // Método abaixo comentado é para salvar no SQLite, não utilizado mais
                        //salvar(chavepublica, chaveprivada, descricao);

                        // Método para salvar carteira no Firebase
                        salvarFB(chavepublica, chaveprivada, descricao);
                    }
                }
            }
        });

    }

    public void salvarFB(String chavepublica, String chaveprivada, String descricao){

        final Carteira carteiraFB = new Carteira(chavepublica, chaveprivada, descricao);

        //Recuperar o usuário pelo ID Base 64
        Preferencias preferencias = new Preferencias(context);
        String identificador = preferencias.getIdentificador();

        //Recuperar instância Firebase no local informado : usuarios >> email em base64
        // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
        firebase = Firebase.getFirebaseDatabase().child("usuarios").child(identificador);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Recuperar dados da conta vinculada ao identificador
                Usuario usuario = dataSnapshot.getValue( Usuario.class );

                // Verifica se o usuário existe
                if ( dataSnapshot.getValue() != null){

                    //Recuperar o usuário pelo ID Base 64
                    Preferencias preferencias = new Preferencias(context);
                    String identificador = preferencias.getIdentificador();

                    firebase = Firebase.getFirebaseDatabase();
                    // Pega um nó da carteira, um outro nó com o identificador (email base64) e a key identificador
                    firebase = firebase.child("carteiras")
                            .child( identificador ).child(carteira.getIdentificador());

                    // Atualizar a carteira no Firebase
                    firebase.setValue( carteiraFB );

                    // Verificando se possui conexão com a internet, se não, informa para o usuário que a carteira foi salva localmente
                    Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                    if ( conexaoInternet == true ) {
                        Toast.makeText(context, "Carteira "+ carteiraFB.getDescricao() +" atualizada.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"SEM INTERNET: Carteira "+ carteiraFB.getDescricao() +" foi atualizada localmente, será atualizada no servidor após conexão ser restabelecida." , Toast.LENGTH_LONG).show();
                    }

                    // Após atualizar vai para tela de Carteiras
                    Intent intent = new Intent(context, CarteiraActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    // Caso não encontre a conta criada, retorna uma mensagem para o usuário
                    Toast.makeText(context, "Usuário não encontrado!", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    };

    private void verificarSeUsuarioLogado(){
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        //Verificar se usuário está logado, caso não, volta para tela de login
        if ( usuarioFirebase.getCurrentUser() == null){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            // Fecha todas activitys que estavam na fila
            finishAffinity();
        }
    }

    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carteira,menu);
        if(usuarioFirebase.getCurrentUser() != null) {
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            MenuItem menuItem = menu.findItem(R.id.bt_mcart_sair);
            menuItem.setTitle("Sair: " + usuarioFirebase.getCurrentUser().getDisplayName());
        }

        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(context, CarteiraActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(context, MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(context, TransferenciaActivity.class);
                startActivity(irTransferencia);
                finish();
                break;
            case R.id.bt_mcart_sair:
                // Desconecta o usuário atual do aplicativo
                Toast.makeText(context, "Usuário " + usuarioFirebase.getCurrentUser().getDisplayName() +" desconectado.", Toast.LENGTH_SHORT).show();
                usuarioFirebase.signOut();
                Intent irLogin = new Intent(context, LoginActivity.class);
                startActivity(irLogin);
                // Fecha todas activitys que estavam na fila
                finishAffinity();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

}
