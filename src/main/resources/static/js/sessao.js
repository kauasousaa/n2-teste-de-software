const chaveToken = 'livraria.token';
const chaveUsuario = 'livraria.usuario';

export function salvarSessao({ token, usuario }) {
    localStorage.setItem(chaveToken, token);
    localStorage.setItem(chaveUsuario, JSON.stringify(usuario));
}

export function obterToken() {
    return localStorage.getItem(chaveToken);
}

export function obterUsuarioLogado() {
    const usuario = localStorage.getItem(chaveUsuario);
    return usuario ? JSON.parse(usuario) : null;
}

export function possuiSessao() {
    return Boolean(obterToken());
}

export function limparSessao() {
    localStorage.removeItem(chaveToken);
    localStorage.removeItem(chaveUsuario);
}
