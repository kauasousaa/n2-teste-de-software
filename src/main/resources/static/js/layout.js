import { api } from './api.js';
import { limparSessao, obterUsuarioLogado, possuiSessao } from './sessao.js';
import { escaparHtml } from './ui.js';

const paginas = [
    { chave: 'livros', titulo: 'Livros', endereco: '/livros.html' },
    { chave: 'vendas', titulo: 'Vendas', endereco: '/vendas.html' },
    { chave: 'usuarios', titulo: 'Usuários', endereco: '/usuarios.html' },
];

export function iniciarPaginaProtegida(paginaAtiva) {
    if (!possuiSessao()) {
        window.location.href = '/index.html';
        return null;
    }
    const usuario = obterUsuarioLogado();
    renderizarCabecalho(paginaAtiva, usuario);
    return usuario;
}

function renderizarCabecalho(paginaAtiva, usuario) {
    const links = paginas
        .map(({ chave, titulo, endereco }) =>
            `<a id="menu-${chave}" href="${endereco}" class="${chave === paginaAtiva ? 'ativo' : ''}">${titulo}</a>`)
        .join('');

    document.getElementById('cabecalho').innerHTML = `
        <div class="marca">📚 Livraria Páginas</div>
        <nav class="menu">${links}</nav>
        <div class="areaUsuario">
            <span>Olá, <strong id="usuarioLogado">${escaparHtml(usuario?.nome)}</strong></span>
            <button id="btnSair" class="botao botaoClaro botaoPequeno" type="button">Sair</button>
        </div>`;

    document.getElementById('btnSair').addEventListener('click', sair);
}

async function sair() {
    try {
        await api.post('/auth/logout');
    } finally {
        limparSessao();
        window.location.href = '/index.html';
    }
}
