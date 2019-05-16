package blockio;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import java.io.*;

public class BlockIO {
	private static String api_key = "6bef-475f-4d48-2370";
	private static String pin = "TrupeDoBitBox";
	
	public static float[] getSaldo(String address) throws Exception {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("https://block.io/api/v2/get_address_balance/?api_key=" + api_key + "&address=" + address);
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
	
	public static boolean fazTransacao(String source_address, String destination_address, float amount) throws Exception {
		String command = "python BlockIOTransaction.py " + Float.toString(amount) + " " + source_address + " " + destination_address;
		String out = "";
		String line = "";
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		while((line = reader.readLine()) != null)
			out += line;
		
		Gson gson = new Gson();
		BlockIOResposta bio_resposta = gson.fromJson(out, BlockIOResposta.class);		
		
		if (bio_resposta.status.equals("success"))
			return true;
		return false;
	}
}
