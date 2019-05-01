package blockio;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

public class BlockIO {
	public static float[] getSaldo(String api_key) throws Exception {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://block.io/api/v2/get_balance/?api_key=" + api_key);
		CloseableHttpResponse resposta = client.execute(get);
		
		Gson gson = new Gson();
		String jsonText = EntityUtils.toString(resposta.getEntity());
		BlockIOResposta bio_resposta = gson.fromJson(jsonText, BlockIOResposta.class);
		resposta.close();
		
		
		
		if (bio_resposta.status.equals("success")) {
			return new float[] {(float) 1.0, (float) 1.0};
		}/*
			float saldo = (float)bio_resposta.corpo.get("available_balance");
			float saldo_pendente = (float)bio_resposta.corpo.get("pending_received_balance");
			return new float[] {saldo, saldo_pendente};
		}else{
			throw new Exception("Erro na comunicacao com Block.io: " + bio_resposta.corpo.get("error_message"));
		}*/
	}
}