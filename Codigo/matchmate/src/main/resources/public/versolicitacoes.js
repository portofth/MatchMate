function buscarGrupos() {

    fetch(`http://localhost:6789/grupo/myGrupos`)
      .then(response => {
        if (!response.ok) throw new Error("Erro ao buscar grupos.");
        return response.json();
      })
      .then(data => {
        console.log(data); // Verifica os dados recebidos
        exibirMinhasSolicitacoes(data);
    })
      .catch(error => {
        console.error("Erro:", error);
        document.getElementById("minhasSolicitacoes").innerHTML = "<p>Erro ao carregar os grupos.</p>";
      });

  }

function exibirMinhasSolicitacoes(grupos) {
    console.log("Grupos recebidos na exibição:", grupos);
    const container = document.getElementById("solicitacoesAceitas");
    container.innerHTML = "";
    const myContainer = document.getElementById("minhasSolicitacoes");
    myContainer.innerHTML = "";

    if (grupos.length === 0) {
        container.innerHTML = "<p>Nenhum grupo encontrado</p>";
        myContainer.innerHTML = "<p>Nenhum grupo encontrado</p>";
        return;
    }
        grupos.forEach(grupo => {
            const div = document.createElement("div");
            div.classList.add("solicitacao");

            if(grupo.idLogado === grupo.idLider){

                let membrosHTML = grupo.nomesMembros.map((nome, index) => {
                    const discord = grupo.discordsMembros[index] || "Sem Discord";
                    return `${nome} (${discord})`;
                }).join(", ");

                div.innerHTML = `
                    <h3>${grupo.titulo}</h3>
                    <p><strong>Líder:</strong> ${grupo.nomeLider} (${grupo.discordLider || "Sem Discord"})</p>
                    <p><strong>Jogo:</strong> ${grupo.nomeJogo}</p>
                    <p><strong>Descrição:</strong> ${grupo.descricao}</p>
                    <p><strong>Estilo:</strong> ${grupo.estilo_jogo}</p>
                    <p><strong>Turno:</strong> ${grupo.turno_preferido}</p>
                    <p><strong>Plataforma:</strong> ${grupo.plataforma}</p>
                    <p><strong>Membros:</strong> ${membrosHTML}</p>
                    <p style="color: red;"><strong>Observação:</strong> <strong>O anfitrião deve adicionar as pessoas no Discord para começar a jogar.</strong></p>
                `;

                div.innerHTML += `<h4><em>Você é o lider deste grupo.</em></h4>`;

                const excluirBtn = document.createElement("button");
                excluirBtn.textContent = "Excluir grupo";
                excluirBtn.classList.add("btn", "btn-primary");

                //css
                excluirBtn.style.backgroundColor = "#cf1d1d";
                excluirBtn.style.color = "white";
                excluirBtn.style.border = "none";
                excluirBtn.style.padding = "10px 16px";
                excluirBtn.style.borderRadius = "6px";
                excluirBtn.style.cursor = "pointer";
                excluirBtn.style.fontWeight = "bold";
                excluirBtn.style.fontSize = "14px";

                excluirBtn.addEventListener("click", () => {
                    fetch("http://localhost:6789/grupo/delete", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams({
                        idGrupo: grupo.idGrupo,
                    })
                    })
                    .then(response => {
                    if (!response.ok) throw new Error("Erro ao excluir grupo.");
					alert("Grupo excluído com sucesso!");
					div.remove();
					buscarGrupos();
                    })
                    .catch(err => {
                    console.error(err);
                    alert("Erro ao excluir grupo.");
                    });
                });

                div.appendChild(excluirBtn);

                myContainer.appendChild(div);
            }else{
                const div = document.createElement("div");
                div.classList.add("solicitacao");

                let membrosHTML = grupo.nomesMembros.map((nome, index) => {
                    const discord = grupo.discordsMembros[index] || "Sem Discord";
                    return `${nome} (${discord})`;
                }).join(", ");

                div.innerHTML = `
                    <h3>${grupo.titulo}</h3>
                    <p><strong>Líder:</strong> ${grupo.nomeLider} (${grupo.discordLider || "Sem Discord"})</p>
                    <p><strong>Jogo:</strong> ${grupo.nomeJogo}</p>
                    <p><strong>Descrição:</strong> ${grupo.descricao}</p>
                    <p><strong>Estilo:</strong> ${grupo.estilo_jogo}</p>
                    <p><strong>Turno:</strong> ${grupo.turno_preferido}</p>
                    <p><strong>Plataforma:</strong> ${grupo.plataforma}</p>
                    <p><strong>Membros:</strong> ${membrosHTML}</p>
                `;

                const sairBtn = document.createElement("button");
                sairBtn.textContent = "Sair do grupo";
                sairBtn.classList.add("btn", "btn-primary");

                //css
                sairBtn.style.backgroundColor = "#cf1d1d";
                sairBtn.style.color = "white";
                sairBtn.style.border = "none";
                sairBtn.style.padding = "10px 16px";
                sairBtn.style.borderRadius = "6px";
                sairBtn.style.cursor = "pointer";
                sairBtn.style.fontWeight = "bold";
                sairBtn.style.fontSize = "14px";

                sairBtn.addEventListener("click", () => {
                    fetch("http://localhost:6789/grupo/sair", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: new URLSearchParams({
                        idGrupo: grupo.idGrupo,
                        idJogador: grupo.idLogado
                    })
                    })
                    .then(response => {
                    if (!response.ok) throw new Error("Erro ao sair do grupo.");
                    alert("Saiu do grupo com sucesso!");
                    buscarGrupos();
                    })
                    .catch(err => {
                    console.error(err);
                    alert("Erro ao sair do grupo.");
                    });
                });
                div.appendChild(sairBtn);

                container.appendChild(div);
            }
            
        });
        
    }

// Carregar as solicitações ao abrir a página
window.addEventListener("load", () => {
    buscarGrupos();
});

