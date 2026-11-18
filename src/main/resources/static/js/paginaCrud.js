import { api } from './api.js';
import { escaparHtml, esconderMensagem, mostrarErro, mostrarMensagem } from './ui.js';

/**
 * Controla uma tela de CRUD composta por um formulário de cadastro/edição e uma tabela de listagem.
 * Cada página informa apenas o que é específico do seu recurso (colunas e mapeamento do formulário).
 */
export class PaginaCrud {

    constructor({
        recurso,
        nomeRegistro,
        nomeFeminino = false,
        colunas,
        lerFormulario,
        preencherFormulario,
        descreverRegistro,
        aoLimparFormulario = () => {},
        aoAlterarDados = async () => {},
    }) {
        this.recurso = recurso;
        this.nomeRegistro = nomeRegistro;
        this.sufixoGenero = nomeFeminino ? 'a' : 'o';
        this.colunas = colunas;
        this.lerFormulario = lerFormulario;
        this.preencherFormulario = preencherFormulario;
        this.descreverRegistro = descreverRegistro;
        this.aoLimparFormulario = aoLimparFormulario;
        this.aoAlterarDados = aoAlterarDados;

        this.formulario = document.getElementById('formulario');
        this.tituloFormulario = document.getElementById('tituloFormulario');
        this.corpoTabela = document.querySelector('#tabela tbody');
        this.mensagem = document.getElementById('mensagem');
        this.campoBusca = document.getElementById('campoBusca');
        this.idEmEdicao = null;
    }

    async iniciar() {
        this.formulario.addEventListener('submit', (evento) => this.salvar(evento));
        document.getElementById('btnCancelar').addEventListener('click', () => this.limparFormulario());
        this.campoBusca?.addEventListener('input', () => this.agendarBusca());
        this.limparFormulario();
        await this.recarregar();
    }

    async recarregar() {
        try {
            const termoBusca = this.campoBusca?.value.trim();
            const consulta = termoBusca ? `?busca=${encodeURIComponent(termoBusca)}` : '';
            this.renderizar(await api.get(this.recurso + consulta));
        } catch (erro) {
            mostrarErro(this.mensagem, erro);
        }
    }

    agendarBusca() {
        clearTimeout(this.temporizadorBusca);
        this.temporizadorBusca = setTimeout(() => this.recarregar(), 300);
    }

    renderizar(registros) {
        if (registros.length === 0) {
            this.corpoTabela.innerHTML =
                `<tr><td class="tabelaVazia" colspan="${this.colunas.length + 1}">Nenhum registro encontrado.</td></tr>`;
            return;
        }
        this.corpoTabela.innerHTML = '';
        registros.forEach((registro) => this.corpoTabela.appendChild(this.criarLinha(registro)));
    }

    criarLinha(registro) {
        const linha = document.createElement('tr');
        linha.dataset.id = registro.id;
        linha.innerHTML = this.colunas
            .map(({ valor, html = false }) => `<td>${html ? valor(registro) : escaparHtml(String(valor(registro)))}</td>`)
            .join('');

        const celulaAcoes = document.createElement('td');
        celulaAcoes.className = 'colunaAcoes';
        celulaAcoes.append(
            this.criarBotao('Editar', 'botaoSecundario btnEditar', () => this.editar(registro)),
            this.criarBotao('Excluir', 'botaoPerigo btnExcluir', () => this.excluir(registro)),
        );
        linha.appendChild(celulaAcoes);
        return linha;
    }

    criarBotao(texto, classes, aoClicar) {
        const botao = document.createElement('button');
        botao.type = 'button';
        botao.className = `botao botaoPequeno ${classes}`;
        botao.textContent = texto;
        botao.addEventListener('click', aoClicar);
        return botao;
    }

    async salvar(evento) {
        evento.preventDefault();
        const dados = this.lerFormulario();
        const emEdicao = this.idEmEdicao !== null;
        try {
            if (emEdicao) {
                await api.put(`${this.recurso}/${this.idEmEdicao}`, dados);
            } else {
                await api.post(this.recurso, dados);
            }
            const acao = emEdicao ? 'atualizad' : 'cadastrad';
            mostrarMensagem(this.mensagem, `${this.nomeRegistro} ${acao}${this.sufixoGenero} com sucesso!`);
            this.limparFormulario();
            await this.aoAlterarDados();
            await this.recarregar();
        } catch (erro) {
            mostrarErro(this.mensagem, erro);
        }
    }

    editar(registro) {
        esconderMensagem(this.mensagem);
        this.idEmEdicao = registro.id;
        this.tituloFormulario.textContent = `Editar ${this.nomeRegistro.toLowerCase()}`;
        this.preencherFormulario(registro);
        this.formulario.querySelector('input, select')?.focus();
    }

    async excluir(registro) {
        if (!window.confirm(`Deseja realmente excluir ${this.descreverRegistro(registro)}?`)) {
            return;
        }
        try {
            await api.delete(`${this.recurso}/${registro.id}`);
            mostrarMensagem(this.mensagem, `${this.nomeRegistro} excluíd${this.sufixoGenero} com sucesso!`);
            if (this.idEmEdicao === registro.id) {
                this.limparFormulario();
            }
            await this.aoAlterarDados();
            await this.recarregar();
        } catch (erro) {
            mostrarErro(this.mensagem, erro);
        }
    }

    limparFormulario() {
        this.idEmEdicao = null;
        this.formulario.reset();
        this.tituloFormulario.textContent = `Nov${this.sufixoGenero} ${this.nomeRegistro.toLowerCase()}`;
        this.aoLimparFormulario();
    }
}
