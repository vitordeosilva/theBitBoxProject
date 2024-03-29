package server;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import resposta.*;
import blockio.*;

class Pin {
	String pin;

	public String getPin(){
		return pin;
	}
	public void setPin(){
		this.pin = pin;
	}
}

@RestController
public class HelloController {
	
	@Autowired
	ProdutoRepository produtoRepository;

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	TransacaoRepository transacaoRepository;

	@Autowired
	TrilhaRepository trilhaRepository;

	@Autowired
	MaquinaRepository maquinaRepository;
	
	@Autowired
	ComentarioRepository comentarioRepository;
	
	private int ESTADO_CLIENTE_ESCOLHEU_DOCE = 1;
	private int ESTADO_ESPERANDO_PAGAMENTO = 2;
	private int ESTADO_PAGAMENTO_CONFIRMADO = 3;
	private int ESTADO_PRODUTO_DISPENSADO = 4;
	private int ESTADO_PRODUTO_RETIRADO = 5;
	
	//tela de hello
	@RequestMapping("/")
	public ResponseEntity hello() {
		return ResponseEntity.ok("Hello from the BitBox server!");
	}


	//cria novo no banco
	@PostMapping("/produtos")
	public ResponseEntity newProduto(@RequestBody Produto produto) {
		return ResponseEntity.ok(produtoRepository.save(produto));
	}

	@PostMapping("/transacoes")
	public ResponseEntity newTransacao(@RequestBody Transacao transacao) {
		if (transacao.getID() != 0)
			return ResponseEntity.ok(new Resposta("ID must be 0", 1));
		List transacoes = transacaoRepository.findUnfinishedTransactionsFromMID(transacao.getMaquinaID());
		if (!transacoes.isEmpty())
			return ResponseEntity.ok(new Resposta("Machine already has an ongoing transaction", 1));
		transacao.setEstado(1);
		
		Optional <Usuario> user = usuarioRepository.findById(transacao.getUsuarioID());
		if (!user.isPresent())
			return ResponseEntity.ok(new Resposta("User not found", 1));
		Usuario u = user.get();
		
		Optional <Produto> prod = produtoRepository.findById(transacao.getProdutoID());
		if (!prod.isPresent())
			return ResponseEntity.ok(new Resposta("Product not found", 1));
		Produto p = prod.get();
		
		Optional <Maquina> maq = maquinaRepository.findById(transacao.getMaquinaID());
		if (!maq.isPresent())
			return ResponseEntity.ok(new Resposta("Machine not found", 1));
		Maquina m = maq.get();

		Optional<Trilha> t = trilhaRepository.getTrilha(transacao.getMaquinaID(), transacao.getProdutoID());
		if (!t.isPresent()){
			return ResponseEntity.ok(new Resposta("Trilha not found or product is not available", 1));
		}
		
		boolean result = false;		

		try{
			result = BlockIO.fazTransacao(u.getIdCarteira(), m.getIdCarteira(), p.getPrecoUnitario());
		} catch (Exception e) {
			return ResponseEntity.ok(new Resposta("Error communicating with block.io: " + e.toString(), 1));
		}
		if(result == true)
			transacao.setEstado(3);
		else
			return ResponseEntity.ok(new Resposta("Could not complete transaction", 1));

		transacao = transacaoRepository.save(transacao);
		return ResponseEntity.ok(new NovaTransacaoResposta("OK", 0, transacao.getID()));
	}
	
	@PostMapping("/maquinas")
	public ResponseEntity newMaquina(@RequestBody Maquina maquina) {
		return ResponseEntity.ok(maquinaRepository.save(maquina));
	}
	
	@PostMapping("/trilhas")
	public ResponseEntity newTrilha(@RequestBody Trilha trilha) {
		return ResponseEntity.ok(trilhaRepository.save(trilha));
	}
	
	@PostMapping("/usuarios")
	public ResponseEntity newUsuario(@RequestBody Usuario usuario) {
		usuario.setSenha(encodePassword(usuario.getSenha()));
		usuario.setPin(encodePassword(usuario.getPin()));
		return ResponseEntity.ok(usuarioRepository.save(usuario));
	}
	
	@PostMapping("/comentarios")
	public ResponseEntity newUsuario(@RequestBody Comentario comentario) {
		return ResponseEntity.ok(comentarioRepository.save(comentario));
	}
	
	@PostMapping("/usuarios/{id}/checkPin") 
	public ResponseEntity checkPin(@PathVariable("id") Long id, @RequestBody Pin pin) {
		Optional <Usuario> user = usuarioRepository.findById(id);
		if (!user.isPresent())
			return ResponseEntity.ok(new Resposta("User not found", 1));
		Usuario u = user.get();
		if (!checkPassword(pin.getPin(), u.getPin()))
			return ResponseEntity.ok(new Resposta("Wrong PIN", 1));
		
		return ResponseEntity.ok(new Resposta("OK", 0));
	}
	
