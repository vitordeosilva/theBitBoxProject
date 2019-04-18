package server;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

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

    @RequestMapping("/produtos/{id}")
    public ResponseEntity produto(@PathVariable("id") Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		if (produto.isPresent()){
			return ResponseEntity.ok(produto.get());
		}else{
		 	return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
		}
    }

	@PostMapping("/produtos")
	Produto newProduto(@RequestBody Produto produto) {
		return produtoRepository.save(produto);
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
