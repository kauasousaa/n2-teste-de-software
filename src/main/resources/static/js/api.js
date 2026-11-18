import { limparSessao, obterToken } from './sessao.js';

const urlBase = '/api';
const rotaLogin = '/auth/login';

export class ApiError extends Error {
    constructor(status, mensagem, detalhes = []) {
        super(mensagem);
        this.status = status;
        this.detalhes = detalhes;
    }
}

async function requisitar(metodo, caminho, corpo) {
    const cabecalhos = { 'Content-Type': 'application/json' };
    const token = obterToken();
    if (token) {
        cabecalhos.Authorization = `Bearer ${token}`;
    }

    const resposta = await fetch(urlBase + caminho, {
        method: metodo,
        headers: cabecalhos,
        body: corpo === undefined ? undefined : JSON.stringify(corpo),
    });

    if (resposta.status === 401 && caminho !== rotaLogin) {
        limparSessao();
        window.location.href = '/index.html?sessaoExpirada=true';
    }

    if (resposta.status === 204) {
        return null;
    }

    const dados = await resposta.json().catch(() => null);
    if (!resposta.ok) {
        throw new ApiError(resposta.status, dados?.mensagem ?? 'Erro inesperado na comunicação com o servidor.',
            dados?.detalhes ?? []);
    }
    return dados;
}

export const api = {
    get: (caminho) => requisitar('GET', caminho),
    post: (caminho, corpo) => requisitar('POST', caminho, corpo),
    put: (caminho, corpo) => requisitar('PUT', caminho, corpo),
    delete: (caminho) => requisitar('DELETE', caminho),
};