	//deleta do banco por id
	@RequestMapping(value = "/produtos/{id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteProduto(@PathVariable("id") Long id) {
		produtoRepository.deleteById(id);
		return ResponseEntity.ok(new Resposta("Deleted", 0));
	}

	@RequestMapping(value = "/transacoes/{id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteTransacao(@PathVariable("id") Long id) {
		transacaoRepository.deleteById(id);
		return ResponseEntity.ok(new Resposta("Deleted", 0));
	}

	@RequestMapping(value = "/maquinas/{id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteMaquina(@PathVariable("id") Long id) {
		maquinaRepository.deleteById(id);
		return ResponseEntity.ok(new Resposta("Deleted", 0));
	}

	@RequestMapping(value = "/trilhas/{id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteTrilha(@PathVariable("id") Long id) {
		trilhaRepository.deleteById(id);
		return ResponseEntity.ok(new Resposta("Deleted", 0));
	}

	@RequestMapping(value = "/usuarios/{id}",method=RequestMethod.DELETE)
	public ResponseEntity deleteUsuario(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		return ResponseEntity.ok(new Resposta("Deleted", 0));
	}


	//muda estado da transacao
	@PostMapping("/transacoes/{id}")
	public ResponseEntity setTransacaoEstado(@PathVariable("id") Long id, @RequestParam("estado") int estado) {
		Optional<Transacao> transacao = transacaoRepository.findById(id);
		if (transacao.isPresent()){
			Transacao t = transacao.get();
			t.setEstado(estado);
			transacaoRepository.save(t);
			return ResponseEntity.ok(new Resposta("OK", 0));			
		}else{
		 	return ResponseEntity.ok(new Resposta("Transaction not found", 1));
		}
	}

	//muda imagem do produto
	@PostMapping("/produtos/{id}")
	public ResponseEntity setProdutoImagemURL(@PathVariable("id") Long id, @RequestParam("imagemURL") String imagemURL) {
		Optional<Produto> produto = produtoRepository.findById(id);
		if (produto.isPresent()){
			Produto p = produto.get();
			p.setImagemURL(imagemURL);
			produtoRepository.save(p);
			return ResponseEntity.ok(new Resposta("OK", 0));			
		}else{
		 	return ResponseEntity.ok(new Resposta("Product not found", 1));
		}
	}

	//muda qtde produtos na trilha
	@RequestMapping("/trilhas/{id}/{qtde}")
	public ResponseEntity setQtdeTrilha(@PathVariable("id") Long id, @PathVariable("qtde") int qtdeProdutos) {
		Optional<Trilha> trilha = trilhaRepository.findById(id);
		if (trilha.isPresent()){
			Trilha t = trilha.get();
			t.setQtdeProdutos(qtdeProdutos);
			trilhaRepository.save(t);
			return ResponseEntity.ok(new Resposta("OK", 0));			
		}else{
		 	return ResponseEntity.ok(new Resposta("Trilha not found", 1));
		}
	}


	//post usuario por nome e senha	
    @PostMapping(value="/login")
    public ResponseEntity usuarioByNameAndPasswd(@RequestBody Map<String, String> json) {
		String nome = json.get("nome");
		String senha = json.get("senha");
		Optional<Usuario> usuario = usuarioRepository.findByName(nome);
		if (usuario.isPresent()){
			Usuario user = usuario.get();

			if (!checkPassword(senha, user.getSenha()))
				return ResponseEntity.ok(new Resposta("USER NOT FOUND", 1));

			float[] saldos = null;
			try {
				saldos = BlockIO.getSaldo(user.getIdCarteira());
			} catch (Exception e) {
				return ResponseEntity.ok(new Resposta(e.toString(), 1));
			}
				
			float saldo = (float) saldos[0];
			float saldo_pendente = (float) saldos[1];
			List id_trans = transacaoRepository.findUnfinishedTransactionsFromUID(user.getId());
			return ResponseEntity.ok(new LoginResposta("OK", 0, user.getId(), saldo, saldo_pendente, id_trans));
		}else{
		 	return ResponseEntity.ok(new Resposta("USER NOT FOUND", 1));
		}
    }
	
	//get trilha para liberar produto
	@RequestMapping(value="/dispensar/{id}")
	public ResponseEntity dispense(@PathVariable("id") Long id) {
		List transacoes = transacaoRepository.findTransactionsWaitingToDispense(id);
		
		if (transacoes.size() == 0) {
			return ResponseEntity.ok(-1);
		}
		
		Transacao transacao = (Transacao) transacoes.get(0);
		Optional<Trilha> t = trilhaRepository.getTrilha(transacao.getMaquinaID(), transacao.getProdutoID());
		if (t.isPresent()){
			Trilha trilha = t.get();
			Long[] pos = {(long)trilha.getPosicaoLinha(), (long)trilha.getPosicaoColuna(), transacao.getID()};
			transacao.setEstado(4);
			transacaoRepository.save(transacao);
			return ResponseEntity.ok(pos);
		}else{
			//erro, a trilha esta vazia
			return ResponseEntity.ok(-1);
		}
	}
	
