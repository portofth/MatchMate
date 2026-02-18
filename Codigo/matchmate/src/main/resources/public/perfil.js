document.addEventListener('DOMContentLoaded', function() {
    const editBtn = document.getElementById('edit-btn');
    const saveBtn = document.getElementById('saveProfile');
    const modal = new bootstrap.Modal(document.getElementById('editProfileModal'));

    function loadProfile() {
        fetch('http://localhost:6789/api/perfil', {
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            // Atualiza os campos com os dados retornados
            document.getElementById('nome-usuario').textContent = data.nomeUsuario;
            document.getElementById('discord').textContent = `Discord: ${data.discordPerfil}`;
            document.getElementById('idade').textContent = `Idade: ${data.idade}`;
            document.getElementById('categoria').textContent = `Categoria: ${data.categoria}`;
            document.getElementById('data-cadastro').textContent = `Membro desde: ${new Date(data.dataCadastro).toLocaleDateString()}`;
            
            // Preenche o modal de edição com os dados atuais
            document.getElementById('edit-nome').value = data.nomeUsuario;
            document.getElementById('edit-discord').value = data.discordPerfil;
            document.getElementById('edit-idade').value = data.idade;
            document.getElementById('edit-categoria').value = data.categoria;
        })
        .catch(error => console.error('Erro ao carregar perfil:', error));
    }

    editBtn.addEventListener('click', function() {
                // Limpa os campos de senha 
                document.getElementById('edit-senha').value = '';
                document.getElementById('edit-confirma-senha').value = '';
        
        modal.show();
        
    });

	
	// Salvar alterações
	saveBtn.addEventListener('click', function() {
        const senha = document.getElementById('edit-senha').value;
	    const confirmaSenha = document.getElementById('edit-confirma-senha').value;

		if(senha === "" || confirmaSenha === "") {
					alert("Por favor preencher as senhas");
					return;
		}
				
	    if (senha || confirmaSenha) {
	        if (senha !== confirmaSenha) {
	            alert('As senhas não coincidem!');
	            return;
	        }
	    }

	    const newData = {
	        nome_usuario: document.getElementById('edit-nome').value,  // Corrigido: 'nome_usuario' ao invés de 'nomeUsuario'
	        discord_perfil: document.getElementById('edit-discord').value,
	        idade: document.getElementById('edit-idade').value,
	        categoria: document.getElementById('edit-categoria').value,
	        senha: senha // Só será incluído se uma nova senha for fornecida

	    }; 


	    // Remove a senha se estiver vazia
	    if (!senha) {
	        delete newData.senha;
	    }


	    // Enviar dados para o backend
	    fetch('http://localhost:6789/jogador/updatePerfil', {
	        method: 'POST',
	        headers: {
	            "Content-Type": "application/x-www-form-urlencoded"
	        },
	        credentials: 'include',
	        body: new URLSearchParams({
				nome: newData.nome_usuario,
				discord: newData.discord_perfil,
				idade: newData.idade,
				categoria: newData.categoria,
				senha: newData.senha,
			})
	    })
	    .then(response => {
	        if (response.ok) {
	            alert('Perfil atualizado com sucesso!');
	            loadProfile(); // Carregar novamente os dados atualizados
	        } else {
	            alert('Erro ao salvar perfil. Verifique o servidor.');
	        }
	    })
	    .catch(error => console.error('Erro ao salvar perfil:', error));

	    modal.hide(); // Fecha o modal
	});


    // Carregar perfil inicial
    loadProfile();
});
