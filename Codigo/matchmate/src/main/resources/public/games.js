const gameList = document.querySelector(".gameList");
const loaderEl = document.getElementById("js-preloader");
const loadMoreGamesBtn = document.querySelector(".main-button");
const searchInput = document.getElementById("barra");
const searchButton = document.getElementById("search-button");

let nextGameListUrl = null;
let currentSearchTerm = '';
const baseUrl = `https://api.rawg.io/api/games?key=515e12bfda4d446db7e7f4175b687895&dates=2000-01-01,2024-12-31&ordering=-playtime`;

const getPlatformStr = (platforms) => {
    const platformStr = platforms.map(pl => pl.platform.name).join(", ");
    if (platformStr.length > 30) {
        return platformStr.substring(0, 30) + "...";
    }
    return platformStr;
}

function loadGames(url) {
    loaderEl.classList.remove("loaded");
    gameList.innerHTML = ''; // Limpa os jogos atuais antes de carregar novos
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            nextGameListUrl = data.next ? data.next : null;
            const games = data.results;

            if (games.length === 0) {
                gameList.innerHTML = '<div class="col-12"><p>Nenhum jogo encontrado. Tente outro termo de pesquisa.</p></div>';
            } else {
                games.forEach(game => {
                    const gameItemEl = `
                    <div class="col-lg-3 col-md-6 col-sm-12">
                        <div class="item">
                            <img src="${game.background_image || 'https://via.placeholder.com/300x200?text=No+Image'}" alt="${game.name} image">
                            <h4 class="game-name"><a href="info.html?id=${game.id}" class="text-decoration-none">${game.name}</a><br><span class="platforms">${getPlatformStr(game.parent_platforms)}</span></h4>
                            <ul>
                                <li><i class="fa fa-star"></i> <span class="rating">${game.rating || 'N/A'}</span></li>
                                <li><i class="fa-regular fa-calendar"></i> <span class="date">${game.released || 'Data desconhecida'}</span></li>
                            </ul>
                        </div>
                    </div>
                    `;
                    gameList.insertAdjacentHTML("beforeend", gameItemEl);
                });
            }
            
            loaderEl.classList.add("loaded");
            if (nextGameListUrl) {
                loadMoreGamesBtn.classList.remove("hidden");
            } else {
                loadMoreGamesBtn.classList.add("hidden");
            }
        })
        .catch(error => {
            console.log("An error occurred:", error);
            gameList.innerHTML = '<div class="col-12"><p>Ocorreu um erro ao carregar os jogos.</p></div>';
            loaderEl.classList.add("loaded");
        });
}

function searchGames() {
    currentSearchTerm = searchInput.value.trim();
    
    if (currentSearchTerm === '') {
        // Se a pesquisa estiver vazia, carrega os jogos populares novamente
        loadGames(baseUrl);
        return;
    }
    
    const searchUrl = `https://api.rawg.io/api/games?key=515e12bfda4d446db7e7f4175b687895&search=${encodeURIComponent(currentSearchTerm)}&search_exact=true`;
    loadGames(searchUrl);
}

// Event Listeners
loadMoreGamesBtn.addEventListener("click", () => {
    if (nextGameListUrl) {
        loadGames(nextGameListUrl);
    }
});

searchButton.addEventListener("click", searchGames);
searchInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
        searchGames();
    }
});

// Load initial games
loadGames(baseUrl);