function obterIdDaUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id');
}

const id = obterIdDaUrl();
const url = `https://api.rawg.io/api/games/${id}?key=515e12bfda4d446db7e7f4175b687895`;

async function loadInfo() {
    try {
        const resp = await fetch(url);
        if (resp.ok) {
            const game = await resp.json();
            document.getElementById("gameName").value = game.name;
        } else {
            alert("Erro ao carregar informações do jogo.");
        }
    } catch (err) {
        console.error("Erro ao buscar dados do jogo:", err);
    }
}

function createGroupRequest() {
    
    const lastCreateTime = localStorage.getItem('lastGroupCreateTime');
    const cooldownTime = 5000; // 5 segundos de cooldown da solicitação
    const now = Date.now();

    if (lastCreateTime && (now - parseInt(lastCreateTime) < cooldownTime)) {
        const remainingTime = Math.ceil((parseInt(lastCreateTime) + cooldownTime - now) / 1000);
        return;
    }

    const quant = parseInt(document.getElementById("gameQuant").value, 10);

    if (quant >= 10) {
        alert("A quantidade de jogadores deve ser menor que 10.");
        return;
    }

    const newData = {
        title: document.getElementById("groupTitle").value,
        gameName: document.getElementById("gameName").value,
        description: document.getElementById("groupDescription").value,
        playStyle: document.getElementById("playStyle").value,
        preferredTime: document.getElementById("preferredTime").value,
        platform: document.getElementById("platform").value,
        quant: quant,
    };

    fetch('http://localhost:6789/solicitacao_grupo', {
        method: 'POST',
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        credentials: 'include',
        body: new URLSearchParams({
            titulo: newData.title,
            nome: newData.gameName,
            descricao: newData.description,
            estilo: newData.playStyle,
            turno: newData.preferredTime,
            plataforma: newData.platform,
            quant: newData.quant,
        })
    })
    .then(async response => {
        if (response.ok) {
          //alert('Solicitação criada com sucesso!');
            localStorage.setItem('lastGroupCreateTime', now.toString());
            showToast("Solicitação criada com sucesso!", "bg-success");
        } else {
            const erro = await response.text();
             //alert('Erro ao criar solicitação: ' + erro);

            showToast("Erro ao criar solicitação, conteúdo inadequado detectado", "bg-danger");
            console.log('Erro ao criar solicitação: ' + erro);
        }
    })
    .catch(error => {
        console.error('Erro ao criar solicitação:', error);
        alert('Erro inesperado ao criar solicitação.');
    });
}

function showToast(mensagem, cor = "bg-primary") {
   const toastEl = document.getElementById('liveToast');
   const toastMsg = document.getElementById('toastMessage');

   toastMsg.textContent = mensagem;

   // Troca a cor de fundo dinamicamente (ex: "bg-danger", "bg-success")
   toastEl.className = `toast align-items-center text-white ${cor} border-0`;

   const toast = new bootstrap.Toast(toastEl); // Bootstrap 5
   toast.show();
 }

function logout() {
    fetch('http://localhost:6789/logout', { credentials: 'include' })
        .then(() => window.location.href = 'login.html');
}

document.addEventListener("DOMContentLoaded", () => {
    loadInfo();

    const saveBtn = document.getElementById('saveButton');
    if (saveBtn) {
        saveBtn.addEventListener('click', createGroupRequest);
    }
});
