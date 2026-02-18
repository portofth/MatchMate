class MobileNavbar {
    constructor(mobileMenu, navList, navLinks) {
        this.mobileMenu = document.querySelector(mobileMenu);
        this.navList = document.querySelector(navList);
        this.navLinks = document.querySelectorAll(navLinks);
        this.activeClass = "active";

        // Verifique se os elementos foram atribuídos
        console.log("mobileMenu:", this.mobileMenu);
        console.log("navList:", this.navList);
        console.log("navLinks:", this.navLinks);

        if (this.mobileMenu && this.navList && this.navLinks.length > 0) {
            this.handleClick = this.handleClick.bind(this);
        } else {
            console.error("Elementos não encontrados. Verifique os seletores.");
            return; // Retorna caso os elementos não sejam encontrados
        }
    }

    animateLinks() {
        this.navLinks.forEach((link, index) => {
            link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
        });
    }

    handleClick() {
        this.navList.classList.toggle(this.activeClass);
        this.mobileMenu.classList.toggle(this.activeClass);
        this.animateLinks();
    }

    addClickEvent() {
        this.mobileMenu.addEventListener("click", this.handleClick);
    }

    init() {
        if (this.mobileMenu && this.navList && this.navLinks.length > 0) {
            this.addClickEvent();
        }
        return this;
    }
}

// Aguarde até o DOM estar carregado antes de instanciar MobileNavbar
document.addEventListener("DOMContentLoaded", () => {
    const mobileNavbar = new MobileNavbar(
        ".mobile-menu",
        ".nav-list",
        ".nav-list li"
    );
    mobileNavbar.init();
});
