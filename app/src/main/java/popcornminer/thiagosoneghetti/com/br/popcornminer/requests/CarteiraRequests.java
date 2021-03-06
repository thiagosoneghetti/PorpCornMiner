package popcornminer.thiagosoneghetti.com.br.popcornminer.requests;

import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Saldo;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Transferencia;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarteiraRequests {
    // http://www.mobimais.com.br/blog/retrofit-2-consumir-json-no-android/

    // Responsável pela transferencia
    // É informado o tipo de requisição (POST)
    // É completada o restante da URl que foi passada na instancia service do retrofit, contendo chaves nas partes onde receberá algum dado,
    // É criada uma call com a classe Transferencia com um método getTransferencia, que receberá os parametros, e substituirá onde tiver o @Path
    // de acordo com as chaves {} inseridas na URL
    @POST("{sua_chave_privada}/{chave_publica_destino}/{valor}")
    Call<Transferencia> getTransferencia(@Path("sua_chave_privada") String sua_chave_privada,
                                         @Path("chave_publica_destino") String chave_publica_destino,
                                         @Path("valor") float valor);

    // Responsável pela consulta de saldo
    // É informado o tipo de requisição (GET)
    // É completada o restante da URl que foi passada na instancia service do retrofit, contendo chaves nas partes onde receberá algum dado,
    // É criada uma call com a classe Saldo com um método getSaldo, que receberá a chave publica, e substituirá onde tiver o @Path
    // de acordo com as chaves {} inseridas na URL
    @GET("{chavepublica}")
    Call<Saldo> getSaldo(@Path("chavepublica") String chavepublica);
}
