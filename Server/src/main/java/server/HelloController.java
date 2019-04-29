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
import java.util.Optional;
import java.util.List;
import resposta.*;

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


	//cria novo no banco

	@PostMapping("/produtos")
	public ResponseEntity newProduto(@RequestBody Produto produto) {
		return ResponseEntity.ok(produtoRepository.save(produto));
	}

	@PostMapping("/transacoes")
	public ResponseEntity newTransacao(@RequestBody Transacao transacao) {
		return ResponseEntity.ok(transacaoRepository.save(transacao));
	}


	//muda estado da transacao
	@PostMapping("/transacoes/{id}")
	public ResponseEntity setTransacaoEstado(@PathVariable("id") Long id, @RequestParam("estado") int estado) {
		Optional<Transacao> transacao = transacaoRepository.findById(id);
		System.out.println("estado=" + estado);
		if (transacao.isPresent()){
			transacao.get().setEstado(estado);
			return ResponseEntity.ok(transacaoRepository.save(transacao.get()));
		}else{
			System.out.println("transacao not found");
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
	}


	//get usuario por nome e senha	
    @RequestMapping(value="/usuarios", method = RequestMethod.GET)
    public ResponseEntity usuarioByNameAndPasswd(@RequestParam("nome") String nome, @RequestParam("senha") String senha) {
		System.out.println("nome=" + nome + ", senha=" + senha);
		Optional<Usuario> usuario = usuarioRepository.findByNameAndPasswd(nome,senha);
		if (usuario.isPresent()){
			return ResponseEntity.ok(usuario.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
    }
	
	//get trilha para liberar produto
	@RequestMapping(value="/dispensar/{id}")
	public ResponseEntity dispense(@PathVariable("id") Long id) {
		List transacoes = transacaoRepository.findTransactionsWaitingToDispense(id);
		
		if (transacoes.size() == 0) {
			return ResponseEntity.ok(new DispensarResposta("NO TRANSACTIONS", 1, -1));
		}
		
		Transacao transacao = (Transacao) transacoes.get(0);
		Optional<Integer> pos = trilhaRepository.getPosition(transacao.getMaquinaID(), transacao.getProdutoID());
		if (pos.isPresent()){
			return ResponseEntity.ok(new DispensarResposta("", 0, pos.get()));
		}else{
			//erro, a trilha esta vazia
			return ResponseEntity.ok(new DispensarResposta("NO PRODUCT", 1, pos.get()));
		}
	}

	//get
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
			return ResponseEntity.ok(transacao.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
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
	

}