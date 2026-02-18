function obterIdDaUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}

const id = obterIdDaUrl();
const modal = new bootstrap.Modal(document.getElementById('editProfileModal'));
const saveBtn = document.getElementById('saveSolicitacao');
modal.hide();

const url = `https://api.rawg.io/api/games/${id}?key=515e12bfda4d446db7e7f4175b687895`;
const screen = `https://api.rawg.io/api/games/${id}/screenshots?key=515e12bfda4d446db7e7f4175b687895`;

let nomeDoJogo = ""; // Variável global para guardar o nome

async function loadInfo() {
  const resp = await fetch(url);
  const respScreen = await fetch(screen);

  if (respScreen.status === 200) {
    const data = await respScreen.json();
    const screenshotsContainer = document.querySelector('#game-screenshots');

    data.results.slice(0, 5).forEach(screenshot => {
      const img = document.createElement('img');
      img.src = screenshot.image;
      img.alt = 'Screenshot do jogo';
      img.style.padding = '10px';
      img.classList.add('screenshot-img');
      screenshotsContainer.appendChild(img);
    });
  }

  if (resp.status === 200) {
    const game = await resp.json();
    nomeDoJogo = game.name;

    document.querySelector('#game-name').textContent = game.name;
    document.querySelector('#game-description').innerHTML = game.description || 'Sem descrição disponível.';
    document.querySelector('#game-image').src = game.background_image;
    document.querySelector('#game-platforms').textContent = game.platforms.map(p => p.platform.name).join(', ');
    document.querySelector('#game-release-date').textContent = game.released || 'Data não informada';
    document.querySelector('#game-rating').textContent = `${game.rating} / 5`;
    document.getElementById('edit-nome').value = game.name;

    const btnCria = document.getElementById("btnCriarSolicitacao");
    btnCria.href = `./recomenda.html?id=${game.id}`;
  } else {
    document.querySelector('#game-name').textContent = 'Jogo não encontrado';
  }
}

document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("btnBuscarGrupos").addEventListener("click", function (e) {
    e.preventDefault();
    buscarGrupos(nomeDoJogo);
  });

  document.getElementById("groupRequestForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    try {
      console.log("Enviando dados:", Object.fromEntries(params.entries()));

      const response = await fetch(form.action, {
        method: "POST",
        body: params,
      });

      const text = await response.text();

      if (!response.ok || text.includes("Conteúdo Inapropriado")) {
		
        showToast("Erro ao alterar solicitação, conteúdo inadequado detectado", "bg-danger");
		console.log('Erro ao criar solicitação: ' + text);
        return;
      }

      showToast("Grupo alterado com sucesso!", "bg-success");
      modal.hide();
      buscarGrupos(nomeDoJogo);
    } catch (err) {
      alert("Erro inesperado ao atualizar o grupo.");
      console.error(err);
    }
  });
});

function buscarGrupos(nome) {
  if (!nome) {
    alert("Nome do jogo ainda não carregado.");
    return;
  }

  fetch(`http://localhost:6789/grupos/${encodeURIComponent(nome)}`)
    .then(res => {
      if (!res.ok) throw new Error("Erro ao buscar grupos.");
      return res.json();
    })
    .then(data => exibirGrupos(data))
    .catch(error => {
      console.error("Erro:", error);
      document.getElementById("grupos").innerHTML = "<p>Erro ao carregar os grupos.</p>";
    });
}

