package blockio;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

public class BlockIO {
	private static String api_key = "6bef-475f-4d48-2370";
	
	public static float[] getSaldo(String address) throws Exception {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://block.io/api/v2/get_balance/?api_key=" + api_key + "&address=" + address);
		CloseableHttpResponse resposta = client.execute(get);
		
		Gson gson = new Gson();
		String jsonText = EntityUtils.toString(resposta.getEntity());
		BlockIOResposta bio_resposta = gson.fromJson(jsonText, BlockIOResposta.class);
		resposta.close();		
		
		if (bio_resposta.status.equals("success")) {
			float saldo = Float.parseFloat((String) bio_resposta.data.get("available_balance"));
			float saldo_pendente = Float.parseFloat((String) bio_resposta.data.get("pending_received_balance"));
			return new float[] {saldo, saldo_pendente};
		}else{
			throw new Exception("Erro na comunicacao com Block.io: " + bio_resposta.data.get("error_message"));
		}
	}
}