	//post produto dispensado
	@PostMapping(value="/dispensado/{id}")
	public ResponseEntity dispensed(@PathVariable("id") Long id, @RequestBody List<Integer> pos) {
		List transacoes = transacaoRepository.findTransactionsWaitingToConfirmDispensed(id);
		
		if (transacoes.size() == 0) {
			return ResponseEntity.ok(new Resposta("No transaction found", 1));
		}
		
		Transacao transacao = (Transacao) transacoes.get(0);
		Optional<Trilha> t = trilhaRepository.getTrilhaFromPos(transacao.getMaquinaID(), transacao.getProdutoID(), pos.get(0), pos.get(1));
		if (t.isPresent()){
			Trilha trilha = t.get();
			trilha.setQtdeProdutos(trilha.getQtdeProdutos()-1);
			trilhaRepository.save(trilha);
			transacao.setEstado(5);
			transacaoRepository.save(transacao);
			return ResponseEntity.ok(new Resposta("OK", 0));
		}
		return ResponseEntity.ok(new Resposta("Error finding trilha", 1));
	}

	//get produtos disponiveis na maquina 
    @RequestMapping("/itens/{maquina_id}")
    public ResponseEntity items(@PathVariable("maquina_id") Long maquina_id) {

		List itens = maquinaRepository.getItems(maquina_id);
		if (itens.size() == 0) {
			return ResponseEntity.ok(new Resposta("There are no available items in the machine", 1));
		}
		else{
			return ResponseEntity.ok(new MaquinaResposta("OK", 0, maquina_id, itens));
		}

    }

	//get all
	@RequestMapping("/produtos")
    public ResponseEntity produtos() {
		List produtos = produtoRepository.findAll();
		return ResponseEntity.ok(produtos);
    }
	
	@RequestMapping("/usuarios")
    public ResponseEntity usuarios() {
		List usuarios = usuarioRepository.findAll();
		return ResponseEntity.ok(usuarios);
    }
	
	@RequestMapping("/transacoes")
    public ResponseEntity transacoes() {
		List transacoes = transacaoRepository.findAll();
		return ResponseEntity.ok(transacoes);
    }
	
	@RequestMapping("/trilhas")
    public ResponseEntity trilhas() {
		List trilhas = trilhaRepository.findAll();
		return ResponseEntity.ok(trilhas);
    }
	
	@RequestMapping("/maquinas")
    public ResponseEntity maquinas() {
		List maquinas = maquinaRepository.findAll();
		return ResponseEntity.ok(maquinas);
    }
	
	@RequestMapping("/comentarios")
    public ResponseEntity comentarios() {
		List comentarios = comentarioRepository.findAll();
		return ResponseEntity.ok(comentarios);
    }

	//get por id
    @RequestMapping("/produtos/{id}")
    public ResponseEntity produto(@PathVariable("id") Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		if (produto.isPresent()){
			return ResponseEntity.ok(produto.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
    }

    @RequestMapping("/usuarios/{id}")
    public ResponseEntity usuario(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		if (usuario.isPresent()){
			return ResponseEntity.ok(usuario.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
    }
	
	@RequestMapping("/transacoes/{id}")
	public ResponseEntity transacao(@PathVariable("id") Long id) {
		Optional<Transacao> transacao = transacaoRepository.findById(id);
		if (transacao.isPresent()){
			Transacao trans = transacao.get();
			return ResponseEntity.ok(new TransacaoResposta("OK", 0, trans));
		}else{
		 	return ResponseEntity.ok(new Resposta("Transaction not found", 1));
		}
	}

	@RequestMapping("/trilhas/{id}")
	public ResponseEntity trilha(@PathVariable("id") Long id) {
		Optional<Trilha> trilha = trilhaRepository.findById(id);
		if (trilha.isPresent()){
			return ResponseEntity.ok(trilha.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping("/maquinas/{id}")
	public ResponseEntity maquina(@PathVariable("id") Long id) {
		Optional<Maquina> maquina = maquinaRepository.findById(id);
		if (maquina.isPresent()){
			return ResponseEntity.ok(maquina.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping("/comentarios/{id}")
	public ResponseEntity comentario(@PathVariable("id") Long id) {
		Optional<Comentario> comentario = comentarioRepository.findById(id);
		if (comentario.isPresent()){
			return ResponseEntity.ok(comentario.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
	}
	
	@Autowired BCryptPasswordEncoder passwordEncoder;
	public String encodePassword(String p) {
		return passwordEncoder.encode(p);
	}
	public Boolean checkPassword(String password_to_check, String db_password) {
		return passwordEncoder.matches(password_to_check, db_password);
	}
}
