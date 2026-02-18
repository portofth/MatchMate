document.addEventListener('DOMContentLoaded', () => {
  const currentPage = window.location.pathname.split('/').pop();

  if (currentPage === 'games.html') {
      loadGames();
  } else if (currentPage === 'details.html') {
      loadGameDetails();
  }
});

// Função para carregar a lista de jogos
function loadGames() {
  const gameList = document.getElementById('gameList');
  const url = 'http://localhost:3000/games';

  fetch(url)
      .then(response => response.json())
      .then(games => {
          gameList.innerHTML = games.map(game => `
              <div class="col-md-4 game-card">
                  <div class="card">
                      <img src="${game.image}" class="card-img-top" alt="${game.name}">
                      <div class="card-body">
                          <h5>${game.name}</h5>
                          <a href="details.html?id=${game.id}" class="btn btn-primary">Ver Detalhes</a>
                      </div>
                  </div>
              </div>
          `).join('');
      })
      .catch(error => console.error('Erro ao carregar os jogos:', error));
}

// Função para carregar os detalhes do jogo
function loadGameDetails() {
  const params = new URLSearchParams(window.location.search);
  const gameId = params.get('id');

  if (!gameId) {
      alert('ID do jogo não encontrado.');
      window.location.href = 'games.html';
      return;
  }

  const url = `http://localhost:3000/games/${gameId}`;

  fetch(url)
      .then(response => {
          if (!response.ok) {
              throw new Error('Jogo não encontrado.');
          }
          return response.json();
      })
      .then(game => {
          document.querySelector('.game-name').textContent = game.name;
          document.querySelector('.game-image').src = game.image;
          document.querySelector('.game-image').alt = game.name;
          document.getElementById('gameDescription').textContent = game.description;
      })
      .catch(error => {
          console.error('Erro ao carregar os detalhes do jogo:', error);
          document.body.innerHTML = `
              <div class="container text-center my-5">
                  <h1>Erro</h1>
                  <p>Não foi possível carregar os detalhes do jogo.</p>
                  <a href="games.html" class="btn btn-primary">Voltar</a>
              </div>
          `;
      });
}