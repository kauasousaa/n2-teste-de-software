import { api } from './api.js';
import { possuiSessao, salvarSessao } from './sessao.js';
import { mostrarErro, mostrarMensagem } from './ui.js';

const paginaInicial = '/livros.html';

const formulario = document.getElementById('formLogin');
const mensagem = document.getElementById('mensagem');
const botaoEntrar = document.getElementById('btnEntrar');

if (possuiSessao()) {
    window.location.href = paginaInicial;
}

if (new URLSearchParams(window.location.search).has('sessaoExpirada')) {
    mostrarMensagem(mensagem, 'Sua sessão expirou. Faça login novamente.', 'erro');
}

formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const credenciais = {
        email: document.getElementById('email').value.trim(),
        senha: document.getElementById('senha').value,
    };

    botaoEntrar.disabled = true;
    try {
        salvarSessao(await api.post('/auth/login', credenciais));
        window.location.href = paginaInicial;
    } catch (erro) {
        mostrarErro(mensagem, erro);
    } finally {
        botaoEntrar.disabled = false;
    }
});
