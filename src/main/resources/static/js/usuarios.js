import { api } from './api.js';
import { iniciarPaginaProtegida } from './layout.js';
import { PaginaCrud } from './paginaCrud.js';
import { formatarData, preencherSelect } from './ui.js';

const campo = (id) => document.getElementById(id);

function definirModoSenha(emEdicao) {
    campo('senha').required = !emEdicao;
    campo('senha').placeholder = emEdicao ? 'Deixe em branco para manter a atual' : 'Mínimo de 6 caracteres';
}

function renderizarSituacao(usuario) {
    return usuario.ativo
        ? '<span class="selo">Ativo</span>'
        : '<span class="selo seloInativo">Inativo</span>';
}

async function iniciar() {
    if (!iniciarPaginaProtegida('usuarios')) {
        return;
    }
    preencherSelect(campo('perfil'), await api.get('/catalogos/perfis-usuario'), 'Selecione');

    const pagina = new PaginaCrud({
        recurso: '/usuarios',
        nomeRegistro: 'Usuário',
        colunas: [
            { valor: (usuario) => usuario.nome },
            { valor: (usuario) => usuario.email },
            { valor: (usuario) => usuario.perfilDescricao },
            { valor: renderizarSituacao, html: true },
            { valor: (usuario) => formatarData(usuario.dataCadastro) },
        ],
        lerFormulario: () => ({
            nome: campo('nome').value.trim(),
            email: campo('email').value.trim(),
            senha: campo('senha').value,
            perfil: campo('perfil').value || null,
            ativo: campo('ativo').checked,
        }),
        preencherFormulario: (usuario) => {
            campo('nome').value = usuario.nome;
            campo('email').value = usuario.email;
            campo('senha').value = '';
            campo('perfil').value = usuario.perfil;
            campo('ativo').checked = usuario.ativo;
            definirModoSenha(true);
        },
        descreverRegistro: (usuario) => `o usuário "${usuario.nome}"`,
        aoLimparFormulario: () => definirModoSenha(false),
    });
    await pagina.iniciar();
}

iniciar();