function exibirGrupos(grupos) {
  const container = document.getElementById("grupos");
  container.innerHTML = "";

  if (grupos.length === 0) {
    container.innerHTML = "<p>Nenhum grupo encontrado para este jogo.</p>";
    return;
  }

  grupos.forEach(grupo => {
    const jaEhMembro = grupo.idsMembros.includes(grupo.idLogado);
    const div = document.createElement("div");
    div.classList.add("grupo");

    let membrosHTML = grupo.nomesMembros.map((nome, index) => {
      const discord = grupo.discordsMembros[index] || "Sem Discord";
      return `${nome} (${discord})`;
    }).join(", ");

    if (grupo.idLogado === grupo.idLider) {
      div.innerHTML = `
        <h3>${grupo.titulo} - Grupo #${grupo.idGrupo}</h3>
        <p><strong>Líder:</strong> ${grupo.nomeLider} (${grupo.discordLider || "Sem Discord"})</p>
        <p><strong>Descrição:</strong> ${grupo.descricao}</p>
        <p><strong>Turno preferido:</strong> ${grupo.turno_preferido}</p>
        <p><strong>Estilo:</strong> ${grupo.estilo_jogo}</p>
        <p><strong>Plataforma:</strong> ${grupo.plataforma}</p>
        <p><strong>Máx. Jogadores:</strong> ${grupo.maxJogadores}</p>
        <p><strong>Membros:</strong> ${membrosHTML}</p>
        <h4><em>Você é o lider deste grupo.</em></h4>
      `;

      const excluirBtn = document.createElement("button");
      excluirBtn.textContent = "Excluir grupo";
      excluirBtn.classList.add("btn", "btn-primary");
      excluirBtn.style.backgroundColor = "#cf1d1d";
      excluirBtn.style.color = "white";
      excluirBtn.style.border = "none";
      excluirBtn.style.padding = "10px 16px";
      excluirBtn.style.borderRadius = "6px";
      excluirBtn.style.cursor = "pointer";
      excluirBtn.style.fontWeight = "bold";
      excluirBtn.style.fontSize = "14px";

      const updateBtn = document.createElement("button");
      updateBtn.textContent = "Atualizar grupo";
      updateBtn.classList.add("btn", "btn-primary");
      updateBtn.style.backgroundColor = "#c4c126";
      updateBtn.style.color = "white";
      updateBtn.style.border = "none";
      updateBtn.style.padding = "10px 16px";
      updateBtn.style.borderRadius = "6px";
      updateBtn.style.cursor = "pointer";
      updateBtn.style.fontWeight = "bold";
      updateBtn.style.fontSize = "14px";

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
            alert("Grupo excluido com sucesso!");
            buscarGrupos(nomeDoJogo);
          })
          .catch(err => {
            console.error(err);
            alert("Erro ao excluir grupo.");
          });
      });

      updateBtn.addEventListener("click", () => {
        document.getElementById('edit-id').value = grupo.idGrupo;
        modal.show();
      });

      div.appendChild(updateBtn);
      div.appendChild(excluirBtn);
    } else if (jaEhMembro) {
      div.innerHTML = `
        <h3>${grupo.titulo} - Grupo #${grupo.idGrupo}</h3>
        <p><strong>Líder:</strong> ${grupo.nomeLider} (${grupo.discordLider || "Sem Discord"})</p>
        <p><strong>Descrição:</strong> ${grupo.descricao}</p>
        <p><strong>Turno preferido:</strong> ${grupo.turno_preferido}</p>
        <p><strong>Estilo:</strong> ${grupo.estilo_jogo}</p>
        <p><strong>Plataforma:</strong> ${grupo.plataforma}</p>
        <p><strong>Máx. Jogadores:</strong> ${grupo.maxJogadores}</p>
        <p><strong>Membros:</strong> ${membrosHTML}</p>
        <p><em>Você já é membro deste grupo.</em></p>
      `;

      const sairBtn = document.createElement("button");
      sairBtn.textContent = "Sair do grupo";
      sairBtn.classList.add("btn", "btn-primary");
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
            buscarGrupos(nomeDoJogo);
          })
          .catch(err => {
            console.error(err);
            alert("Erro ao sair do grupo.");
          });
      });

      div.appendChild(sairBtn);
    } else {
      div.innerHTML = `
        <h3>${grupo.titulo} - Grupo #${grupo.idGrupo}</h3>
        <p><strong>Líder:</strong> ${grupo.nomeLider} (${grupo.discordLider || "Sem Discord"})</p>
        <p><strong>Descrição:</strong> ${grupo.descricao}</p>
        <p><strong>Turno preferido:</strong> ${grupo.turno_preferido}</p>
        <p><strong>Estilo:</strong> ${grupo.estilo_jogo}</p>
        <p><strong>Plataforma:</strong> ${grupo.plataforma}</p>
        <p><strong>Máx. Jogadores:</strong> ${grupo.maxJogadores}</p>
        <p><strong>Membros:</strong> ${membrosHTML}</p>
      `;

      const entrarBtn = document.createElement("button");
      entrarBtn.textContent = "Entrar no grupo";
      entrarBtn.classList.add("btn", "btn-primary");
      entrarBtn.style.backgroundColor = "#28a745";
      entrarBtn.style.color = "white";
      entrarBtn.style.border = "none";
      entrarBtn.style.padding = "10px 16px";
      entrarBtn.style.borderRadius = "6px";
      entrarBtn.style.cursor = "pointer";
      entrarBtn.style.fontWeight = "bold";
      entrarBtn.style.fontSize = "14px";

      entrarBtn.addEventListener("click", () => {
        fetch("http://localhost:6789/grupo/entrar", {
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
            if (!response.ok) throw new Error("Erro ao entrar no grupo.");
            alert("Entrou no grupo com sucesso!");
            buscarGrupos(nomeDoJogo);
          })
          .catch(err => {
            console.error(err);
            alert("Erro ao entrar no grupo.");
          });
      });

      div.appendChild(entrarBtn);
    }

    container.appendChild(div);
  });
}

function showToast(mensagem, cor = "bg-primary") {
  const toastEl = document.getElementById('liveToast');
  const toastMsg = document.getElementById('toastMessage');

  toastMsg.textContent = mensagem;
  toastEl.className = `toast align-items-center text-white ${cor} border-0`;

  const toast = new bootstrap.Toast(toastEl);
  toast.show();
}

loadInfo();
